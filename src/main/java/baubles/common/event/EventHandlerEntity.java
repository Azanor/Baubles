package baubles.common.event;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import baubles.api.IBauble;
import baubles.common.Baubles;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;

import com.google.common.io.Files;

public class EventHandlerEntity {
	
	static HashSet<Integer> syncSchedule = new HashSet<Integer>();

	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {

		// player events
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			if (!syncSchedule.isEmpty() && syncSchedule.contains(player.getEntityId())) {
				EventHandlerNetwork.syncBaubles(player);
				syncSchedule.remove(player.getEntityId());
			}

			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
			for (int a = 0; a < baubles.getSizeInventory(); a++) {
				if (baubles.getStackInSlot(a) != null
						&& baubles.getStackInSlot(a).getItem() instanceof IBauble) {
					((IBauble) baubles.getStackInSlot(a).getItem()).onWornTick(
							baubles.getStackInSlot(a), player);
				}
			}

		}

	}

	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer
				&& !event.getEntity().worldObj.isRemote
				&& !event.getEntity().worldObj.getGameRules().getBoolean("keepInventory")) {
			PlayerHandler.getPlayerBaubles(event.getEntityPlayer()).dropItemsAt(
					event.getDrops(),event.getEntityPlayer());
		}

	}

	@SubscribeEvent
	public void playerLoad(PlayerEvent.LoadFromFile event) {
		PlayerHandler.clearPlayerBaubles(event.getEntityPlayer());
		
		File file1 = getPlayerFile("baub", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString());
		if (!file1.exists()) {
			File filep = event.getPlayerFile("baub");
			if (filep.exists()) {
				try {
					Files.copy(filep, file1);					
					Baubles.log.info("Using and converting UUID Baubles savefile for "+event.getEntityPlayer().getDisplayNameString());
					filep.delete();
					File fb = event.getPlayerFile("baubback");
					if (fb.exists()) fb.delete();					
				} catch (IOException e) {}
			} else {				 
				File fileq = getLegacy1110FileFromPlayer("baub", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString());
				if (fileq.exists()) {
					try {
						Files.copy(fileq, file1);
						fileq.deleteOnExit();
						Baubles.log.info("Using pre 1.1.1.1 Baubles savefile for "+event.getEntityPlayer().getDisplayNameString());
					} catch (IOException e) {}
				} else {
					File filet = getLegacy1710FileFromPlayer(event.getEntityPlayer());
					if (filet.exists()) {
						try {
							Files.copy(filet, file1);
							filet.deleteOnExit();
							Baubles.log.info("Using pre MC 1.7.10 Baubles savefile for "+event.getEntityPlayer().getDisplayNameString());
						} catch (IOException e) {}
					}
				}				
			}
		}
		
		PlayerHandler.loadPlayerBaubles(event.getEntityPlayer(), file1, getPlayerFile("baubback", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()));
		EventHandlerEntity.syncSchedule.add(event.getEntityPlayer().getEntityId());
	}
	
	public File getPlayerFile(String suffix, File playerDirectory, String playername)
    {
        if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
        return new File(playerDirectory, "_"+playername+"."+suffix);
    }
	
	public static File getLegacy1710FileFromPlayer(EntityPlayer player)
    {
		try {
			File playersDirectory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			return new File(playersDirectory, player.getDisplayNameString() + ".baub");
		} catch (Exception e) { e.printStackTrace(); }
		return null;
    }
	
	public File getLegacy1110FileFromPlayer(String suffix, File playerDirectory, String playername)
    {
		if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
        return new File(playerDirectory, playername+"."+suffix);
    }
	
	

	@SubscribeEvent
	public void playerSave(PlayerEvent.SaveToFile event) {
		PlayerHandler.savePlayerBaubles(event.getEntityPlayer(), 
				getPlayerFile("baub", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()),
				getPlayerFile("baubback", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()));
	}

}
