package baubles.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenBaublesInventory {
	public static void encode(PacketOpenBaublesInventory msg, PacketBuffer buf) {}

	public static PacketOpenBaublesInventory decode(PacketBuffer buf) {
		return new PacketOpenBaublesInventory();
	}

	public static void handle(PacketOpenBaublesInventory message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ctx.get().getSender().openContainer.onContainerClosed(ctx.get().getSender());
			// todo 1.13 ctx.get().getSender().openGui(Baubles.instance, Baubles.GUI, ctx.getServerHandler().player.world, 0, 0, 0);
		});
	}
	private PacketOpenBaublesInventory() {}
}
