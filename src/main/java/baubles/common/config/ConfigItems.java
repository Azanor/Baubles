package baubles.common.config;

import net.minecraft.item.Item;
import baubles.common.Baubles;
import baubles.common.items.ItemRing;
import cpw.mods.fml.common.registry.GameRegistry;

public class ConfigItems {
	public static Item itemRing;

	public static void init() {
		initItems();
	}

	private static void initItems() {
		GameRegistry.registerItem(itemRing = (new ItemRing()).setUnlocalizedName("Ring"), "Ring", Baubles.modid);
	}
}