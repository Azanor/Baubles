package baubles.common;

import java.io.File;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import baubles.common.items.ItemRing;

public class Config {
	
	public static Configuration config;
	public static Item itemRing;
		
	public static void initialize(File file)
    {
		config = new Configuration(file);
        config.load();

        itemRing =(new ItemRing()).setUnlocalizedName("Ring");
		GameRegistry.registerItem(itemRing, "Ring");

		//save it
		config.save();
    }

	
	public static void save()
    {
        config.save();
    }	
		
	public static void initRecipe() {	}
	
}
