package baubles.api.cap;

import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import net.minecraftforge.items.ItemStackHandler;

public class BaublesContainer extends ItemStackHandler implements IBaublesItemHandler {

	private final static int BAUBLE_SLOTS = 7;
	private boolean[] changed = new boolean[BAUBLE_SLOTS];
	private boolean blockEvents=false;	
	private EntityLivingBase player;

	public BaublesContainer()
	{
		super(BAUBLE_SLOTS);
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

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
		OptionalCapabilityInstance<IBauble> oci = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE);
		if (stack.isEmpty() || !oci.isPresent())
			return false;
		IBauble bauble = oci.orElseThrow(NullPointerException::new);
		return bauble.canEquip(player) && bauble.getBaubleType().hasSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (stack.isEmpty() || this.isItemValidForSlot(slot, stack, player)) {
			super.setStackInSlot(slot, stack);
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!this.isItemValidForSlot(slot, stack, player)) return stack;
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

	@Override
	public void setPlayer(EntityLivingBase player) {
		this.player=player;
	}
}
