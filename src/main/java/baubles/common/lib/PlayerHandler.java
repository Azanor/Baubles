package baubles.common.lib;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import baubles.common.container.InventoryBaubles;

public class PlayerHandler {
	private HashMap<String,InventoryBaubles> playerBaubles = new HashMap<String,InventoryBaubles>();
	
	public InventoryBaubles getPlayerBaubles(EntityPlayer player) {
		if (!playerBaubles.containsKey(player.getCommandSenderName())) {
			InventoryBaubles inventory = new InventoryBaubles(player);
			playerBaubles.put(player.getCommandSenderName(), inventory);
		}
		return playerBaubles.get(player.getCommandSenderName());
	}
	
	public void setPlayerBaubles(EntityPlayer player, InventoryBaubles inventory) {
		playerBaubles.put(player.getCommandSenderName(), inventory);
	}
	
	public void updatePlayerBaubles(EntityPlayer player) {
		InventoryBaubles inventory = getPlayerBaubles(player);
		inventory.saveNBT(player);
	}
	
	public void loadPlayerBaubles(EntityPlayer player) {
		NBTTagCompound data = player.getEntityData();        
		if (!data.hasKey("Baubles")) {
			data.setTag("Baubles", new NBTTagCompound());  
		}
		InventoryBaubles inventory = new InventoryBaubles(player);
		inventory.readNBT(player);
		playerBaubles.put(player.getCommandSenderName(), inventory);
		updatePlayerBaubles(player);
	}
}
