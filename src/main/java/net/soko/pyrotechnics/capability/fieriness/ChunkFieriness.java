package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.nbt.CompoundTag;

public class ChunkFieriness {
    private int fieriness;
    private final int minFieriness = 0;
    private final int maxFieriness = 100;

    public int getFieriness() {
        return fieriness;
    }

    public void addFieriness(int amount) {
        this.fieriness = Math.min(fieriness + amount, maxFieriness);
    }

    public void subtractFieriness(int amount) {
        this.fieriness = Math.max(fieriness - amount, minFieriness);
    }

    public void copyFrom(ChunkFieriness source) {
        this.fieriness = source.fieriness;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("fieriness", fieriness);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.fieriness = nbt.getInt("fieriness");
    }
}
