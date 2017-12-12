package baubles.common.event;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.BaublesContainerProvider;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSync;

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
	public void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(Baubles.MODID,"container"),
					new BaublesContainerProvider(new BaublesContainer()));
		}
	}

	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			syncSlots(player, Collections.singletonList(player));
		}
	}

	@SubscribeEvent
	public void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (target instanceof EntityPlayerMP) {
			syncSlots((EntityPlayer) target, Collections.singletonList(event.getEntityPlayer()));
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		baublesSync.remove(event.player.getUniqueID());
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		// player events
		if (event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
			for (int i = 0; i < baubles.getSlots(); i++) {
				ItemStack stack = baubles.getStackInSlot(i);
				IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
				if (bauble != null) {
					bauble.onWornTick(stack, player);
				}
			}
			if (!player.world.isRemote) {
				syncBaubles(player, baubles);
			}
		}
	}

	private void syncBaubles(EntityPlayer player, IBaublesItemHandler baubles) {
		ItemStack[] items = baublesSync.get(player.getUniqueID());
		if (items == null) {
			items = new ItemStack[baubles.getSlots()];
			Arrays.fill(items, ItemStack.EMPTY);
			baublesSync.put(player.getUniqueID(), items);
		}
		if (items.length != baubles.getSlots()) {
			ItemStack[] old = items;
			items = new ItemStack[baubles.getSlots()];
			System.arraycopy(old, 0, items, 0, Math.min(old.length, items.length));
			baublesSync.put(player.getUniqueID(), items);
		}
		Set<EntityPlayer> receivers = null;
		for (int i = 0; i < baubles.getSlots(); i++) {
			ItemStack stack = baubles.getStackInSlot(i);
			IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
			if (baubles.isChanged(i) || bauble != null && bauble.willAutoSync(stack, player) && !ItemStack.areItemStacksEqual(stack, items[i])) {
				if (receivers == null) {
					receivers = new HashSet<>(((WorldServer) player.world).getEntityTracker().getTrackingPlayers(player));
					receivers.add(player);
				}
				syncSlot(player, i, stack, receivers);
				baubles.setChanged(i,false);
				items[i] = stack == null ? ItemStack.EMPTY : stack.copy();
			}
		}
	}

	private void syncSlots(EntityPlayer player, Collection<? extends EntityPlayer> receivers) {
		IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
		for (int i = 0; i < baubles.getSlots(); i++) {
			syncSlot(player, i, baubles.getStackInSlot(i), receivers);
		}
	}

	private void syncSlot(EntityPlayer player, int slot, ItemStack stack, Collection<? extends EntityPlayer> receivers) {
		PacketSync pkt = new PacketSync(player, slot, stack);
		for (EntityPlayer receiver : receivers) {
			PacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
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
}
