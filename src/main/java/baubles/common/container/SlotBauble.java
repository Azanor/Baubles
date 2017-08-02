package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBauble extends SlotItemHandler
{
	int baubleSlot;
	EntityPlayer player;

	public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int par4, int par5)
	{
		super(itemHandler, slot, par4, par5);
		this.baubleSlot = slot;
		this.player = player;
	}

	/**
	 * Check if the stack is a valid item for this slot.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return ((IBaublesItemHandler)getItemHandler()).isItemValidForSlot(baubleSlot, stack, player);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		ItemStack stack = getStack();
		if(stack.isEmpty())
			return false;

		IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
		return bauble.canUnequip(stack, player);
	}

	@Override
	public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
		if (!getHasStack() && !((IBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(stack, playerIn);
		}
		super.onTake(playerIn, stack);
		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		if (getHasStack() && !ItemStack.areItemStacksEqual(stack,getStack()) &&
				!((IBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(getStack(), player);
		}

		ItemStack oldstack = getStack().copy();
		super.putStack(stack);

		if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack,getStack())
				&& !((IBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onEquipped(getStack(), player);
		}
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
