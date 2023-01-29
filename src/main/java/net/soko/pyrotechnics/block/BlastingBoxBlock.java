package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static net.soko.pyrotechnics.block.GunpowderFuseBlock.FUSE_STATE;

public class BlastingBoxBlock extends HorizontalDirectionalBlock {

    private static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    private static final VoxelShape BOX = Block.box(3, 0, 3, 13, 14, 13);
    private static final VoxelShape ROD = Block.box(7, 14, 4, 9, 23, 6);
    private static final VoxelShape HANDLE = Block.box(3, 23, 4, 13, 25, 6);
    private static final VoxelShape ROD2 = Block.box(7, 14, 10, 9, 23, 12);
    private static final VoxelShape HANDLE2 = Block.box(3, 23, 10, 13, 25, 12);
    private static final VoxelShape ROD3 = Block.box(4, 14, 7, 6, 23, 9);
    private static final VoxelShape HANDLE3 = Block.box(4, 23, 3, 6, 25, 13);
    private static final VoxelShape ROD4 = Block.box(10, 14, 7, 12, 23, 9);
    private static final VoxelShape HANDLE4 = Block.box(10, 23, 3, 12, 25, 13);

    private static final VoxelShape ROD_DOWN = Block.box(7, 14, 4, 9, 16, 6);
    private static final VoxelShape HANDLE_DOWN = Block.box(3, 16, 4, 13, 18, 6);
    private static final VoxelShape ROD2_DOWN = Block.box(7, 14, 10, 9, 16, 12);
    private static final VoxelShape HANDLE2_DOWN = Block.box(3, 16, 10, 13, 18, 12);
    private static final VoxelShape ROD3_DOWN = Block.box(4, 14, 7, 6, 16, 9);
    private static final VoxelShape HANDLE3_DOWN = Block.box(4, 16, 3, 6, 18, 13);
    private static final VoxelShape ROD4_DOWN = Block.box(10, 14, 7, 12, 16, 9);
    private static final VoxelShape HANDLE4_DOWN = Block.box(10, 16, 3, 12, 18, 13);

    private static final VoxelShape SHAPE_NORTH = Shapes.or(BOX, ROD, HANDLE);
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(BOX, ROD2, HANDLE2);
    private static final VoxelShape SHAPE_WEST = Shapes.or(BOX, ROD3, HANDLE3);
    private static final VoxelShape SHAPE_EAST = Shapes.or(BOX, ROD4, HANDLE4);

    private static final VoxelShape SHAPE_NORTH_DOWN = Shapes.or(BOX, ROD_DOWN, HANDLE_DOWN);
    private static final VoxelShape SHAPE_SOUTH_DOWN = Shapes.or(BOX, ROD2_DOWN, HANDLE2_DOWN);
    private static final VoxelShape SHAPE_WEST_DOWN = Shapes.or(BOX, ROD3_DOWN, HANDLE3_DOWN);
    private static final VoxelShape SHAPE_EAST_DOWN = Shapes.or(BOX, ROD4_DOWN, HANDLE4_DOWN);


    public BlastingBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        // Toggles between activated and deactivated
        level.setBlock(blockPos, blockState.cycle(ACTIVATED), Block.UPDATE_ALL);
        float f = blockState.getValue(ACTIVATED) ? 0.5F : 0.6F;
        level.playSound(null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(blockState, level, blockPos, newState, isMoving);
        if (blockState.is(newState.getBlock())) {
            if (!blockState.getValue(ACTIVATED) && newState.getValue(ACTIVATED)) {
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    blockpos$mutableblockpos.setWithOffset(blockPos, direction);
                    BlockState neighbourBlockState = level.getBlockState(blockpos$mutableblockpos);
                    if (neighbourBlockState.getBlock() instanceof GunpowderFuseBlock) {
                        FuseState fuseState = neighbourBlockState.getValue(FUSE_STATE);
                        if (fuseState == FuseState.UNIGNITED) {
                            level.setBlockAndUpdate(blockpos$mutableblockpos, neighbourBlockState.setValue(FUSE_STATE, FuseState.IGNITED));
                            level.scheduleTick(blockpos$mutableblockpos, neighbourBlockState.getBlock(), 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState, @NotNull Entity entity) {
        if (!blockState.getValue(ACTIVATED)) {
            level.setBlock(blockPos, blockState.setValue(ACTIVATED, true), Block.UPDATE_ALL);
            level.playSound(null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }


    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState blockState, @NotNull LevelReader level, @NotNull BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return this.canSurviveOn(level, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter pReader, BlockPos pPos, @NotNull BlockState blockState) {
        return blockState.isFaceSturdy(pReader, pPos, Direction.UP);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (blockState.getValue(ACTIVATED)) {
            return getVoxelShape(blockState, SHAPE_SOUTH_DOWN, SHAPE_WEST_DOWN, SHAPE_EAST_DOWN, SHAPE_NORTH_DOWN);
        } else {
            return getVoxelShape(blockState, SHAPE_SOUTH, SHAPE_WEST, SHAPE_EAST, SHAPE_NORTH);
        }
    }

    @NotNull
    private VoxelShape getVoxelShape(@NotNull BlockState blockState, VoxelShape shapeSouth, VoxelShape shapeWest, VoxelShape shapeEast, VoxelShape shapeNorth) {
        return switch (blockState.getValue(FACING)) {
            case SOUTH -> shapeSouth;
            case WEST -> shapeWest;
            case EAST -> shapeEast;
            default -> shapeNorth;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ACTIVATED, FACING);
    }
}

