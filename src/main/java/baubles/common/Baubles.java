package baubles.common; 

import java.nio.file.Paths;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.client.ClientProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import baubles.api.cap.BaublesCapabilities.CapabilityBaubles;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.event.CommandBaubles;
import baubles.common.network.PacketHandler;

@Mod(Baubles.MODID)
public class Baubles {
	public static final String MODID = "baubles";

	public static CommonProxy proxy;

	public static final Logger log = LogManager.getLogger(MODID.toUpperCase());
	public static final int GUI = 0;

	public Baubles() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		MinecraftForge.EVENT_BUS.addListener(this::serverLoad);
		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	}

	private void preInit(FMLCommonSetupEvent event) {
		Config.initialize(Paths.get("config", MODID + ".toml"));

		CapabilityManager.INSTANCE.register(IBaublesItemHandler.class,
				new CapabilityBaubles<>(), () -> new BaublesContainer(null));

		CapabilityManager.INSTANCE
				.register(IBauble.class, new BaublesCapabilities.CapabilityItemBaubleStorage(), () -> new IBauble() {
					@Override
					public BaubleType getBaubleType() {
						return BaubleType.TRINKET;
					}
				});

		proxy.registerEventHandlers();
		PacketHandler.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.init();
	}

	private void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandBaubles());
	}
}
