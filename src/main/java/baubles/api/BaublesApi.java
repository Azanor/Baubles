package baubles.api;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.BaublesInventoryWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * @author Azanor
 */
public class BaublesApi
{
	/**
	 * Retrieves the baubles inventory capability handler for the supplied player
	 */
	public static IBaublesItemHandler getBaublesHandler(EntityPlayer player)
	{
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return handler;
	}

	/**
	 * Retrieves the baubles capability handler wrapped as a IInventory for the supplied player
	 */
	@Deprecated
	public static IInventory getBaubles(EntityPlayer player)
	{
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return new BaublesInventoryWrapper(handler, player);
	}
	
	public static boolean isBaubleEquipped(EntityPlayer player, IBauble bauble) {
		IBaublesItemHandler handler = getBaublesHandler(player);
		for (int a=0;a<handler.getSlots();a++) {
			if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem()==bauble) return true;
		}
		return false;
	}
}
