package net.soko.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.soko.pyrotechnics.block.entity.CharredGrassBlockEntity;
import net.soko.pyrotechnics.capability.fieriness.FierinessManager;
import net.soko.pyrotechnics.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CharredGrassBlock extends BaseEntityBlock {
    public static final BooleanProperty SMOLDERING = BooleanProperty.create("smoldering");
    public static final BooleanProperty IS_SOURCE = BooleanProperty.create("is_source");
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CharredGrassBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SMOLDERING, false).setValue(IS_SOURCE, false).setValue(LIT, false));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player pPlayer, InteractionHand interactionHand, BlockHitResult hitResult) {
        ItemStack playerHeldItem = pPlayer.getItemInHand(interactionHand);
        if (playerHeldItem.is(Tags.Items.TOOLS_SHOVELS)) {
            if (!pPlayer.isCreative()) {
                playerHeldItem.hurtAndBreak(1, pPlayer, (player) -> player.broadcastBreakEvent(interactionHand));
            }
            List<BlockPos> blocksToReplace = affectedArea(playerHeldItem.getItem(), blockPos, pPlayer);
            level.playSound(null, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 0.8f);
            for (BlockPos replacePos : blocksToReplace) {
                if (!level.getBlockState(replacePos).is(ModBlocks.CHARRED_GRASS_BLOCK.get())) {
                    continue;
                }
                level.setBlockAndUpdate(replacePos, Blocks.DIRT.defaultBlockState());
                BlockPos aboveReplace = replacePos.above();
                if (level.getBlockState(aboveReplace).is(ModBlocks.BURNT_GRASS.get()) || level.getBlockState(aboveReplace).is(ModBlocks.BURNT_PLANT.get())) {
                    level.setBlockAndUpdate(aboveReplace, Blocks.AIR.defaultBlockState());
                }
                if (level.random.nextFloat() > 0.7f) {
                    double d0 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemEntity itementity = new ItemEntity(level, (double) blockPos.getX() + d0, (double) blockPos.getY() + d1, (double) blockPos.getZ() + d2, new ItemStack(ModItems.ASH.get()));
                    itementity.setDefaultPickUpDelay();
                    level.addFreshEntity(itementity);
                    Item item = playerHeldItem.getItem();
                    pPlayer.awardStat(Stats.ITEM_USED.get(item));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(blockState, level, blockPos, pPlayer, interactionHand, hitResult);
    }


    public List<BlockPos> affectedArea(Item item, BlockPos pos, Player player) {
        List<BlockPos> blockPosList = new ArrayList<>();
        /*
        Wood/Default: Affect 1 block
        Stone: Affect 3 blocks in a straight line
        Iron: Affect 5 blocks in a straight line
        Gold/Diamond: Affect 3x3 area (9 corresponding blocks) in front of player
        Netherite: Affect 6*3 area (18 corresponding blocks)
         */
        Direction direction = player.getDirection();
        if (item.equals(Items.STONE_SHOVEL)) {
            for (int i = 0; i <= 3; i++) {
                blockPosList.add(pos.relative(direction, i));
            }
        } else if (item.equals(Items.IRON_SHOVEL)) {
            for (int i = 0; i <= 5; i++) {
                blockPosList.add(pos.relative(direction, i));
            }
        } else if (item.equals(Items.GOLDEN_SHOVEL) || item.equals(Items.DIAMOND_SHOVEL)) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    blockPosList.add(pos.relative(direction, 1).offset(i, 0, j));
                }
            }
        } else if (item.equals(Items.NETHERITE_SHOVEL)) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (direction == Direction.NORTH || direction == Direction.SOUTH)
                        blockPosList.add(pos.relative(direction, 2).offset(j, 0, i));
                    else if (direction == Direction.EAST || direction == Direction.WEST)
                        blockPosList.add(pos.relative(direction, 2).offset(i, 0, j));
                }
            }
        } else {
            blockPosList.add(pos);
        }
        return blockPosList;
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
