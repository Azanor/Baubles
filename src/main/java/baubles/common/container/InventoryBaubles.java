package baubles.common.container;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.lib.PlayerHandler;

public class InventoryBaubles implements IInventory {
	public ItemStack[] stackList;
	private Container eventHandler;
	public WeakReference<EntityPlayer> player;

	public InventoryBaubles(EntityPlayer player) {
		this.stackList = new ItemStack[4];
		this.player = new WeakReference<EntityPlayer>(player);
	}

	public Container getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(Container eventHandler) {
		this.eventHandler = eventHandler;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return this.stackList.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1) {
		return par1 >= this.getSizeInventory() ? null : this.stackList[par1];
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName() {
		return "";
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
		if (this.stackList[par1] != null) {
			ItemStack itemstack = this.stackList[par1];
			this.stackList[par1] = null;
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
		if (this.stackList[par1] != null) {
			ItemStack itemstack;

			if (this.stackList[par1].stackSize <= par2) {
				itemstack = this.stackList[par1];				
				this.stackList[par1] = null;
				
				if (itemstack !=null && itemstack.getItem() instanceof IBauble) {
					((IBauble)itemstack.getItem()).onUnequipped(itemstack, player.get());
				}
				
				if (eventHandler!=null) this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			} else {
				itemstack = this.stackList[par1].splitStack(par2);
				
				if (this.stackList[par1].stackSize == 0) {
					this.stackList[par1] = null;
				}

				if (itemstack !=null && itemstack.getItem() instanceof IBauble) {
					((IBauble)itemstack.getItem()).onUnequipped(itemstack, player.get());
				}
				if (eventHandler!=null) this.eventHandler.onCraftMatrixChanged(this);
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
		this.stackList[par1] = stack;
		if (stack !=null && stack.getItem() instanceof IBauble) {
			((IBauble)stack.getItem()).onEquipped(stack, player.get());
		}
		if (eventHandler!=null) this.eventHandler.onCraftMatrixChanged(this);
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
			PlayerHandler.updatePlayerBaubles(player.get());
		} catch (Exception e) {		}
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
	public void openInventory() {
		
	}

	@Override
	public void closeInventory() {	
		
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof IBauble))
			return false;
		if (i == 0
				&& ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.AMULET)
			return true;
		if ((i == 1 || i == 2)
				&& ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.RING)
			return true;
		if (i == 3
				&& ((IBauble) stack.getItem()).getBaubleType(stack) == BaubleType.BELT)
			return true;
		return false;
	}

	public void saveNBT(EntityPlayer player) {
		NBTTagCompound tags = player.getEntityData();
		NBTTagList tagList = new NBTTagList();
		NBTTagCompound invSlot;
		for (int i = 0; i < this.stackList.length; ++i) {
			if (this.stackList[i] != null) {
				invSlot = new NBTTagCompound();
				invSlot.setByte("Slot", (byte) i);
				this.stackList[i].writeToNBT(invSlot);
				tagList.appendTag(invSlot);
			}
		}
		tags.setTag("Baubles.Inventory", tagList);
	}

	public void readNBT(EntityPlayer player) {
		NBTTagCompound tags = player.getEntityData();
		NBTTagList tagList = tags.getTagList("Baubles.Inventory", 10);
		for (int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = (NBTTagCompound) tagList.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
			if (itemstack != null) {
				this.stackList[j] = itemstack;
			}
		}
	}

	public void dropItems(EntityPlayer player) {
		for (int i = 0; i < 4; ++i) {
			if (this.stackList[i] != null) {
				player.dropPlayerItemWithRandomChoice(this.stackList[i], true);
				this.stackList[i] = null;
			}
		}
	}
}