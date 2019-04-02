package baubles.common.network;

import baubles.common.Baubles;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel INSTANCE =
            NetworkRegistry.newSimpleChannel(new ResourceLocation(Baubles.MODID.toLowerCase(), "packets"), () -> "2.0", client -> true, server -> true);
    private static int id = 0;

    public static void init() {
        registerPacket(PacketOpenBaublesInventory.class);
        registerPacket(PacketOpenNormalInventory.class);
        registerPacket(PacketSync.class);
    }

    private static void registerPacket(Class<? extends Packet> clazz) {
        try {
            final Packet packet = clazz.newInstance();
            INSTANCE.registerMessage(id, (Class<Packet>)clazz, packet::encode, packet::decode, packet::handlePacket);
            id += 1;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void sendTo(Packet packet, EntityPlayerMP player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
    public static void sendToServer(Packet packet) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), packet);
    }
}
