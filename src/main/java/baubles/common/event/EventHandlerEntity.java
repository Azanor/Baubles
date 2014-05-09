package baubles.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerEntity  {

	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {
		
		//player events
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			
			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
			for (int a=0;a<baubles.getSizeInventory();a++) {
				ItemStack bauble = baubles.getStackInSlot(a);
				if (bauble!=null && bauble.getItem() instanceof IBauble) {
					((IBauble)bauble.getItem()).onWornTick(bauble, player);
				}
			}
			
		}
		
	}
	
	@SubscribeEvent
	public void playerDeath(LivingDropsEvent event) {
		if (event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer)event.entity;
			
			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
			for (int a=0;a<baubles.getSizeInventory();a++) {
				ItemStack bauble = baubles.getStackInSlot(a);
				if (bauble!=null) {
					drops.add(bauble);
				}
			}
		}
	}
		
		
}
