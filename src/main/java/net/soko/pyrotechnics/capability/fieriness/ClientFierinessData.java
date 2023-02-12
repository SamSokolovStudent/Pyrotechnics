package net.soko.pyrotechnics.capability.fieriness;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.ChunkPos;

public class ClientFierinessData {
    private static Object2IntMap<ChunkPos> fierinessMap = new Object2IntOpenHashMap<>();
    private static Object2IntMap<ChunkPos> clientFierinessMap = new Object2IntOpenHashMap<>();

    public static void setChunkFieriness(int fieriness, ChunkPos chunkPos) {
        fierinessMap.put(chunkPos, fieriness);
    }

    public static int getChunkFieriness(ChunkPos chunkPos) {
        return fierinessMap.getInt(chunkPos);
    }

    public void blurFieriness() {
    }
}
