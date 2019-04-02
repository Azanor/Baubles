package baubles.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {
    abstract void encode(Packet packet, PacketBuffer buf);
    abstract Packet decode(PacketBuffer buf);

    void handlePacket(Packet packet, Supplier<NetworkEvent.Context> context) {
        final NetworkEvent.Context ctx = context.get();

        if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            ctx.enqueueWork(() -> packet.server(ctx.getSender()));
            ctx.setPacketHandled(true);
        } else
            Minecraft.getInstance().addScheduledTask(() -> packet.client(clientPlayer()));
    }

    void client(EntityPlayerSP player) {}
    void server(EntityPlayerMP player) {}

    @OnlyIn(Dist.CLIENT)
    private EntityPlayerSP clientPlayer() {
        return Minecraft.getInstance().player;
    }
}