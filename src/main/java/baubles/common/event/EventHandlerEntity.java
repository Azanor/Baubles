package baubles.common.event;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

public class EventHandlerEntity {
	
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
			
			String[] ia = new String[baubles.getSlots()];
			Arrays.fill(ia, "");
			syncCheck.put(event.getEntity().getName(), ia);		
		}
	}
	
	private HashMap<String,String[]> syncCheck = new  HashMap<>();
	
	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {
		// player events
		if (event.getEntity() instanceof EntityPlayer) {			
			EntityPlayer player = (EntityPlayer) event.getEntity();
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);	
			String[] hashOld = syncCheck.get(player.getName());
			
			boolean syncTick = player.ticksExisted%10==0;
			
			for (int a = 0; a < baubles.getSlots(); a++) {
				ItemStack bauble = baubles.getStackInSlot(a);
				
				if (bauble != null && bauble.getItem() instanceof IBauble) {
					//Worn Tick
					((IBauble) bauble.getItem()).onWornTick(bauble, player);
					
					//Sync
					if (!player.getEntityWorld().isRemote) {
						if (syncTick && !baubles.isChanged(a) &&
								((IBauble) bauble.getItem()).willAutoSync(bauble, player)) {							
							String s = bauble.toString();
							if (bauble.hasTagCompound()) s += bauble.getTagCompound().toString();
							if (!s.equals(hashOld[a])) {
								baubles.setChanged(a,true);
							}
							hashOld[a] = s;							
						}						
					}
				}			
				
				if (baubles.isChanged(a)) {
					try {
						PacketHandler.INSTANCE.sendToDimension(new PacketSync(player, a),
								player.getEntityWorld().provider.getDimension());
					} catch (Exception e) {	}				
				}
			}
				
		}
			
	}

	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer
				&& !event.getEntity().worldObj.isRemote
				&& !event.getEntity().worldObj.getGameRules().getBoolean("keepInventory")) {			
			dropItemsAt(event.getEntityPlayer(),event.getDrops(),event.getEntityPlayer());						
		}
	}
	
	public void dropItemsAt(EntityPlayer player, List<EntityItem> drops, Entity e) {
		IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
		for (int i = 0; i < baubles.getSlots(); ++i) {
			if (baubles.getStackInSlot(i) != null) {
				EntityItem ei = new EntityItem(e.worldObj,
						e.posX, e.posY + e.getEyeHeight(), e.posZ,
						baubles.getStackInSlot(i).copy());
				ei.setPickupDelay(40);
				float f1 = e.worldObj.rand.nextFloat() * 0.5F;
				float f2 = e.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
				ei.motionX = (double) (-MathHelper.sin(f2) * f1);
				ei.motionZ = (double) (MathHelper.cos(f2) * f1);
				ei.motionY = 0.20000000298023224D;
				drops.add(ei);
				baubles.setStackInSlot(i, null);
			}
		}
	}

	
	@SubscribeEvent
	public void tooltipEvent(ItemTooltipEvent event) {
		if (event.getItemStack()!=null && event.getItemStack().getItem() instanceof IBauble) {
			BaubleType bt = ((IBauble)event.getItemStack().getItem()).getBaubleType(event.getItemStack());
			event.getToolTip().add(TextFormatting.GOLD+I18n.translateToLocal("name."+bt));
		}
	}
	
	
	
	@SubscribeEvent
	public void playerLoad(PlayerEvent.LoadFromFile event) {		
		File file1 = getPlayerFile("baub", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString());
		if (file1.exists()) {
			Baubles.log.info("Loading legacy baubles inventory for ["+event.getEntityPlayer().getDisplayNameString()+"]. Occupied slots will be skipped");
			loadPlayerBaubles(event.getEntityPlayer(), file1, getPlayerFile("baubback", event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()));
			file1.delete();
		}				
	}
	
	public void loadPlayerBaubles(EntityPlayer player, File file1, File file2) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				NBTTagCompound data = null;
				boolean save = false;
				if (file1 != null && file1.exists()) {
					try {
						FileInputStream fileinputstream = new FileInputStream(file1);
						data = CompressedStreamTools.readCompressed(fileinputstream);
						fileinputstream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	
				if (data != null) {
					IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);				
					NBTTagList tagList = data.getTagList("Baubles.Inventory", 10);
					for (int i = 0; i < tagList.tagCount(); ++i) {
						NBTTagCompound nbttagcompound = (NBTTagCompound) tagList.getCompoundTagAt(i);
						int j = nbttagcompound.getByte("Slot") & 255;
						ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
						if (itemstack != null && baubles.getStackInSlot(j)==null) {
							baubles.setStackInSlot(j, itemstack);
						}
					}				
				}
			} catch (Exception exception1) {
				Baubles.log.fatal("Error loading legacy baubles inventory");
				exception1.printStackTrace();
			}
		}
	}
	
	public File getPlayerFile(String suffix, File playerDirectory, String playername)
    {
        if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
        return new File(playerDirectory, "_"+playername+"."+suffix);
    }


}
