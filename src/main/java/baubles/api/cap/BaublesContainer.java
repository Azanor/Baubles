package baubles.api.cap;

import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class BaublesContainer extends ItemStackHandler implements IBaublesItemHandler {

	private final static int BAUBLE_SLOTS = 7;
	private boolean[] changed = new boolean[BAUBLE_SLOTS];
	private boolean blockEvents=false;
	private final EntityLivingBase holder;

	public BaublesContainer(EntityLivingBase holder)
	{
		super(BAUBLE_SLOTS);
		this.holder = holder;
	}

	@Override
	public void setSize(int size)
	{
		if (size<BAUBLE_SLOTS) size = BAUBLE_SLOTS;
		super.setSize(size);
		boolean[] old = changed;
		changed = new boolean[size];
		for(int i = 0;i<old.length && i<changed.length;i++)
		{
			changed[i] = old[i];
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		LazyOptional<IBauble> opt = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE);
		if (stack.isEmpty() || !opt.isPresent())
			return false;
		IBauble bauble = opt.orElseThrow(NullPointerException::new);
		return bauble.canEquip(holder) && bauble.getBaubleType().hasSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (stack.isEmpty() || this.isItemValidForSlot(slot, stack)) {
			super.setStackInSlot(slot, stack);
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!this.isItemValidForSlot(slot, stack)) return stack;
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public boolean isEventBlocked() {
		return blockEvents;
	}

	@Override
	public void setEventBlock(boolean blockEvents) {
		this.blockEvents = blockEvents;
	}

	@Override
	protected void onContentsChanged(int slot)
	{
		setChanged(slot,true);
	}

	@Override
	public boolean isChanged(int slot) {
		return changed[slot];
	}

	@Override
	public void setChanged(int slot, boolean change) {
		this.changed[slot] = change;
	}
}
