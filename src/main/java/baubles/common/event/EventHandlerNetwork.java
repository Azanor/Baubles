package baubles.common.event;

import net.minecraft.entity.player.EntityPlayer;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;

public class EventHandlerNetwork {
	
	@SubscribeEvent    
	public void playerLoggedInEvent (PlayerEvent.PlayerLoggedInEvent event)    {    
		Side side = FMLCommonHandler.instance().getEffectiveSide();        
		if (side == Side.SERVER)        {
			syncBaubles(event.player);
		}
	}
	
	public static void syncBaubles(EntityPlayer player) {
		for (int a = 0; a < 4; a++) {
			PlayerHandler.getPlayerBaubles(player).syncSlotToClients(a);
		}
	}
	
	
}
