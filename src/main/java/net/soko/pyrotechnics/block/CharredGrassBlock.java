package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.Mod;
import net.soko.pyrotechnics.block.entity.CharredGrassBlockEntity;
import net.soko.pyrotechnics.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class CharredGrassBlock extends BaseEntityBlock {
    public static final BooleanProperty SMOLDERING = BooleanProperty.create("smoldering");
    public static final BooleanProperty IS_SOURCE = BooleanProperty.create("is_source");
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CharredGrassBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SMOLDERING, false).setValue(IS_SOURCE, false).setValue(LIT, false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockPos above = pPos.above();
        if (pLevel.isEmptyBlock(above)) {
            ItemStack playerHeldItem = pPlayer.getItemInHand(pHand);
            if (playerHeldItem.is(Tags.Items.TOOLS_SHOVELS)) {
                if (!pPlayer.isCreative()) {
                    playerHeldItem.hurtAndBreak(1, pPlayer, (player) -> player.broadcastBreakEvent(pHand));
                }
                pLevel.setBlock(pPos, Blocks.DIRT.defaultBlockState(), 3);
                pLevel.playSound(null, pPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 0.8f);
                if (pLevel.random.nextFloat() > 0.7f) {
                    double d0 = (double) (pLevel.random.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (double) (pLevel.random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (pLevel.random.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemEntity itementity = new ItemEntity(pLevel, (double) pPos.getX() + d0, (double) pPos.getY() + d1, (double) pPos.getZ() + d2, new ItemStack(ModItems.ASH.get()));
                    itementity.setDefaultPickUpDelay();
                    pLevel.addFreshEntity(itementity);
                    Item item = playerHeldItem.getItem();
                    pPlayer.awardStat(Stats.ITEM_USED.get(item));
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            } else if (playerHeldItem.is(Items.FLINT_AND_STEEL) || playerHeldItem.is(Items.FIRE_CHARGE)) {
                if (!pPlayer.isCreative()) {
                    if (playerHeldItem.is(Items.FLINT_AND_STEEL)) {
                        playerHeldItem.hurtAndBreak(1, pPlayer, (player) -> player.broadcastBreakEvent(pHand));
                    } else {
                        playerHeldItem.shrink(1);
                    }
                }
                pLevel.setBlockAndUpdate(pPos, pState.setValue(SMOLDERING, true));
                pLevel.playSound(null, pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, 0.8f);
                Item item = playerHeldItem.getItem();
                pPlayer.awardStat(Stats.ITEM_USED.get(item));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
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
        // check if block above is grass block or fern
        if (pLevel.getBlockState(pPos.above()).is(BlockTags.REPLACEABLE_PLANTS)) {
            if (pLevel.getBlockState(pPos.above()).is(Blocks.GRASS)) {
                pLevel.setBlockAndUpdate(pPos.above(), ModBlocks.BURNT_GRASS.get().defaultBlockState());
            } else if (pLevel.getBlockState(pPos.above()).is(Blocks.FERN) || pLevel.getBlockState(pPos.above()).is(BlockTags.FLOWERS)) {
                pLevel.setBlockAndUpdate(pPos.above(), ModBlocks.BURNT_PLANT.get().defaultBlockState());
            }
            }
        if (pRandom.nextInt(7) > 1) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SMOLDERING, false).setValue(LIT, false));
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(SMOLDERING)) {
            pState.setValue(LIT, true);
            BlockPos above = pPos.above();
            pLevel.addParticle(ParticleTypes.SMALL_FLAME, (double) above.getX() + pRandom.nextDouble(), (double) above.getY() + 0.1, (double) above.getZ() + pRandom.nextDouble(), 0.0D, 0.0D, 0.0D);
            super.animateTick(pState, pLevel, pPos, pRandom);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SMOLDERING, IS_SOURCE, LIT);

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(IS_SOURCE)) {
            return new CharredGrassBlockEntity(pPos, pState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlocks.CHARRED_GRASS_BLOCK_ENTITY.get(), CharredGrassBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
