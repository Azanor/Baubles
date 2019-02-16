package baubles.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

public class PacketOpenBaublesInventory {
	public static void encode(PacketOpenBaublesInventory msg, PacketBuffer buf) {}

	public static PacketOpenBaublesInventory decode(PacketBuffer buf) {
		return new PacketOpenBaublesInventory();
	}

	public static void handle(PacketOpenBaublesInventory message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ctx.get().getSender().closeContainer();
			NetworkHooks.openGui(ctx.get().getSender(), new BaublesInteractionObject(), null);
		});
		ctx.get().setPacketHandled(true);
	}

	public PacketOpenBaublesInventory() {}
}
