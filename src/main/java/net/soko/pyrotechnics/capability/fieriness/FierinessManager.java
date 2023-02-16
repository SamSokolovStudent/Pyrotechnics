package net.soko.pyrotechnics.capability.fieriness;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;
import net.soko.pyrotechnics.math.MathTools;
import net.soko.pyrotechnics.networking.ModMessages;
import net.soko.pyrotechnics.networking.packet.PacketSyncFierinessS2C;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FierinessManager extends SavedData {

    private final Map<ChunkPos, Fieriness> fierinessMap = new HashMap<>();
    private int counter = 0;


    public static @NotNull FierinessManager get(@NotNull Level level) {
        if (level.isClientSide) {
            throw new IllegalStateException("Cannot access FierinessManager on client side");
        }
        DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();
        return storage.computeIfAbsent(FierinessManager::new, FierinessManager::new, "fieriness");
    }

    public float getFieriness(ChunkPos pos) {
        Fieriness fieriness = fierinessMap.get(pos);
        if (fieriness == null) {
            return 0;
        } else {
            return fieriness.getFieriness();
        }
    }


    public void increaseFieriness(BlockPos pos, float amount) {
        fierinessMap.compute(new ChunkPos(pos), (chunkPos, fieriness) -> {
            if (fieriness == null) {
                fieriness = new Fieriness(0);
            }
            fieriness.addFieriness(amount);
            setDirty();
            return fieriness;
        });
    }

    public void decreaseFieriness(BlockPos pos, float amount) {
        increaseFieriness(pos, -amount);
    }

    public void tick(Level level) {
        boolean wasChanged = false;
        counter++;

        if (counter % 20 != 0) {
            return;
        }

        for (var fierinessEntry : fierinessMap.entrySet()) {
            float fieriness = fierinessEntry.getValue().getFieriness();
            fierinessEntry.getValue().decay();

            if (fieriness != fierinessEntry.getValue().getFieriness()) {
                setDirty();
                wasChanged = true;
            }

        }

        if (!wasChanged) {
            return;
        }

        for (Player player : level.players()) {
            BlockPos playerPos = player.blockPosition();
            ChunkPos chunkPos = new ChunkPos(playerPos);
            float[][] fierinessMatrix = new float[5][5];
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    fierinessMatrix[x + 2][z + 2] = getFieriness(new ChunkPos(chunkPos.x + x, chunkPos.z + z));
                }
            }

            float sum = MathTools.convolveMatrixGaussian(fierinessMatrix);
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PacketSyncFierinessS2C(sum));
        }
    }


    public FierinessManager() {
        System.out.println("Creating new FierinessManager");
    }

    public FierinessManager(@NotNull CompoundTag tag) {
        System.out.println("Creating FierinessManager from data");
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
            chunkTag.putFloat("fieriness", fieriness.getFieriness());
            list.add(chunkTag);
        });
        tag.put("fieriness", list);
        return tag;
    }

}
