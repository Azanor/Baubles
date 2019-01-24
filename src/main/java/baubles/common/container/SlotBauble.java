package baubles.common.container;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotBauble extends SlotItemHandler
{
	private final int baubleSlot;
	private final EntityPlayer player;

	public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int par4, int par5)
	{
		super(itemHandler, slot, par4, par5);
		this.baubleSlot = slot;
		this.player = player;
	}

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

		return stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE)
				.map(b -> b.canUnequip(player))
				.orElse(false);
	}

	@Nonnull
	@Override
	public ItemStack onTake(EntityPlayer playerIn, @Nonnull ItemStack stack) {
		if (!getHasStack() && !((IBaublesItemHandler)getItemHandler()).isEventBlocked()) {
			stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE)
					.ifPresent(b -> b.onUnequipped(playerIn));
		}
		super.onTake(playerIn, stack);
		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		if (getHasStack() && !ItemStack.areItemStacksEqual(stack,getStack()) &&
				!((IBaublesItemHandler)getItemHandler()).isEventBlocked()) {
			getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE)
					.ifPresent(b -> b.onUnequipped(player));
		}

		ItemStack oldstack = getStack().copy();
		super.putStack(stack);

		if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack,getStack())
				&& !((IBaublesItemHandler)getItemHandler()).isEventBlocked()) {
			getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE)
					.ifPresent(b -> b.onEquipped(player));
		}
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
