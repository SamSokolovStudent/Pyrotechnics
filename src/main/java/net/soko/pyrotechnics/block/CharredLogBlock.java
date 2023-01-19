package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class CharredLogBlock extends RotatedPillarBlock {
    public static final BooleanProperty SMOLDERING = BooleanProperty.create("smoldering");
    public static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;

    public CharredLogBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SMOLDERING, false).setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        if (pState.getValue(SMOLDERING)) {
            // lava particle if smoldering
            pLevel.addParticle(ParticleTypes.LAVA, (double) pPos.getX() + 0.5D, (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.5D, (double) (pLevel.random.nextFloat() / 2.0F), 5.0E-5D, (double) (pLevel.random.nextFloat() / 2.0F));
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        if (pState.getValue(SMOLDERING)) {
            return true;
        }
        return super.isRandomlyTicking(pState);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(6) > 1) {
            if (pLevel.isEmptyBlock(pPos.above())) {
                if (pRandom.nextFloat() > 0.6f) {
                    // do nothing to keep the top block smoldering for longer, making more particles spawn
                } else {
                    pLevel.setBlock(pPos, pState.setValue(SMOLDERING, false), Block.UPDATE_ALL);
                }
                }
            } else {
            pLevel.setBlock(pPos, pState.setValue(SMOLDERING, false), Block.UPDATE_ALL);
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = mutablePos.setWithOffset(pPos, direction);
            BlockState neighbourState = pLevel.getBlockState(neighbourPos);
            if (neighbourState.is(BlockTags.LOGS_THAT_BURN) || neighbourState.is(BlockTags.LEAVES)) {
                for (Direction neighbourDirection : Direction.values()) {
                    BlockPos neighbourNeighbourPos = mutablePos.setWithOffset(neighbourPos, neighbourDirection);
                    if (BaseFireBlock.canBePlacedAt(pLevel, neighbourNeighbourPos, neighbourDirection.getOpposite())) {
                        pLevel.setBlock(neighbourNeighbourPos, BaseFireBlock.getState(pLevel, neighbourNeighbourPos), 3);
                    }
                }
            } else if (neighbourState.is(BlockTags.REPLACEABLE_PLANTS) || neighbourState.is(BlockTags.FLOWERS) || pLevel.getBlockState(neighbourPos).is(Blocks.TALL_GRASS) || pLevel.getBlockState(neighbourPos).is(Blocks.LARGE_FERN)) {
                if (pLevel.getBlockState(neighbourPos).is(Blocks.GRASS)) {
                    pLevel.setBlock(neighbourPos, ModBlocks.BURNT_GRASS.get().defaultBlockState(), 3);
                } else if (pLevel.getBlockState(neighbourPos).is(Blocks.VINE)) {
                    pLevel.setBlock(neighbourPos, Blocks.AIR.defaultBlockState(), 3);
                    if (BaseFireBlock.canBePlacedAt(pLevel, neighbourPos, direction.getOpposite())) {
                        pLevel.setBlock(neighbourPos, BaseFireBlock.getState(pLevel, neighbourPos), 3);
                    }
                } else {
                    pLevel.setBlock(neighbourPos, ModBlocks.BURNT_PLANT.get().defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(SMOLDERING)) {
            BlockPos above = pPos.above();
            if (pLevel.isEmptyBlock(above)) {
                RandomSource randomSource = pLevel.random;
                pLevel.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, (double)pPos.getX() + 0.5D + randomSource.nextDouble() / 3.0D * (double)(randomSource.nextBoolean() ? 1 : -1), (double)pPos.getY() + randomSource.nextDouble() + randomSource.nextDouble(), (double)pPos.getZ() + 0.5D + randomSource.nextDouble() / 3.0D * (double)(randomSource.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
                pLevel.addParticle(ParticleTypes.SMOKE, (double)pPos.getX() + 0.5D + randomSource.nextDouble() / 4.0D * (double)(randomSource.nextBoolean() ? 1 : -1), (double)pPos.getY() + 0.4D, (double)pPos.getZ() + 0.5D + randomSource.nextDouble() / 4.0D * (double)(randomSource.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
            }
        }
        super.animateTick(pState, pLevel, pPos, pRandom);
    }




    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (pState.getValue(SMOLDERING)) {
            if (pEntity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) pEntity)) {
                pEntity.hurt(DamageSource.HOT_FLOOR, 1.0F);
            }
        }
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SMOLDERING, AXIS);
    }
}