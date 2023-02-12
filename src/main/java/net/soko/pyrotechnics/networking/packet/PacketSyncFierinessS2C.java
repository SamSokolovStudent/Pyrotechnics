package net.soko.pyrotechnics.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;
import net.soko.pyrotechnics.capability.fieriness.ClientFierinessData;

import java.util.function.Supplier;

public class PacketSyncFierinessS2C {
    private final int fieriness;
    private final ChunkPos chunkPos;

    public PacketSyncFierinessS2C(int fieriness, ChunkPos chunkPos) {
        this.fieriness = fieriness;
        this.chunkPos = chunkPos;
    }

    public PacketSyncFierinessS2C(FriendlyByteBuf friendlyByteBuf) {
        this.fieriness = friendlyByteBuf.readInt();
        this.chunkPos = friendlyByteBuf.readChunkPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(fieriness);
        buf.writeChunkPos(chunkPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientFierinessData.setChunkFieriness(fieriness, chunkPos);
        });
        return true;
    }
}
