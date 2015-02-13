package baubles.common;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import baubles.common.event.EventHandlerEntity;
import baubles.common.event.EventHandlerNetwork;
import baubles.common.network.PacketHandler;

@Mod(modid = Baubles.MODID, name = Baubles.MODNAME, version = Baubles.VERSION, dependencies="required-after:Forge@[11.14,);")
public class Baubles {
	
	public static final String MODID = "Baubles";
	public static final String MODNAME = "Baubles";
	public static final String VERSION = "1.1.0.0";

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
	public void serverPreStart(FMLServerAboutToStartEvent event) {
		Config.loadBaublesID();
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppedEvent event) {
		Config.saveBaublesID();
	}
	
	public File getPlayerFile(String suffix, File playerDirectory, String playername)
    {
        if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
        return new File(playerDirectory, playername+"."+suffix);
    }
	
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
		FMLCommonHandler.instance().bus().register(entityEventNetwork);
		proxy.registerHandlers();

		/////////////////////

		Config.save();
		
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Config.itemRing, 0, new ModelResourceLocation(Baubles.MODID + ":ring", "inventory"));
		ModelBakery.addVariantName(Config.itemRing, Baubles.MODID + ":ring");
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
  		proxy.registerKeyBindings();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		Config.initRecipe();
	}
		
}
