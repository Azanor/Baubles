package baubles.common.network;

import java.util.HashMap;

import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;

public class EventHandlerNetwork {
	
	@SubscribeEvent    
	public void playerLoggedInEvent (PlayerEvent.PlayerLoggedInEvent event)    {    
		Side side = FMLCommonHandler.instance().getEffectiveSide();        
		if (side == Side.SERVER) {
			PlayerHandler.loadPlayerBaubles(event.player);
		}
	}
	
	HashMap<Integer,Long> lastPlayerUpdate = new HashMap<Integer,Long>();
	
	@SubscribeEvent    
	public void playerUpdateEvent (PlayerEvent event)    {  
		Side side = FMLCommonHandler.instance().getEffectiveSide();        
		if (side == Side.SERVER) {
			PlayerHandler.updatePlayerBaubles(event.player);
		}
	}
	
}
