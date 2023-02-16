package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.util.Mth;

public class ClientFierinessData {
    private static float targetFieriness;
    private static float currentFieriness;
    private static final float MAX_FIERINESS = 100;


    public static void setFieriness(float fieriness) {
        targetFieriness = fieriness;
    }

    public static void update() {
        currentFieriness = Mth.lerp(0.01f, currentFieriness, targetFieriness);
    }

    public static float getFierinessPercentage() {
        return Math.min(currentFieriness / MAX_FIERINESS, 1);
    }

    public static float updateWithPartial(float partialTick) {
        return Mth.lerp(partialTick, currentFieriness, targetFieriness);
    }

}
