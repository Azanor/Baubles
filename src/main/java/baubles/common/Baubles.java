package baubles.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import baubles.common.config.Config;
import baubles.common.config.ConfigItems;
import baubles.common.config.ConfigRecipes;
import baubles.common.lib.event.EventHandlerEntity;
import baubles.common.network.EventHandlerNetwork;
import baubles.common.network.PacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Baubles.modid, name = Baubles.modid, version = Baubles.version, dependencies="required-after:Forge@[10.12.1.1110,);")
public class Baubles {
	public static final String modid = "Baubles", version = "1.0.0.16";

	@SidedProxy(clientSide = "baubles.client.ClientProxy", serverSide = "baubles.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(modid)
	public static Baubles instance;

	public EventHandlerNetwork networkEventHandler;
	public EventHandlerEntity entityEventHandler;

	public static final Logger logger = LogManager.getLogger(modid);
	public static final int GUI = 0;

	public static CreativeTabs tabBaubles = new CreativeTabs(modid) {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return ConfigItems.itemRing;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		Config.init(e.getSuggestedConfigurationFile());

		PacketHandler.init();

		entityEventHandler = new EventHandlerEntity();
		MinecraftForge.EVENT_BUS.register(entityEventHandler);

		FMLCommonHandler.instance().bus().register(new EventHandlerNetwork());
		proxy.registerHandlers();
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		ConfigItems.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.registerKeyBindings();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		ConfigRecipes.init();
	}
}