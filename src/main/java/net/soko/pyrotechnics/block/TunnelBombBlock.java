package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TunnelBombBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private static final VoxelShape SHAPE_NORTH = Block.box(5, 4, 13, 11, 12, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(5, 4, 0, 11, 12, 3);
    private static final VoxelShape SHAPE_WEST = Block.box(13, 4, 5, 16, 12, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 5, 3, 12, 11);
    private static final VoxelShape CEILING_Z = Block.box(5, 13, 4, 11, 16, 12);
    private static final VoxelShape CEILING_X = Block.box(4, 13, 5, 12, 16, 11);
    private static final VoxelShape FLOOR_Z = Block.box(5, 0, 4, 11, 3, 12);
    private static final VoxelShape FLOOR_X = Block.box(4, 0, 5, 12, 3, 11);
    private final ExplosionDamageCalculator damageCalculator;
    private static Explosion explosion;


    public TunnelBombBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(FACE, AttachFace.WALL));
        this.damageCalculator = new ExplosionDamageCalculator();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pState.getValue(LIT)) {
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
                pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
                explode(pLevel, pPos);
                pLevel.playSound(null, pPos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
                Item item = playerHeldItem.getItem();
                pPlayer.awardStat(Stats.ITEM_USED.get(item));
                return InteractionResult.SUCCESS;
            }

        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    private void explode(Level pLevel, BlockPos pPos) {
        List<BlockPos> affectedBlocks = getAffectedBlocks(pLevel, pPos);
        explosion = new Explosion(pLevel, null, pPos.getX(), pPos.getY(), pPos.getZ(), 1f, false, Explosion.BlockInteraction.DESTROY, affectedBlocks);
        explosion.explode();
        explosion.finalizeExplosion(true);
        spawnParticles(affectedBlocks, pLevel);

    }

    private List<BlockPos> getAffectedBlocks(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        Direction direction = switch (blockState.getValue(FACE)) {
            case FLOOR -> Direction.UP.getOpposite();
            case WALL -> level.getBlockState(blockPos).getValue(FACING).getOpposite();
            case CEILING -> Direction.DOWN.getOpposite();
        };
        List<BlockPos> affectedBlocks = new ArrayList<>();
        float decreaseFactor = 0.1F;
        int randomDistance = 16 - level.random.nextInt(6);
        for (int i = 0; i < randomDistance; i++) {
            blockPos = blockPos.relative(direction);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        float f = 1 * (0.7F + level.random.nextFloat() * 0.6F);
                        BlockPos blockPosWithOffset = blockPos.offset(x, y, z);
                        BlockState affectedBlockState = level.getBlockState(blockPosWithOffset);
                        FluidState fluidState = level.getFluidState(blockPosWithOffset);
                        if (!level.isInWorldBounds(blockPosWithOffset)) {
                            break;
                        }
                        if (!affectedBlockState.isAir() || !fluidState.isEmpty()) {
                            float resistance = Math.max(affectedBlockState.getExplosionResistance(level, blockPosWithOffset, explosion), fluidState.getExplosionResistance(level, blockPosWithOffset, explosion));
                            f -= (resistance + 0.3F) * decreaseFactor;
                            if (f < 0) {
                                decreaseFactor += Math.cbrt(resistance);
                            } else {
                                affectedBlocks.add(blockPosWithOffset);
                            }
                        }
                    }
                }
            }
        }
        return affectedBlocks;
    }

    /**
     * Spawns particles along the path of the explosion,
     * particles are randomly spawned at the center of each 3x3x3 cube and at a delay the further away from the start of the explosion.
     *
     * @param affectedBlocks
     */
    private void spawnParticles(List<BlockPos> affectedBlocks, Level level) {
        for (BlockPos affectedBlock : affectedBlocks) {
            if (level.getBlockState(affectedBlock).isAir()) {
                if (level.random.nextFloat() > 0.95f) {
                    level.addParticle(ParticleTypes.EXPLOSION, affectedBlock.getX() + 0.5, affectedBlock.getY() + 0.5, affectedBlock.getZ() + 0.5, 0, 0, 0);
                    level.playSound(null, affectedBlock, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5f, 0.7f);
                }
            }
        }
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos
            blockPos, @NotNull CollisionContext context) {
        Direction direction = blockState.getValue(FACING);
        switch (blockState.getValue(FACE)) {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X) {
                    return FLOOR_X;
                } else {
                    return FLOOR_Z;
                }
            case WALL:
                switch (direction) {
                    case EAST:
                        return SHAPE_EAST;
                    case WEST:
                        return SHAPE_WEST;
                    case SOUTH:
                        return SHAPE_SOUTH;
                    case NORTH:
                    default:
                        return SHAPE_NORTH;
                }
            case CEILING:
                if (direction.getAxis() == Direction.Axis.X) {
                    return CEILING_X;
                } else {
                    return CEILING_Z;
                }
            default:
                throw new IncompatibleClassChangeError("Invalid face: " + blockState.getValue(FACE));
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT, FACE);
    }
}
