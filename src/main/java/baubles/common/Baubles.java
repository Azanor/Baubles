package baubles.common; 

import java.io.File;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.common.event.EventHandlerItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import baubles.api.cap.BaublesCapabilities.CapabilityBaubles;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.event.CommandBaubles;
import baubles.common.event.EventHandlerEntity;
import baubles.common.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
		modid = Baubles.MODID, 
		name = Baubles.MODNAME, 
		version = Baubles.VERSION, 
		guiFactory = "baubles.client.gui.BaublesGuiFactory")
public class Baubles {
	
	public static final String MODID = "baubles";
	public static final String MODNAME = "Baubles";
	public static final String VERSION = "1.4.6";

	@SidedProxy(clientSide = "baubles.client.ClientProxy", serverSide = "baubles.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(value=Baubles.MODID)
	public static Baubles instance;
	
	public EventHandlerEntity entityEventHandler;
	public EventHandlerItem itemEventHandler;
	public File modDir;
	
	public static final Logger log = LogManager.getLogger(MODID.toUpperCase());
	public static final int GUI = 0;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modDir = event.getModConfigurationDirectory();

		try {
			Config.initialize(event.getSuggestedConfigurationFile());
		} catch (Exception e) {
			Baubles.log.error("BAUBLES has a problem loading it's configuration"); 
		} finally {
			if (Config.config!=null) Config.save();
		}
				
		CapabilityManager.INSTANCE.register(IBaublesItemHandler.class, 
				new CapabilityBaubles<IBaublesItemHandler>(), BaublesContainer.class);

		CapabilityManager.INSTANCE.register(IBauble.class,
				new BaublesCapabilities.CapabilityItemBaubleStorage(), () -> new BaubleItem(BaubleType.TRINKET));
						
		PacketHandler.init();
		
		entityEventHandler = new EventHandlerEntity();
		itemEventHandler = new EventHandlerItem();

		MinecraftForge.EVENT_BUS.register(entityEventHandler);
		MinecraftForge.EVENT_BUS.register(itemEventHandler);


		/////////////////////
		proxy.registerItemModels();
		Config.save();	
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
  		proxy.registerKeyBindings();  	
  		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		
	}
		
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandBaubles());
	}
}
