package baubles.common.event;

import java.io.File;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.apache.logging.log4j.Level;

import baubles.api.IBauble;
import baubles.common.Baubles;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;

import com.google.common.io.Files;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerEntity {
	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;

			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
			for (int a = 0; a < baubles.getSizeInventory(); a++)
				if (baubles.getStackInSlot(a) != null && baubles.getStackInSlot(a).getItem() instanceof IBauble)
					((IBauble) baubles.getStackInSlot(a).getItem()).onWornTick(baubles.getStackInSlot(a), player);
		}
	}

	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote && !event.entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
			PlayerHandler.getPlayerBaubles(event.entityPlayer).dropItems(event.drops);
		}
	}

	@SubscribeEvent
	public void playerLoad(PlayerEvent.LoadFromFile event) {
		PlayerHandler.clearPlayerBaubles(event.entityPlayer);

		File file1 = event.getPlayerFile("baub");
		if (!file1.exists()) {
			File filet = getLegacyFileFromPlayer(event.entityPlayer);
			if (filet.exists()) {
				try {
					Files.copy(filet, file1);
					Baubles.logger.info("Using pre MC 1.7.10 Baubles savefile for " + event.entityPlayer.getCommandSenderName());
				} catch (IOException e) {
					Baubles.logger.log(Level.ERROR, "Error loading data!", e);
				}
			}
		}

		PlayerHandler.loadPlayerBaubles(event.entityPlayer, file1, event.getPlayerFile("baubback"));

		for (int a = 0; a < 4; a++)
			PlayerHandler.getPlayerBaubles(event.entityPlayer).syncSlotToClients(a);
	}

	public static File getLegacyFileFromPlayer(EntityPlayer player) {
		try {
			File playersDirectory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			return new File(playersDirectory, player.getCommandSenderName() + ".baub");
		} catch (Exception e) {
			Baubles.logger.log(Level.ERROR, "Error loading data!", e);
		}
		return null;
	}

	@SubscribeEvent
	public void playerSave(PlayerEvent.SaveToFile event) {
		PlayerHandler.savePlayerBaubles(event.entityPlayer, event.getPlayerFile("baub"), event.getPlayerFile("baubback"));
	}
}