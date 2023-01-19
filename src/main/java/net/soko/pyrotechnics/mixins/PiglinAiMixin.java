package net.soko.pyrotechnics.mixins;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.soko.pyrotechnics.data.advancements.ModCriteria;
import net.soko.pyrotechnics.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {

    @Shadow
    private static void throwItems(Piglin pPilgin, List<ItemStack> pStacks) {
    }

    @Shadow
    private static List<ItemStack> getBarterResponseItems(Piglin pPiglin) {
        return null;
    }


    @Inject(method = "stopHoldingOffHandItem", at = @At("HEAD"), cancellable = true)
    private static void pyrotechnics$onStopHoldingOffHandItem(Piglin pPiglin, boolean pShouldBarter, CallbackInfo ci) {
        Brain<Piglin> brain = pPiglin.getBrain();
        ItemStack itemstack = pPiglin.getItemInHand(InteractionHand.OFF_HAND);
        if (pPiglin.isAdult()) {
            boolean flag = itemstack.is(ModItems.RAW_PYRITE.get());
            if (pShouldBarter && flag) {
                // Chance for barter to fail
                if (pPiglin.getRandom().nextFloat() < 0.5) {
                    throwItems(pPiglin, getBarterResponseItems(pPiglin));
                    pPiglin.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                } else {
                    pPiglin.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                    brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).ifPresent(
                            player -> {
                                brain.setMemory(MemoryModuleType.ANGRY_AT, player.getUUID());
                                PiglinAi.angerNearbyPiglins(player, true);
                                pPiglin.setAggressive(true);
                                if (player instanceof ServerPlayer serverPlayer) {
                                    ModCriteria.BARTER_PYRITE.trigger(serverPlayer);
                                }
                            });
                }
                ci.cancel();
            }
        }
    }
}
