package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkFierinessProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<ChunkFieriness> FIERINESS_CAPABILITY = CapabilityManager.get(new CapabilityToken<ChunkFieriness>() {
    });

    private ChunkFieriness fieriness = null;
    private final LazyOptional<ChunkFieriness> fierinessOptional = LazyOptional.of(this::createChunkFieriness);

    private ChunkFieriness createChunkFieriness() {
        if (this.fieriness == null) {
            this.fieriness = new ChunkFieriness();
        }
        return this.fieriness;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == FIERINESS_CAPABILITY) {
            return fierinessOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createChunkFieriness().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createChunkFieriness().loadNBTData(nbt);
    }
}
