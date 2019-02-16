package baubles.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenNormalInventory {
	public static void encode(PacketOpenNormalInventory msg, PacketBuffer buf) {}

	public static PacketOpenNormalInventory decode(PacketBuffer buf) {
		return new PacketOpenNormalInventory();
	}

	public static void handle(PacketOpenNormalInventory message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ctx.get().getSender().closeContainer());
		ctx.get().setPacketHandled(true);
	}

	public PacketOpenNormalInventory() {}
}
