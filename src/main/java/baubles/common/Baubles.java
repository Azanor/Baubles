package baubles.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;

import baubles.common.config.Config;
import baubles.common.event.EventHandlerEntity;
import baubles.common.network.PacketHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Baubles.MODID, name = Baubles.MODID, version = Baubles.VERSION, dependencies = "required-after:Forge@[10.13.0.1177,);")
public class Baubles {
	public static final String MODID = "Baubles", VERSION = "1.0.1.1";

	@SidedProxy(clientSide = "baubles.client.ClientProxy", serverSide = "baubles.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(MODID)
	public static Baubles instance;

	public EventHandlerEntity entityEventHandler = new EventHandlerEntity();

	public static Logger logger;
	public static final int GUI = 0;

	public static CreativeTabs tabBaubles = new CreativeTabs(MODID) {
		@Override
		public Item getTabIconItem() {
			return Config.itemRing;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();

		Config.preInit(e.getSuggestedConfigurationFile());

		PacketHandler.init();
		MinecraftForge.EVENT_BUS.register(entityEventHandler);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		Config.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.registerKeyBindings();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		Config.postInit();
	}
}