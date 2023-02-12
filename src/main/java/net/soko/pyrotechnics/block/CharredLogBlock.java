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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.soko.pyrotechnics.capability.fieriness.FierinessManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CharredLogBlock extends RotatedPillarBlock {
    public static final BooleanProperty SMOLDERING = BooleanProperty.create("smoldering");
    public static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CharredLogBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SMOLDERING, false).setValue(AXIS, Direction.Axis.Y).setValue(LIT, false));
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos blockPos, BlockState pState, @Nullable BlockEntity pBlockEntity, @NotNull ItemStack itemStack) {
        if (pState.getValue(SMOLDERING) && level instanceof ServerLevel) {
            // lava particle if smoldering
            ((ServerLevel) level).sendParticles(ParticleTypes.LAVA, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
            if (FierinessManager.get(level).getFierinessIncrement(blockPos) > 0)
                FierinessManager.get(level).decreaseFieriness(blockPos, 1);
        }
        super.playerDestroy(level, player, blockPos, pState, pBlockEntity, itemStack);
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
                    pLevel.setBlock(pPos, pState.setValue(SMOLDERING, false).setValue(LIT, false), Block.UPDATE_ALL);
                    if (FierinessManager.get(pLevel).getFierinessIncrement(pPos) > 0)
                        FierinessManager.get(pLevel).decreaseFieriness(pPos, 1);
                }
            }
        } else {
            pLevel.setBlock(pPos, pState.setValue(SMOLDERING, false), Block.UPDATE_ALL);
            if (FierinessManager.get(pLevel).getFierinessIncrement(pPos) > 0)
                FierinessManager.get(pLevel).decreaseFieriness(pPos, 1);
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = mutablePos.setWithOffset(pPos, direction);
            BlockState neighbourState = pLevel.getBlockState(neighbourPos);
            if (neighbourState.is(BlockTags.LOGS_THAT_BURN) || neighbourState.is(BlockTags.LEAVES)) {
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && y == 0 && z == 0) continue;
                            BlockPos neighbourNeighbourPos = mutablePos.set(pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z);
                            if (BaseFireBlock.canBePlacedAt(pLevel, neighbourNeighbourPos, Direction.UP)) {
                                pLevel.setBlockAndUpdate(neighbourNeighbourPos, BaseFireBlock.getState(pLevel, neighbourNeighbourPos));
                            }
                            if (neighbourState.is(BlockTags.REPLACEABLE_PLANTS) || neighbourState.is(BlockTags.FLOWERS) || pLevel.getBlockState(neighbourPos).is(Blocks.TALL_GRASS) || pLevel.getBlockState(neighbourPos).is(Blocks.LARGE_FERN)) {
                                if (pLevel.getBlockState(neighbourPos).is(Blocks.GRASS)) {
                                    pLevel.setBlockAndUpdate(neighbourPos, ModBlocks.BURNT_GRASS.get().defaultBlockState());
                                } else if (pLevel.getBlockState(neighbourPos).is(Blocks.VINE)) {
                                    pLevel.setBlockAndUpdate(neighbourPos, Blocks.AIR.defaultBlockState());
                                    if (BaseFireBlock.canBePlacedAt(pLevel, neighbourPos, direction.getOpposite())) {
                                        pLevel.setBlockAndUpdate(neighbourPos, BaseFireBlock.getState(pLevel, neighbourPos));
                                    }
                                } else {
                                    pLevel.setBlockAndUpdate(neighbourPos, ModBlocks.BURNT_PLANT.get().defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(SMOLDERING)) {
            pState.setValue(LIT, true);
            BlockPos above = pPos.above();
            if (pLevel.isEmptyBlock(above)) {
                RandomSource randomSource = pLevel.random;
                pLevel.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, (double) pPos.getX() + 0.5D + randomSource.nextDouble() / 3.0D * (double) (randomSource.nextBoolean() ? 1 : -1), (double) pPos.getY() + randomSource.nextDouble() + randomSource.nextDouble(), (double) pPos.getZ() + 0.5D + randomSource.nextDouble() / 3.0D * (double) (randomSource.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
                pLevel.addParticle(ParticleTypes.SMOKE, (double) pPos.getX() + 0.5D + randomSource.nextDouble() / 4.0D * (double) (randomSource.nextBoolean() ? 1 : -1), (double) pPos.getY() + 0.4D, (double) pPos.getZ() + 0.5D + randomSource.nextDouble() / 4.0D * (double) (randomSource.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
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
        pBuilder.add(SMOLDERING, AXIS, LIT);
    }
}
