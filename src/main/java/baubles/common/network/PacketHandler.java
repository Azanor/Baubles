package baubles.common.network;

import baubles.common.Baubles;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler
{
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Baubles.MODID, "main_channel"))
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.simpleChannel();

	public static void init()
	{
		INSTANCE.registerMessage(0, PacketOpenBaublesInventory.class, PacketOpenBaublesInventory::encode, PacketOpenBaublesInventory::decode, PacketOpenBaublesInventory::handle);
		INSTANCE.registerMessage(1, PacketOpenNormalInventory.class, PacketOpenNormalInventory::encode, PacketOpenNormalInventory::decode, PacketOpenNormalInventory::handle);
		INSTANCE.registerMessage(2, PacketSync.class, PacketSync::encode, PacketSync::decode, PacketSync::handle);
	}
}
