package baubles.common;

import java.io.File;

import baubles.common.items.ItemRing;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Config {
	
	public static Configuration config;
	public static Item itemRing;
	public static boolean renderBaubles=true;
		
	public static void initialize(File file)
    {
		config = new Configuration(file);
        config.load();

        itemRing =(new ItemRing()).setUnlocalizedName("Ring");
		GameRegistry.registerItem(itemRing, "Ring");
		
		load();
		
		MinecraftForge.EVENT_BUS.register(ConfigChangeListener.class);
    }
	
	public static void load() {
		String desc = "Set this to false to disable rendering of baubles in the player.";
		renderBaubles = config.getBoolean("baubleRender.enabled", 
				Configuration.CATEGORY_CLIENT, renderBaubles, desc);
		
		if(config.hasChanged())	config.save();
	}

	
	public static void save()
    {
        config.save();
    }	
	
	public static class ConfigChangeListener {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if(eventArgs.getModID().equals(Baubles.MODID))
				load();
		}
	}
	
}
