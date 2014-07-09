package baubles.api;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import baubles.common.Baubles;

/**
 * @author Azanor
 */
public class BaublesApi {
	static Method getBaubles;

	/**
	 * Retrieves the baubles inventory for the supplied player
	 */
	public static IInventory getBaubles(EntityPlayer player) {
		IInventory ot = null;
		try {
			if (getBaubles == null)
				getBaubles = Class.forName("baubles.common.lib.PlayerHandler").getMethod("getPlayerBaubles", EntityPlayer.class);
			ot = (IInventory) getBaubles.invoke(null, player);
		} catch (Exception e) {
			Baubles.log.warn("Could not invoke baubles.common.lib.PlayerHandler method getPlayerBaubles", e);
		}
		return ot;
	}
}