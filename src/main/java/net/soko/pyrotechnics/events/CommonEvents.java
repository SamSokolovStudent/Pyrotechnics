package net.soko.pyrotechnics.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.soko.pyrotechnics.block.FireworksBoxBlock;
import net.soko.pyrotechnics.block.entity.FireworksBoxBlockEntity;
import net.soko.pyrotechnics.entity.PyrotechnicDamageSources;
import net.soko.pyrotechnics.entity.PyrotechnicsLargeFireCharge;
import net.soko.pyrotechnics.item.ModItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommonEvents {
    public static void onEntityDrops(LivingDropsEvent event) {
        if (event.getSource().isExplosion()) {
            event.getDrops().removeIf(drop -> drop.getItem().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FireworksBoxBlock);
        }
    }

    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.getSource().isExplosion()) {
            Iterable<ItemStack> inventory;
            if (entity instanceof Player) {
                inventory = ((Player) entity).getInventory().items;
            } else {
                inventory = entity.getAllSlots();
            }
            List<ItemStack> fireworks = new ArrayList<>();
            for (ItemStack itemStack : inventory) {
                if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FireworksBoxBlock) {
                    CompoundTag blockEntityTag = itemStack.getTagElement("BlockEntityTag");
                    if (blockEntityTag == null) {
                        continue;
                    }
                    for (Tag tag : blockEntityTag.getList("Fireworks", Tag.TAG_COMPOUND)) {
                        CompoundTag compoundTag = (CompoundTag) tag;
                        fireworks.add(ItemStack.of(compoundTag));
                    }
                }
            }
            if (!fireworks.isEmpty()) {
                Collections.shuffle(fireworks);
                entity.getCombatTracker().recordDamage(PyrotechnicDamageSources.firework(event.getSource()), entity.getHealth(), Objects.requireNonNull(entity.getCombatTracker().getLastEntry()).getDamage());
                for (int i = 0; i < 64 && !fireworks.isEmpty(); i++) {
                    ItemStack shotFirework = fireworks.remove(0);
                    FireworksBoxBlockEntity.activate(entity.level, entity.blockPosition(), shotFirework.split(1), 0.8, 0.8f);
                    if (!shotFirework.isEmpty()) {
                        if (fireworks.isEmpty()) {
                            fireworks.add(shotFirework);
                        } else {
                            fireworks.add((1 + entity.level.random.nextInt(fireworks.size())) % (fireworks.size() + 1), shotFirework);
                        }
                    }
                }
            }
        }
    }

    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.is(Items.FIRE_CHARGE)) {
            Level level = event.getEntity().getLevel();
            Entity fireball = new SmallFireball(level, event.getEntity(), event.getEntity().getLookAngle().x, event.getEntity().getLookAngle().y, event.getEntity().getLookAngle().z);
            fireball.setPos(event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getEyeHeight(), event.getEntity().getZ());
            level.addFreshEntity(fireball);
            if (!event.getEntity().getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            event.setCanceled(true);
        }
        if (itemStack.is(ModItems.LARGE_FIRE_CHARGE.get())) {
            Level level = event.getEntity().getLevel();
            Entity largeFireball = new PyrotechnicsLargeFireCharge(level, event.getEntity(), event.getEntity().getLookAngle().x, event.getEntity().getLookAngle().y, event.getEntity().getLookAngle().z, 1);
            largeFireball.setPos(event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getEyeHeight(), event.getEntity().getZ());
            level.addFreshEntity(largeFireball);
            if (!event.getEntity().getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            event.setCanceled(true);
        }
    }
}
