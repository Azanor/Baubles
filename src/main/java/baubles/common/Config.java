package baubles.common;

import java.io.File;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import baubles.common.items.ItemRing;
import cpw.mods.fml.common.registry.GameRegistry;


public class Config {
	
	public static Configuration config;
	public static Item itemRing;
	
    // config properties
    private static boolean splitSurvivalCreative = false;
	
	public static void initialize(File file)
    {
		config = new Configuration(file);
        config.load();
        
        itemRing =(new ItemRing()).setUnlocalizedName("Ring");
		GameRegistry.registerItem(itemRing, "Ring", Baubles.MODID);        
        
		splitSurvivalCreative = config.getBoolean("splitSurvivalCreative", "server", splitSurvivalCreative, "Split Baubles inventory for survival and creative game modes.");
		
        //save it
		config.save();
    }

	
	public static void save()
    {
        config.save();
    }	
		
	public static void initRecipe() {	
//		GameRegistry.addShapedRecipe(
//				new ItemStack(itemRing), new Object[] {
//					"PIP", "IPI", "PIP", 
//					Character.valueOf('I'), new ItemStack(Items.iron_ingot), 
//					Character.valueOf('P'), new ItemStack(Items.potionitem,1,8226)});
	}
	
    public static boolean isSplitSurvivalCreative() {
        return splitSurvivalCreative;
    }
}
