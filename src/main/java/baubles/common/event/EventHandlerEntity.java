package baubles.common.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.BaublesContainerProvider;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class EventHandlerEntity {
	
	private HashMap<UUID,ItemStack[]> baublesSync = new HashMap<UUID,ItemStack[]>();
	
	@SubscribeEvent
	public void cloneCapabilitiesEvent(PlayerEvent.Clone event)
	{		
		try {
			BaublesContainer bco = (BaublesContainer) BaublesApi.getBaublesHandler(event.getOriginal());
			NBTTagCompound nbt = bco.serializeNBT();
			BaublesContainer bcn = (BaublesContainer) BaublesApi.getBaublesHandler(event.getEntityPlayer());
			bcn.deserializeNBT(nbt);
		} catch (Exception e) {
			Baubles.log.error("Could not clone player ["+event.getOriginal().getName()+"] baubles when changing dimensions");
		}
	}
	
	@SubscribeEvent
	public void attachCapabilitiesPlayer(AttachCapabilitiesEvent.Entity event) {
		if (event.getEntity() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(Baubles.MODID,"container"), 
					new BaublesContainerProvider(new BaublesContainer()));	
		}
	}
	
	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) {		
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) event.getEntity());	
			
			for (int a=0;a<baubles.getSlots();a++) baubles.setChanged(a,true);
			
			for (EntityPlayer p:event.getEntity().getEntityWorld().playerEntities) {
				if (p.getEntityId() != event.getEntity().getEntityId()) {
					IBaublesItemHandler baubles2 = BaublesApi.getBaublesHandler(p);	
					for (int a=0;a<baubles2.getSlots();a++) baubles2.setChanged(a,true);
				}
			}
			ItemStack[] sl = new ItemStack[baubles.getSlots()];
			Arrays.fill(sl, ItemStack.EMPTY);
			baublesSync.put(event.getEntity().getUniqueID(), sl);
		}
	}	
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		baublesSync.remove(event.player.getUniqueID());
	}
	
	
	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {
		// player events
		if (event.getEntity() instanceof EntityPlayer) {			
			EntityPlayer player = (EntityPlayer) event.getEntity();
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);	
			ItemStack[] items = baublesSync.get(player.getUniqueID());
			if (items==null) {
				ItemStack[] sl = new ItemStack[baubles.getSlots()];
				Arrays.fill(sl, ItemStack.EMPTY);
				baublesSync.put(player.getUniqueID(), sl);
				items = baublesSync.get(player.getUniqueID());
			}
			int count = baubles.getSlots();
			if(items.length != count) {
				ItemStack[] old = items;
				items = new ItemStack[count];
				for(int i = 0;i<old.length && i<items.length;i++) {
					items[i] = old[i];
				}
				baublesSync.put(player.getUniqueID(), items);
			}
			
			for (int a = 0; a < count; a++) {
				ItemStack baubleStack = baubles.getStackInSlot(a);
				IBauble bauble = null;
				if (baubleStack != null && !baubleStack.isEmpty() && baubleStack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {

					bauble = baubleStack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
					//Worn Tick
					bauble.onWornTick(baubleStack, player);
				}				
				//Sync
				if (!player.getEntityWorld().isRemote) { 
					if(baubles.isChanged(a) || (bauble!=null && bauble.willAutoSync(baubleStack, player) && !ItemStack.areItemStacksEqual(baubleStack, items[a]))) {
						try {
							PacketHandler.INSTANCE.sendToDimension(new PacketSync(player, a), player.getEntityWorld().provider.getDimension());
						} catch (Exception e) {	}
						items[a] = baubleStack != null ? baubleStack.copy() : ItemStack.EMPTY; 
					}
				}	
			}				
		}						
	}

	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer
				&& !event.getEntity().world.isRemote
				&& !event.getEntity().world.getGameRules().getBoolean("keepInventory")) {			
			dropItemsAt(event.getEntityPlayer(),event.getDrops(),event.getEntityPlayer());						
		}
	}
	
	public void dropItemsAt(EntityPlayer player, List<EntityItem> drops, Entity e) {
		IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
		for (int i = 0; i < baubles.getSlots(); ++i) {
			if (baubles.getStackInSlot(i) != null && !baubles.getStackInSlot(i).isEmpty()) {
				EntityItem ei = new EntityItem(e.world,
						e.posX, e.posY + e.getEyeHeight(), e.posZ,
						baubles.getStackInSlot(i).copy());
				ei.setPickupDelay(40);
				float f1 = e.world.rand.nextFloat() * 0.5F;
				float f2 = e.world.rand.nextFloat() * (float) Math.PI * 2.0F;
				ei.motionX = (double) (-MathHelper.sin(f2) * f1);
				ei.motionZ = (double) (MathHelper.cos(f2) * f1);
				ei.motionY = 0.20000000298023224D;
				drops.add(ei);
				baubles.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	
	@SubscribeEvent
	public void tooltipEvent(ItemTooltipEvent event) {
		if (event.getItemStack()!=null && !event.getItemStack().isEmpty() && event.getItemStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			IBauble bauble = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
			BaubleType bt = bauble.getBaubleType(event.getItemStack());
			event.getToolTip().add(TextFormatting.GOLD+I18n.translateToLocal("name."+bt));
		}
	}

}
