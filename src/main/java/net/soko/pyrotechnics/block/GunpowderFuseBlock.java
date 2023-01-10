package net.soko.pyrotechnics.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.minecraft.world.level.block.RedStoneWireBlock.PROPERTY_BY_DIRECTION;

public class GunpowderFuseBlock extends Block {

    public static final EnumProperty<FuseState> FUSE_STATE = EnumProperty.create("fuse_state", FuseState.class);
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    private final BlockState crossState;
    private static final VoxelShape SHAPE_DOT = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
    private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
    private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Shapes.or(SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, Shapes.or(SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, Shapes.or(SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, Shapes.or(SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));

    private static final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();

    public GunpowderFuseBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE));
        this.crossState = this.defaultBlockState().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE);

        for (BlockState blockstate : this.getStateDefinition().getPossibleStates()) {
            if (blockstate.getValue(FUSE_STATE) == FuseState.UNIGNITED) {
                SHAPES_CACHE.put(blockstate, this.calculateShape(blockstate));
            }
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        // Check if block is ignited, if so then set neighboring blocks that are unignited to ignited and schedule current block to be set to burnt
        if (pState.getValue(FUSE_STATE) == FuseState.IGNITED) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(FUSE_STATE, FuseState.BURNT));
            pLevel.scheduleTick(pPos, this, 1);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pState.getValue(PROPERTY_BY_DIRECTION.get(direction)) == RedstoneSide.UP ? pPos.relative(direction).above() : pPos.relative(direction);
                if (!shouldConnectTo(pLevel.getBlockState(blockpos))) {
                    blockpos = blockpos.below();
                }
                BlockState blockstate = pLevel.getBlockState(blockpos);
                if (shouldConnectTo(blockstate)) {
                    if (tryIgnite(blockstate, blockpos, pLevel)) {
                        // Set fuse to burnt state after a short amount of time
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(FUSE_STATE, FuseState.BURNT));
                        pLevel.scheduleTick(pPos, this, 7);
                    }
                }
            }
        } else if (pState.getValue(FUSE_STATE) == FuseState.BURNT) {
            // 75% chance for the block to turn into gunpowder ash
            if (pRandom.nextFloat() < 0.66f) {
                pLevel.setBlock(pPos, ModBlocks.GUNPOWDER_ASH.get().defaultBlockState(), 3);
            } else {
                pLevel.removeBlock(pPos, false);
            }
        }
    }

    public boolean tryIgnite(BlockState blockState, BlockPos blockPos, Level level) {
        if (blockState.is(this) && blockState.getValue(FUSE_STATE) == FuseState.UNIGNITED) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(FUSE_STATE, FuseState.IGNITED));
            level.scheduleTick(blockPos, this, 2);
            spawnParticles(blockState, level, blockPos, level.random);
            return true;
        } else if (blockState.is(ModBlockTags.EXPLODABLE) && blockState.getBlock() instanceof TntBlock tntBlock) {
            tntBlock.onCaughtFire(blockState, level, blockPos, null, null);
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
            return true;
        } else if (blockState.is(ModBlocks.FIREWORKS_BOX.get())) {
            level.setBlock(blockPos, blockState.setValue(FireworksBoxBlock.TRIGGERED, true), 3);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pState.getValue(FUSE_STATE) == FuseState.UNIGNITED) {
            ItemStack playerHeldItem = pPlayer.getItemInHand(pHand);
            if (!playerHeldItem.is(Items.FLINT_AND_STEEL) && !playerHeldItem.is(Items.FIRE_CHARGE)) {
                return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
            } else {
                if (!pPlayer.isCreative()) {
                    if (playerHeldItem.is(Items.FLINT_AND_STEEL)) {
                        playerHeldItem.hurtAndBreak(1, pPlayer, (player) -> player.broadcastBreakEvent(pHand));
                    } else {
                        playerHeldItem.shrink(1);
                    }
                }
                pLevel.setBlockAndUpdate(pPos, pState.setValue(FUSE_STATE, FuseState.IGNITED));
                pLevel.scheduleTick(pPos, this, 1);
                Item item = playerHeldItem.getItem();
                pPlayer.awardStat(Stats.ITEM_USED.get(item));
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    private void spawnParticlesAlongLine(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, Vec3 pParticleVec, Direction pXDirection, Direction pZDirection, float pMin, float pMax) {
        float f = pMax - pMin;
        float f2 = pMin + f * pRandom.nextFloat();
        double d0 = 0.5D + (double) (0.4375F * (float) pXDirection.getStepX()) + (double) (f2 * (float) pZDirection.getStepX());
        double d1 = 0.5D + (double) (0.4375F * (float) pXDirection.getStepY()) + (double) (f2 * (float) pZDirection.getStepY());
        double d2 = 0.5D + (double) (0.4375F * (float) pXDirection.getStepZ()) + (double) (f2 * (float) pZDirection.getStepZ());
        pLevel.sendParticles(ParticleTypes.FLAME, (double) pPos.getX() + d0, (double) pPos.getY() + d1, (double) pPos.getZ() + d2, 2, pParticleVec.x, pParticleVec.y + 0.025, pParticleVec.z, 0.0D);
    }

    public void spawnParticles(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                RedstoneSide redstoneSide = pState.getValue(PROPERTY_BY_DIRECTION.get(direction));
                switch (redstoneSide) {
                    case UP:
                        this.spawnParticlesAlongLine(serverLevel, pRandom, pPos, Vec3.ZERO, direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.spawnParticlesAlongLine(serverLevel, pRandom, pPos, Vec3.ZERO, Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.spawnParticlesAlongLine(serverLevel, pRandom, pPos, Vec3.ZERO, Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
        }
    }


    protected static boolean shouldConnectTo(BlockState pState) {
        if (pState.is(ModBlocks.GUNPOWDER_ASH.get())) {
            return true;
        } else if (pState.is(ModBlockTags.EXPLODABLE)) {
            return true;
        } else if (pState.is(ModBlocks.FIREWORKS_BOX.get())) {
            return true;
        } else {
            return pState.is(ModBlocks.GUNPOWDER_FUSE_BLOCK.get());
        }
    }

    private VoxelShape calculateShape(BlockState pState) {
        VoxelShape voxelshape = SHAPE_DOT;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = pState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneside == RedstoneSide.SIDE) {
                voxelshape = Shapes.or(voxelshape, SHAPES_FLOOR.get(direction));
            } else if (redstoneside == RedstoneSide.UP) {
                voxelshape = Shapes.or(voxelshape, SHAPES_UP.get(direction));
            }
        }

        return voxelshape;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES_CACHE.getOrDefault(pState.setValue(FUSE_STATE, FuseState.UNIGNITED), SHAPE_DOT);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.getConnectionState(pContext.getLevel(), this.crossState, pContext.getClickedPos());
    }

    private BlockState getConnectionState(BlockGetter pLevel, BlockState pState, BlockPos pPos) {
        boolean flag = isDot(pState);
        pState = this.getMissingConnections(pLevel, this.defaultBlockState().setValue(FUSE_STATE, pState.getValue(FUSE_STATE)), pPos);
        if (flag && isDot(pState)) {
            return pState;
        } else {
            boolean flag1 = pState.getValue(NORTH).isConnected();
            boolean flag2 = pState.getValue(SOUTH).isConnected();
            boolean flag3 = pState.getValue(EAST).isConnected();
            boolean flag4 = pState.getValue(WEST).isConnected();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;
            if (!flag4 && flag5) {
                pState = pState.setValue(WEST, RedstoneSide.SIDE);
            }

            if (!flag3 && flag5) {
                pState = pState.setValue(EAST, RedstoneSide.SIDE);
            }

            if (!flag1 && flag6) {
                pState = pState.setValue(NORTH, RedstoneSide.SIDE);
            }

            if (!flag2 && flag6) {
                pState = pState.setValue(SOUTH, RedstoneSide.SIDE);
            }

            return pState;
        }
    }

    private BlockState getMissingConnections(BlockGetter pLevel, BlockState pState, BlockPos pPos) {
        boolean flag = !pLevel.getBlockState(pPos.above()).isRedstoneConductor(pLevel, pPos);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!pState.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected()) {
                RedstoneSide redstoneside = this.getConnectingSide(pLevel, pPos, direction, flag);
                pState = pState.setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneside);
            }
        }

        return pState;
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing == Direction.DOWN) {
            return pState;
        } else if (pFacing == Direction.UP) {
            return this.getConnectionState(pLevel, pState, pCurrentPos);
        } else {
            RedstoneSide redstoneside = this.getConnectingSide(pLevel, pCurrentPos, pFacing);
            return redstoneside.isConnected() == pState.getValue(PROPERTY_BY_DIRECTION.get(pFacing)).isConnected() && !isCross(pState) ? pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), redstoneside) : this.getConnectionState(pLevel, this.crossState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), redstoneside).setValue(FUSE_STATE, pState.getValue(FUSE_STATE)), pCurrentPos);
        }
    }

    private static boolean isCross(BlockState pState) {
        return pState.getValue(NORTH).isConnected() && pState.getValue(SOUTH).isConnected() && pState.getValue(EAST).isConnected() && pState.getValue(WEST).isConnected();
    }

    private static boolean isDot(BlockState pState) {
        return !pState.getValue(NORTH).isConnected() && !pState.getValue(SOUTH).isConnected() && !pState.getValue(EAST).isConnected() && !pState.getValue(WEST).isConnected();
    }

    /**
     * Performs updates on diagonal neighbors of the target position and passes in the flags.
     * The flags are equivalent to {@link net.minecraft.world.level.Level#setBlock}.
     */
    public void updateIndirectNeighbourShapes(BlockState pState, LevelAccessor pLevel, BlockPos pPos, int pFlags, int pRecursionLeft) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = pState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneside != RedstoneSide.NONE && !shouldConnectTo(pLevel.getBlockState(blockpos$mutableblockpos.setWithOffset(pPos, direction)))) {
                blockpos$mutableblockpos.move(Direction.DOWN);
                BlockState blockstate = pLevel.getBlockState(blockpos$mutableblockpos);
                if (shouldConnectTo(blockstate)) {
                    BlockPos blockpos = blockpos$mutableblockpos.relative(direction.getOpposite());
                    pLevel.neighborShapeChanged(direction.getOpposite(), pLevel.getBlockState(blockpos), blockpos$mutableblockpos, blockpos, pFlags, pRecursionLeft);
                }

                blockpos$mutableblockpos.setWithOffset(pPos, direction).move(Direction.UP);
                BlockState blockstate1 = pLevel.getBlockState(blockpos$mutableblockpos);
                if (shouldConnectTo(blockstate1)) {
                    BlockPos blockpos1 = blockpos$mutableblockpos.relative(direction.getOpposite());
                    pLevel.neighborShapeChanged(direction.getOpposite(), pLevel.getBlockState(blockpos1), blockpos$mutableblockpos, blockpos1, pFlags, pRecursionLeft);
                }
            }
        }

    }

    private RedstoneSide getConnectingSide(BlockGetter pLevel, BlockPos pPos, Direction pFace) {
        return this.getConnectingSide(pLevel, pPos, pFace, !pLevel.getBlockState(pPos.above()).isRedstoneConductor(pLevel, pPos));
    }

    private RedstoneSide getConnectingSide(BlockGetter pLevel, BlockPos pPos, Direction pDirection, boolean pNonNormalCubeAbove) {
        BlockPos blockpos = pPos.relative(pDirection);
        BlockState blockstate = pLevel.getBlockState(blockpos);
        if (pNonNormalCubeAbove) {
            boolean flag = this.canSurviveOn(pLevel, blockpos, blockstate);
            if (flag && shouldConnectTo(pLevel.getBlockState(blockpos.above()))) {
                if (blockstate.isFaceSturdy(pLevel, blockpos, pDirection.getOpposite())) {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        if (shouldConnectTo(blockstate)) {
            return RedstoneSide.SIDE;
        } else if (blockstate.isRedstoneConductor(pLevel, blockpos)) {
            return RedstoneSide.NONE;
        } else {
            BlockPos blockPosBelow = blockpos.below();
            return shouldConnectTo(pLevel.getBlockState(blockPosBelow)) ? RedstoneSide.SIDE : RedstoneSide.NONE;
        }
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return this.canSurviveOn(pLevel, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter pReader, BlockPos pPos, BlockState pState) {
        return pState.isFaceSturdy(pReader, pPos, Direction.UP) || pState.is(Blocks.HOPPER);
    }

    /**
     * Calls {@link net.minecraft.world.level.Level#updateNeighborsAt} for all neighboring blocks, but only if the given
     * block is a redstone wire.
     */
    private void checkCornerChangeAt(Level pLevel, BlockPos pPos) {
        if (shouldConnectTo(pLevel.getBlockState(pPos))) {
            pLevel.updateNeighborsAt(pPos, this);

            for (Direction direction : Direction.values()) {
                pLevel.updateNeighborsAt(pPos.relative(direction), this);
            }

        }
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock()) && !pLevel.isClientSide) {
            for (Direction direction : Direction.Plane.VERTICAL) {
                pLevel.updateNeighborsAt(pPos.relative(direction), this);
            }

            this.updateNeighborsOfNeighboringWires(pLevel, pPos);
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pIsMoving && !pState.is(pNewState.getBlock())) {
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            if (!pLevel.isClientSide) {
                for (Direction direction : Direction.values()) {
                    pLevel.updateNeighborsAt(pPos.relative(direction), this);
                }
                this.updateNeighborsOfNeighboringWires(pLevel, pPos);
            }
        }
    }

    private void updateNeighborsOfNeighboringWires(Level pLevel, BlockPos pPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(pLevel, pPos.relative(direction));
        }

        for (Direction direction1 : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pPos.relative(direction1);
            if (pLevel.getBlockState(blockpos).isRedstoneConductor(pLevel, blockpos)) {
                this.checkCornerChangeAt(pLevel, blockpos.above());
            } else {
                this.checkCornerChangeAt(pLevel, blockpos.below());
            }
        }

    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            if (!pState.canSurvive(pLevel, pPos)) {
                dropResources(pState, pLevel, pPos);
                pLevel.removeBlock(pPos, false);
            }

        }
    }


    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#rotate} whenever
     * possible. Implementing/overriding is fine.
     */
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return switch (pRotation) {
            case CLOCKWISE_180 ->
                    pState.setValue(NORTH, pState.getValue(SOUTH)).setValue(EAST, pState.getValue(WEST)).setValue(SOUTH, pState.getValue(NORTH)).setValue(WEST, pState.getValue(EAST));
            case COUNTERCLOCKWISE_90 ->
                    pState.setValue(NORTH, pState.getValue(EAST)).setValue(EAST, pState.getValue(SOUTH)).setValue(SOUTH, pState.getValue(WEST)).setValue(WEST, pState.getValue(NORTH));
            case CLOCKWISE_90 ->
                    pState.setValue(NORTH, pState.getValue(WEST)).setValue(EAST, pState.getValue(NORTH)).setValue(SOUTH, pState.getValue(EAST)).setValue(WEST, pState.getValue(SOUTH));
            default -> pState;
        };
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#mirror} whenever
     * possible. Implementing/overriding is fine.
     */
    public @NotNull BlockState mirror(@NotNull BlockState pState, Mirror pMirror) {
        return switch (pMirror) {
            case LEFT_RIGHT ->
                    pState.setValue(NORTH, pState.getValue(SOUTH)).setValue(SOUTH, pState.getValue(NORTH)).setValue(FUSE_STATE, pState.getValue(FUSE_STATE));
            case FRONT_BACK ->
                    pState.setValue(EAST, pState.getValue(WEST)).setValue(WEST, pState.getValue(EAST)).setValue(FUSE_STATE, pState.getValue(FUSE_STATE));
            default -> super.mirror(pState, pMirror);
        };
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, FUSE_STATE);
    }

}
