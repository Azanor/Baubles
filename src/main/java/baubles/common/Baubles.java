package baubles.common;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import baubles.common.event.EventHandlerEntity;
import baubles.common.event.EventHandlerNetwork;
import baubles.common.network.PacketHandler;

@Mod(
		modid = Baubles.MODID, 
		name = Baubles.MODNAME, 
		version = Baubles.VERSION, 
		dependencies="required-after:Forge@[12.17.0,);")
public class Baubles {
	
	public static final String MODID = "Baubles";
	public static final String MODNAME = "Baubles";
	public static final String VERSION = "1.2.1.0";

	@SidedProxy(clientSide = "baubles.client.ClientProxy", serverSide = "baubles.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(value=Baubles.MODID)
	public static Baubles instance;
	
	public EventHandlerEntity entityEventHandler;
	public EventHandlerNetwork entityEventNetwork;
	public File modDir;
	
	public static final Logger log = LogManager.getLogger("Baubles");
	public static final int GUI = 0;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		event.getModMetadata().version = Baubles.VERSION;
		modDir = event.getModConfigurationDirectory();

		try {
			Config.initialize(event.getSuggestedConfigurationFile());
		} catch (Exception e) {
			Baubles.log.error("BAUBLES has a problem loading it's configuration");
		} finally {
			if (Config.config!=null) Config.save();
		}
		
		PacketHandler.init();
		
		entityEventHandler = new EventHandlerEntity();
		entityEventNetwork = new EventHandlerNetwork();
		
		MinecraftForge.EVENT_BUS.register(entityEventHandler);
		MinecraftForge.EVENT_BUS.register(entityEventNetwork);		

		/////////////////////
		proxy.registerItemModels();
		Config.save();	
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
  		proxy.registerKeyBindings();  		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		Config.initRecipe();
	}
		
}
