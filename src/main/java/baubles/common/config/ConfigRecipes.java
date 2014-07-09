package baubles.common.config;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class ConfigRecipes {
	public static void init() {
		initRecipes();
	}

	public static void initRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(ConfigItems.itemRing), new Object[] {
			"PIP", "IPI", "PIP", Character.valueOf('I'),
			new ItemStack(Items.iron_ingot), Character.valueOf('P'),
			new ItemStack(Items.potionitem, 1, 8226) });
	}
}