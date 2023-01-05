package net.soko.pyrotechnics.block;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class FireworksBoxBlock extends Block {
    public FireworksBoxBlock(Properties properties) {
        super(properties);
    }

    public void activate(Level pLevel, BlockPos above) {
        ItemStack fireworkRocket = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkRocketTag = fireworkRocket.getOrCreateTag();
        CompoundTag fireworksTag = new CompoundTag();
        fireworksTag.putByte("Flight", (byte) -1);
        ListTag explosions = new ListTag();
        CompoundTag explosion = new CompoundTag();
        explosion.putByte("Type", (byte) 4);
        explosion.putBoolean("Flicker", true);
        explosion.putBoolean("Trail", true);
        explosion.putIntArray("Colors", new int[]{0xFF0000, 0xeb624d});
        explosion.putIntArray("FadeColors", new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF});
        explosions.add(explosion);
        fireworksTag.put("Explosions", explosions);
        fireworkRocketTag.put("Fireworks", fireworksTag);
        fireworkRocket.setTag(fireworkRocketTag);

        FireworkRocketEntity firework = new FireworkRocketEntity(pLevel, above.getX() + 0.5, above.getY(), above.getZ() + 0.5, fireworkRocket);
        pLevel.addFreshEntity(firework);
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
                activate(pLevel, above);
                Item item = playerHeldItem.getItem();
                pPlayer.awardStat(Stats.ITEM_USED.get(item));
                return InteractionResult.SUCCESS;
            }

        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
    // Add method that explodes all fireworks in the box at once when the block is blown up
}