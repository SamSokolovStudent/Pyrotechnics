package net.soko.pyrotechnics.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ghast.class)
public class GhastMixin {

    @Inject(method = "isReflectedFireball", at = @At("HEAD"), cancellable = true)
    private static void pyrotechnics$onIsReflectedFireball(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if ("large_fireball".equals(source.msgId)) {
            cir.setReturnValue(true);
        }
    }
}
