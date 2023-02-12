package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;
import net.soko.pyrotechnics.networking.ModMessages;
import net.soko.pyrotechnics.networking.packet.PacketSyncFierinessS2C;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FierinessManager extends SavedData {

    private final Map<ChunkPos, Fieriness> fierinessMap = new HashMap<>();
    private int counter = 0;

    /**
     * Used to access the fieriness manager from the server side.
     * This method will create a new storage to store the fieriness data of a chunk on, on the server side by using ComputeIfAbsent with the constructors of this class.
     */
    public static @NotNull FierinessManager get(@NotNull Level level) {
        if (level.isClientSide) {
            throw new IllegalStateException("Cannot access FierinessManager on client side");
        }
        DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();
        return storage.computeIfAbsent(FierinessManager::new, FierinessManager::new, "fieriness");
    }

    /**
     * Used to get or to generate the fieriness of a chunk, if the chunk is not in the map then it will be added with a fieriness of 0 and thus will always return a value.
     *
     * @param pos The position of the block that is being set on fire, this is used to get the chunk.
     */
    @NotNull
    private Fieriness getFierinessInternal(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return fierinessMap.computeIfAbsent(chunkPos, k -> new Fieriness(0));
    }

    /**
     * Used to get the fieriness of a chunk. This method calls the internal {@link #getFierinessInternal(BlockPos)} method.
     * Which will generate a fieriness if it does not exist, meaning that this method is always safe to call and is public for that reason.
     *
     * @param pos The position of the block that is being set on fire, this is used to get the chunk.
     */
    public int getFieriness(BlockPos pos) {
        return getFierinessInternal(pos).getFieriness();
    }

    /**
     * Used to increase the fieriness of a chunk by a certain amount, this will be smoothed out over time.
     *
     * @param pos    The position of the block that is being set on fire, this is used to get the chunk.
     * @param amount The amount of fieriness to add to the chunk.
     */
    public void increaseFieriness(BlockPos pos, int amount) {
        int fieriness = getFieriness(pos);
        if (fieriness < Fieriness.MAX_FIERINESS) {
            // Increase fieriness increment on the key chunk
            getFierinessInternal(pos).increaseFierinessIncrement(amount);
        }
    }

    /**
     * Used to decrease the fieriness of a chunk by a certain amount, this will be smoothed out over time.
     *
     * @param pos    The position of the block that is being set on fire, this is used to get the chunk.
     * @param amount The amount of fieriness to subtract from the chunk.
     */
    public void decreaseFieriness(BlockPos pos, int amount) {
        int fieriness = getFieriness(pos);
        if (fieriness > Fieriness.MIN_FIERINESS) {
            // Decrease fieriness increment on the key chunk
            getFierinessInternal(pos).decreaseFierinessIncrement(amount);
        }
    }

    public int getFierinessIncrement(BlockPos pos) {
        return getFierinessInternal(pos).getFierinessIncrement();
    }


    public void tick(Level level) {
        boolean wasChanged = false;
        for (var fierinessEntry : fierinessMap.entrySet()) {

            int decrementTickSpeed = level.isRainingAt(fierinessEntry.getKey().getWorldPosition()) ? 4 : 6;
            // every 6 ticks, decrease the fieriness by 1, if it is raining then decrease by 1 every 4 ticks
            if (counter % decrementTickSpeed == 0) {
                if (fierinessEntry.getValue().getFieriness() > 0) {
                    fierinessEntry.getValue().subtractFieriness(1);
                    wasChanged = true;
                }
                setDirty();
            }


            int fieriness = fierinessEntry.getValue().getFieriness();
            int fierinessIncrement = fierinessEntry.getValue().getFierinessIncrement();
            float fierinessRatio = (float) fierinessIncrement / (float) fieriness;
            int incrementTickSpeed = (fierinessRatio > 2) ? 1
                    : (fierinessRatio > 1) ? 2
                    : (fierinessRatio > 0.20) ? 3
                    : 2;


            /* increments the fieriness based on the ratio of fieriness to fieriness increment,
             meaning that if the fierinessIncrement is higher, fieriness will increase faster. */
            if (counter % incrementTickSpeed == 0) {
                if (fierinessIncrement >= 0 && fieriness < Fieriness.MAX_FIERINESS) {
                    fierinessEntry.getValue().addFieriness(1);
                    if (fierinessIncrement > 0) {
                        fierinessEntry.getValue().decreaseFierinessIncrement(1);
                    }
                    wasChanged = true;
                }
                setDirty();
            }


            // if the fieriness changed, send a packet to the client to update the fieriness
            if (wasChanged) {
                ModMessages.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(
                                () -> level.getChunk(fierinessEntry.getKey().x, fierinessEntry.getKey().z)),
                        new PacketSyncFierinessS2C(fierinessEntry.getValue().getFieriness(), fierinessEntry.getKey()));
            }
        }
        counter++;
    }


    public FierinessManager() {
    }

    public FierinessManager(@NotNull CompoundTag tag) {
        ListTag list = tag.getList("fieriness", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag fierinessTag = (CompoundTag) t;
            Fieriness fieriness = new Fieriness(fierinessTag.getInt("fieriness"));
            ChunkPos chunkPos = new ChunkPos(fierinessTag.getInt("x"), fierinessTag.getInt("z"));
            fierinessMap.put(chunkPos, fieriness);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        fierinessMap.forEach((chunkPos, fieriness) -> {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putInt("x", chunkPos.x);
            chunkTag.putInt("z", chunkPos.z);
            chunkTag.putInt("fieriness", fieriness.getFieriness());
            list.add(chunkTag);
        });
        return tag;
    }

}
