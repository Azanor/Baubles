package baubles.common.network;

import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;

public class EventHandlerNetwork {
	@SubscribeEvent
	public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent e) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			PlayerHandler.clearPlayerBaubles(e.player);
			PlayerHandler.loadPlayerBaubles(e.player);
			for (int a = 0; a < 4; a++)
				PlayerHandler.getPlayerBaubles(e.player).syncSlotToClients(a);
		}
	}

	@SubscribeEvent
	public void playerUpdateEvent(PlayerEvent e) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			PlayerHandler.savePlayerBaubles(e.player);
	}
}