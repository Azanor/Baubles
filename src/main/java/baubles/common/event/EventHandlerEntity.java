package baubles.common.event;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.BaublesContainerProvider;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerEntity {
	
	@SubscribeEvent
	public void playerTick(AttachCapabilitiesEvent.Entity event) {
		if (event.getEntity() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(Baubles.MODID,"container"), 
					new BaublesContainerProvider(new BaublesContainer()));
			
			
		}
	}
	
	
	
	
	
//	static HashSet<Integer> syncSchedule = new HashSet<Integer>();

	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {

		// player events
		if (event.getEntity() instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) event.getEntity();
			IBaublesItemHandler baubles = BaublesApi.getBaubles(player);
			
			for (int a = 0; a < baubles.getSlots(); a++) {
				if (baubles.getStackInSlot(a) != null && baubles.getStackInSlot(a).getItem() instanceof IBauble) {
					((IBauble) baubles.getStackInSlot(a).getItem()).onWornTick(baubles.getStackInSlot(a), player);
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
		IBaublesItemHandler baubles = BaublesApi.getBaubles(player);
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
				IBaublesItemHandler baubles = BaublesApi.getBaubles(player);				
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
