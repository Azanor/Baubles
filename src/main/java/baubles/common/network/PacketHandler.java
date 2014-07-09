package baubles.common.network;

import baubles.common.Baubles;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Baubles.modid.toLowerCase());

	public static void init() {
		INSTANCE.registerMessage(PacketOpenBaublesInventory.class, PacketOpenBaublesInventory.class, 0, Side.SERVER);
		INSTANCE.registerMessage(PacketSyncBauble.class, PacketSyncBauble.class, 1, Side.CLIENT);
	}
}