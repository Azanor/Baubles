package baubles.api;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Azanor
 */
public class BaublesApi 
{
	
	/**
	 * Retrieves the baubles inventory for the supplied player
	 */
	public static IBaublesItemHandler getBaubles(EntityPlayer player)
	{
		return player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
	}
		
}
