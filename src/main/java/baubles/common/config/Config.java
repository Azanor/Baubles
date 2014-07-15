package baubles.common.config;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import baubles.common.Baubles;
import baubles.common.items.ItemRing;
import cpw.mods.fml.common.registry.GameRegistry;

public class Config {
	public static Configuration config;
	public static boolean enableRing;

	public static Item itemRing;

	public static String itemName[] = {
		"Ring"
	};

	public static void preInit(File configFile) {
		config = new Configuration(configFile);

		try {
			config.load();
			enableRing = config.getBoolean("enableRing", config.CATEGORY_GENERAL, true, "Toggles the default ring added by Baubles.");
		} catch (Exception e) {
			Baubles.logger.log(Level.ERROR, "An error has occurred while loading configuration properties!", e);
		} finally {
			if (config.hasChanged())
				config.save();
		}
	}

	public static void init() {
		if (enableRing)
			GameRegistry.registerItem(itemRing = new ItemRing().setCreativeTab(Baubles.tabBaubles).setHasSubtypes(true).setMaxDamage(0).setMaxStackSize(1), itemName[0]);
	}

	public static void postInit() {
		if (enableRing)
			GameRegistry.addShapedRecipe(new ItemStack(itemRing), new Object[] {
				"PIP", "IPI", "PIP", Character.valueOf('I'),
				new ItemStack(Items.iron_ingot), Character.valueOf('P'),
				new ItemStack(Items.potionitem, 1, 8226) });
	}
}