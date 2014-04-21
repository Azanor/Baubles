package baubles.common.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import baubles.common.Baubles;
import baubles.common.container.InventoryBaubles;

public class PlayerHandler {
	private static File playersDirectory;
	
	private static HashMap<String,InventoryBaubles> playerBaubles = new HashMap<String,InventoryBaubles>();
	
	public static InventoryBaubles getPlayerBaubles(EntityPlayer player) {
		if (!playerBaubles.containsKey(player.getCommandSenderName())) {
			InventoryBaubles inventory = new InventoryBaubles(player);
			playerBaubles.put(player.getCommandSenderName(), inventory);
		}
		return playerBaubles.get(player.getCommandSenderName());
	}
	
	public static void setPlayerBaubles(EntityPlayer player, InventoryBaubles inventory) {
		playerBaubles.put(player.getCommandSenderName(), inventory);
	}
	
//	public static void updatePlayerBaubles(EntityPlayer player) {
//		InventoryBaubles inventory = getPlayerBaubles(player);
//		inventory.saveNBT(player);
//	}
	
	public static File getFileFromPlayer(EntityPlayer player)
    {
		if (playersDirectory == null) {
			try {
				playersDirectory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			} catch (Exception e) { e.printStackTrace(); }
		}
        return new File(playersDirectory, player.getCommandSenderName() + ".baub");
    }
	
	public static File getBackupFileFromPlayer(EntityPlayer player)
    {
		if (playersDirectory == null) {
			try {
				playersDirectory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			} catch (Exception e) { e.printStackTrace(); }
		}
        return new File(playersDirectory, player.getCommandSenderName() + ".baubback");
    }
	
	public static void loadPlayerBaubles(EntityPlayer player) {
		try
        {
            File file1 = getFileFromPlayer(player);
            
            if (file1 == null || !file1.exists())
            {
            	Baubles.logger.warn("Baubles inventory not found for "+player.getCommandSenderName()+". Trying to load backup baubles data.");
            	file1 = getBackupFileFromPlayer(player);
            }
            if (file1 != null && file1.exists())
            {
                FileInputStream fileinputstream = new FileInputStream(file1);
                NBTTagCompound data = CompressedStreamTools.readCompressed(fileinputstream);
                fileinputstream.close();
                
                InventoryBaubles inventory = new InventoryBaubles(player);
        		inventory.readNBT(data);
        		playerBaubles.put(player.getCommandSenderName(), inventory);
                
            } else {  //legacy data
            	Baubles.logger.warn("Baubles inventory not found for "+player.getCommandSenderName()+". Trying to load legacy baubles data.");
            	NBTTagCompound data = player.getEntityData();        
        		if (!data.hasKey("Baubles")) {
        			data.setTag("Baubles", new NBTTagCompound());  
        		}
        		InventoryBaubles inventory = new InventoryBaubles(player);
        		inventory.readNBT(player);
        		playerBaubles.put(player.getCommandSenderName(), inventory);
        		savePlayerBaubles(player);
            }
        }
        catch (Exception exception1)
        {
            exception1.printStackTrace();
            Baubles.logger.fatal("Error loading baubles inventory");
        }
				
	}
	
	public static void savePlayerBaubles(EntityPlayer player) {
		try
        {
            File file1 = getFileFromPlayer(player);
            if (file1 != null )
            {
            	InventoryBaubles inventory = getPlayerBaubles(player);
            	NBTTagCompound data = new NBTTagCompound();
        		inventory.saveNBT(data);
            	
            	FileOutputStream fileoutputstream = new FileOutputStream(file1);
                CompressedStreamTools.writeCompressed(data, fileoutputstream);
                fileoutputstream.close();
                
            }
            
            File file2 = getBackupFileFromPlayer(player);
            if (file2 != null )
            {
            	InventoryBaubles inventory = getPlayerBaubles(player);
            	NBTTagCompound data = new NBTTagCompound();
        		inventory.saveNBT(data);
            	
            	FileOutputStream fileoutputstream = new FileOutputStream(file2);
                CompressedStreamTools.writeCompressed(data, fileoutputstream);
                fileoutputstream.close();
                
            }
        }
        catch (Exception exception1)
        {
            exception1.printStackTrace();
            Baubles.logger.fatal("Error saving baubles inventory");
        }
				
	}
}
