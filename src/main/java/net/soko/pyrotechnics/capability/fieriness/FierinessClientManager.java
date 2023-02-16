package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.soko.pyrotechnics.math.MathTools;

import java.util.Objects;
import java.util.Optional;

public class FierinessClientManager {
    public static FierinessClientManager fierinessClientManager;

    public static FierinessClientManager getFierinessClientManager() {
        return Objects.requireNonNull(fierinessClientManager, "FierinessClientManager is not initialized!");
    }

    public static Optional<FierinessClientManager> getFierinessClientManagerOptional() {
        return Optional.ofNullable(fierinessClientManager);
    }

    private final Minecraft mc;
    private final InterpolatedValue undergroundness;
    private final InterpolatedValue currentSkyLight;
    public InterpolatedValue fogStart;
    public InterpolatedValue fogEnd;


    public FierinessClientManager() {
        this.mc = Minecraft.getInstance();
        this.undergroundness = new InterpolatedValue(0.0F, 0.02f);
        this.currentSkyLight = new InterpolatedValue(16.0F);
        this.fogStart = new InterpolatedValue(0.0F);
        this.fogEnd = new InterpolatedValue(1.0F);
    }

    public float getUndergroundFactor(float partialTick) {
        float y = (float) mc.cameraEntity.getY();
        float yFactor = Mth.clamp(MathTools.mapRange(mc.level.getSeaLevel() - 32.0F, mc.level.getSeaLevel() + 32.0F, 1, 0, y), 0.0F, 1.0F);
        return Mth.lerp(yFactor, 1 - this.undergroundness.get(partialTick), this.currentSkyLight.get(partialTick) / 16.0F);
    }

    public void close() {}
}
