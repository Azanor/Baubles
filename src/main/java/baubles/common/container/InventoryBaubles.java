package baubles.common.container;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.Baubles;
import baubles.common.lib.PlayerHandler;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSyncBauble;

public class InventoryBaubles implements IInventory {
	public ItemStack[] stackList;
	private Container eventHandler;
	public WeakReference<EntityPlayer> player;

	public InventoryBaubles(EntityPlayer p) {
		stackList = new ItemStack[4];
		player = new WeakReference<EntityPlayer>(p);
	}

	public Container getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(Container container) {
		eventHandler = container;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return stackList.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1) {
		return par1 >= getSizeInventory() ? null : stackList[par1];
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName() {
		return ""; // modid?
	}

	/**
	 * Returns if the inventory is named
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (stackList[par1] != null) {
			ItemStack itemstack = stackList[par1];
			stackList[par1] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if (stackList[par1] != null) {
			ItemStack itemstack;

			if (stackList[par1].stackSize <= par2) {
				itemstack = stackList[par1];
				stackList[par1] = null;

				if (itemstack != null && itemstack.getItem() instanceof IBauble) {
					((IBauble) itemstack.getItem()).onUnequipped(itemstack, player.get());
				}

				if (eventHandler != null)
					eventHandler.onCraftMatrixChanged(this);
				syncSlotToClients(par1);
				return itemstack;
			} else {
				itemstack = stackList[par1].splitStack(par2);

				if (stackList[par1].stackSize == 0) {
					stackList[par1] = null;
				}

				if (itemstack != null && itemstack.getItem() instanceof IBauble) {
					((IBauble) itemstack.getItem()).onUnequipped(itemstack, player.get());
				}
				if (eventHandler != null)
					eventHandler.onCraftMatrixChanged(this);
				syncSlotToClients(par1);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack stack) {
		stackList[par1] = stack;
		if (stack != null && stack.getItem() instanceof IBauble) {
			((IBauble) stack.getItem()).onEquipped(stack, player.get());
		}
		if (eventHandler != null)
			eventHandler.onCraftMatrixChanged(this);
		syncSlotToClients(par1);
	}

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved
	 * to disk later - the game won't think it hasn't changed and skip it.
	 */
	@Override
	public void markDirty() {
		try {
			PlayerHandler.savePlayerBaubles(player.get());
		} catch (Exception e) {
			// Baubles.log....
		}
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof IBauble) || !((IBauble) stack.getItem()).canEquip(stack, player.get()))
			return false;
		if (i == 0 && ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.AMULET)
			return true;
		if ((i == 1 || i == 2) && ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.RING)
			return true;
		if (i == 3 && ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.BELT)
			return true;
		return false;
	}

	public void saveNBT(EntityPlayer player) {
		NBTTagCompound tags = player.getEntityData();
		saveNBT(tags);
	}

	public void saveNBT(NBTTagCompound tags) {
		NBTTagList tagList = new NBTTagList();
		NBTTagCompound invSlot;
		for (int i = 0; i < stackList.length; ++i) {
			if (stackList[i] != null) {
				invSlot = new NBTTagCompound();
				invSlot.setByte("Slot", (byte) i);
				stackList[i].writeToNBT(invSlot);
				tagList.appendTag(invSlot);
			}
		}
		tags.setTag("Baubles.Inventory", tagList);
	}

	public void readNBT(EntityPlayer player) {
		NBTTagCompound tags = player.getEntityData();
		readNBT(tags);
	}

	public void readNBT(NBTTagCompound tags) {
		NBTTagList tagList = tags.getTagList("Baubles.Inventory", 10);
		for (int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound nbt = (NBTTagCompound) tagList.getCompoundTagAt(i);
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbt);
			if (itemstack != null)
				stackList[nbt.getByte("Slot") & 255] = itemstack;
		}
	}

	public void dropItems(ArrayList<EntityItem> drops) {
		for (int i = 0; i < 4; ++i) {
			if (stackList[i] != null) {
				EntityItem ei = new EntityItem(player.get().worldObj, player.get().posX, player.get().posY + player.get().eyeHeight, player.get().posZ, stackList[i].copy());
				ei.delayBeforeCanPickup = 40;
				float f1 = player.get().worldObj.rand.nextFloat() * 0.5F;
				float f2 = player.get().worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
				ei.motionX = (double) (-MathHelper.sin(f2) * f1);
				ei.motionZ = (double) (MathHelper.cos(f2) * f1);
				ei.motionY = 0.20000000298023224D;
				drops.add(ei);
				stackList[i] = null;
				syncSlotToClients(i);
			}
		}
	}

	public void syncSlotToClients(int slot) {
		try {
			if (Baubles.proxy.getClientWorld() == null) {
				PacketHandler.INSTANCE.sendToAll(new PacketSyncBauble(player.get(), slot));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}