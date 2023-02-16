package net.soko.pyrotechnics.mixins;

import net.minecraft.client.renderer.LevelRenderer;
import net.soko.pyrotechnics.capability.fieriness.FierinessClientManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/client/renderer/RenderBuffers;)V", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        FierinessClientManager.fierinessClientManager = new FierinessClientManager();

    }

    @Inject(method = "close()V", at = @At("TAIL"))
    private void close(CallbackInfo info) {
        FierinessClientManager.getFierinessClientManager().close();
        FierinessClientManager.fierinessClientManager = null;
    }
}
