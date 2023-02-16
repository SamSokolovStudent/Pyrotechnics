package net.soko.pyrotechnics.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.soko.pyrotechnics.capability.fieriness.ClientFierinessData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;
    private static final int SKY_RED = 0x74;
    private static final int SKY_GREEN = 0x10;
    private static final int SKY_BLUE = 0x00;

    private static final int CLOUD_RED = 0x38;
    private static final int CLOUD_GREEN = 0x00;
    private static final int CLOUD_BLUE = 0x00;


    @Inject(method = "getSkyColor", at = @At(value = "RETURN"), cancellable = true)
    public void pyrotechnics$getSkyColor(Vec3 pPos, float pPartialTick, @NotNull CallbackInfoReturnable<Vec3> cir) {
        float percentage = ClientFierinessData.getFierinessPercentage();
        Vec3 skyColor = cir.getReturnValue();
        if (percentage > 0) {
            double linearRed = Mth.lerp(percentage, skyColor.x, SKY_RED/255.0);
            double linearGreen = Mth.lerp(percentage, skyColor.y, SKY_GREEN/255.0);
            double linearBlue = Mth.lerp(percentage, skyColor.z, SKY_BLUE/255.0);
            cir.setReturnValue(new Vec3(linearRed, linearGreen, linearBlue));
        }
    }

    @Inject(method = "getCloudColor", at = @At(value = "RETURN"), cancellable = true)
    public void pyrotechnics$getCloudColor(float pPartialTick, @NotNull CallbackInfoReturnable<Vec3> cir) {
        float percentage = ClientFierinessData.getFierinessPercentage();
        Vec3 cloudColor = cir.getReturnValue();
        if (percentage > 0) {
            double linearRed = Mth.lerp(percentage, cloudColor.x, CLOUD_RED/255.0);
            double linearGreen = Mth.lerp(percentage, cloudColor.y, CLOUD_GREEN/255.0);
            double linearBlue = Mth.lerp(percentage, cloudColor.z, CLOUD_BLUE/255.0);
            cir.setReturnValue(new Vec3(linearRed, linearGreen, linearBlue));
        }
    }
}
