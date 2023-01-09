package net.soko.pyrotechnics.block;


import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.soko.pyrotechnics.block.entity.FireworksBoxBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class FireworksBoxBlock extends BaseEntityBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");
    public static final BooleanProperty FUSE = BooleanProperty.create("fuse");

    public static final VoxelShape SHAPE = Block.box(1, 0, 2, 15, 9, 14);

    public FireworksBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, false).setValue(EMPTY, false).setValue(FUSE, false));
    }

    public void neighborChanged(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Block pBlock, @NotNull BlockPos pFromPos, boolean pIsMoving) {
        boolean powered = pLevel.hasNeighborSignal(pPos);
        boolean triggered = pState.getValue(TRIGGERED);
        if (powered && !triggered && !pState.getValue(EMPTY)) {
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, true), Block.UPDATE_ALL);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        BlockPos above = pPos.above();
        if (pLevel.isEmptyBlock(above)) {
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
                pLevel.setBlock(pPos, pState.setValue(FUSE, true), Block.UPDATE_ALL);
                pLevel.playSound(null, pPos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
                Item item = playerHeldItem.getItem();
                pPlayer.awardStat(Stats.ITEM_USED.get(item));
                return InteractionResult.SUCCESS;
            }

        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof FireworksBoxBlockEntity fireworksBoxBlockEntity) {
                fireworksBoxBlockEntity.exploded();
            }
        }
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        if (!pLevel.isClientSide) {
            if (pProjectile.isOnFire() && pProjectile.mayInteract(pLevel, pHit.getBlockPos())) {
                pLevel.setBlock(pHit.getBlockPos(), pState.setValue(FUSE, true), Block.UPDATE_ALL);
            }
        }
        super.onProjectileHit(pLevel, pState, pHit, pProjectile);
    }

    public boolean dropFromExplosion(@NotNull Explosion pExplosion) {
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(TRIGGERED, EMPTY, FUSE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new FireworksBoxBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModBlocks.FIREWORKS_BOX_BLOCK_ENTITY.get(), FireworksBoxBlockEntity::tick) : null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}