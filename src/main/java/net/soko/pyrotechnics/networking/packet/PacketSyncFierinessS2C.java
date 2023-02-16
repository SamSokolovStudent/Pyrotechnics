package net.soko.pyrotechnics.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.soko.pyrotechnics.capability.fieriness.ClientFierinessData;

import java.util.function.Supplier;

public class PacketSyncFierinessS2C {
    private final float fieriness;

    public PacketSyncFierinessS2C(float fieriness) {
        this.fieriness = fieriness;
    }

    public PacketSyncFierinessS2C(FriendlyByteBuf friendlyByteBuf) {
        this.fieriness = friendlyByteBuf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(fieriness);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientFierinessData.setFieriness(fieriness);
        });
        return true;
    }
}
