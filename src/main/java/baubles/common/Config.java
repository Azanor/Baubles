package baubles.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import baubles.common.items.ItemRing;

public class Config {
	
	public static Configuration config;
	public static Item itemRing;
	private static int nextInventoryID = 0;
		
	public static void initialize(File file)
    {
		config = new Configuration(file);
        config.load();

        itemRing =(new ItemRing()).setUnlocalizedName("Ring");
		GameRegistry.registerItem(itemRing, "Ring");

		//save it
		config.save();
    }
	
	public static void loadBaublesID() {
		File file = new File(DimensionManager.getCurrentSaveRootDirectory(), "BaublesCount.baub");
		try{
			if (file.exists() && file != null){
				Baubles.log.info("Reading Baubles ID list");
				FileInputStream fileinputstream = new FileInputStream(file);
				nextInventoryID = fileinputstream.read();
				fileinputstream.close();
				}
			else{
				Baubles.log.info("Creating Baubles ID list");
				saveBaublesID();
			}
		}catch(Exception e) {
			Baubles.log.error("An error ocurred while reading Baubles ID count, resetting", e);
		}
	}
	
	public static void saveBaublesID() {
		File file = new File(DimensionManager.getCurrentSaveRootDirectory(), "BaublesCount.baub");
		try{
			if(file.exists()){
				file.delete();
			}
			FileOutputStream fileoutputstream = new FileOutputStream(file);
			fileoutputstream.write(nextInventoryID);
			fileoutputstream.close();
		}catch(Exception e){
			Baubles.log.fatal("An error ocurred while saving Baubles ID count!", e);
		}
	}
	
	public static void addBaublesID() {
		nextInventoryID++;
		saveBaublesID();
	}
	
	public static int getNewBaublesID() {
		return nextInventoryID;
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
	
}
