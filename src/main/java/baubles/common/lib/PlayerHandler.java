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
			NBTTagCompound data = null;
            boolean save = false;
            File file = getFileFromPlayer(player);
            if (file != null && file.exists())
            {
            	try {
					FileInputStream fileinputstream = new FileInputStream(file);
					data = CompressedStreamTools.readCompressed(fileinputstream);
					fileinputstream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            
            if (file == null || !file.exists() || data == null || data.hasNoTags())
            {
            	Baubles.log.warn("Data not found for "+player.getCommandSenderName()+". Trying to load backup data.");
            	file = getBackupFileFromPlayer(player);
            	if (file != null && file.exists())
                {
            		try {
    					FileInputStream fileinputstream = new FileInputStream(file);
    					data = CompressedStreamTools.readCompressed(fileinputstream);
    					fileinputstream.close();
    					save = true;
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
                } 
            }
            
            if (file == null || !file.exists() || data == null || data.hasNoTags())
            {  //legacy data
            	Baubles.log.warn("Data not found for "+player.getCommandSenderName()+". Trying to load legacy data.");
            	data = player.getEntityData();  
            	save = true;
            }
            
            if (data!=null) {
            	InventoryBaubles inventory = new InventoryBaubles(player);
        		inventory.readNBT(data);
        		playerBaubles.put(player.getCommandSenderName(), inventory);
        		if (save) savePlayerBaubles(player);
            }
        }
        catch (Exception exception1)
        {
            Baubles.log.fatal("Error loading baubles inventory");
            exception1.printStackTrace();
        }
				
	}
	
	public static void savePlayerBaubles(EntityPlayer player) {
		try
        {
			File file2 = getBackupFileFromPlayer(player);
            if (file2 != null && file2.exists())
            {
            	try {
					file2.delete();
				} 
            	catch (Exception e) {
					Baubles.log.error("Could not delete backup file for player "+player.getCommandSenderName());	
				}
            }
            
            File file1 = getFileFromPlayer(player);
            file2 = getBackupFileFromPlayer(player);
            if (file1!=null && file1.exists()) {
            	try {
					file1.renameTo(file2);
				} 
            	catch (Exception e) {
            		Baubles.log.error("Could not backup old baubles file for player "+player.getCommandSenderName());	
				}
            }
        
            file1 = getFileFromPlayer(player);
            try {
				if (file1 != null )
				{
					InventoryBaubles inventory = getPlayerBaubles(player);
					NBTTagCompound data = new NBTTagCompound();
					inventory.saveNBT(data);
					
					FileOutputStream fileoutputstream = new FileOutputStream(file1);
				    CompressedStreamTools.writeCompressed(data, fileoutputstream);
				    fileoutputstream.close();
				    
				}
			} catch (Exception e) {
				Baubles.log.error("Could not save baubles file for player "+player.getCommandSenderName());
				if (file1.exists()) {
					try {
						file1.delete();
					} 
	            	catch (Exception e2) {}
				}
			}
        }
        catch (Exception exception1)
        {
            Baubles.log.fatal("Error saving baubles inventory");
            exception1.printStackTrace();
        }
				
	}
}
