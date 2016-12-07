package baubles.api.cap;

import java.util.Arrays;

import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BaublesContainer extends ItemStackHandler implements IBaublesItemHandler {

	private final static int BAUBLE_SLOTS = 7;
	private boolean[] changed = new boolean[BAUBLE_SLOTS];
	private boolean blockEvents = false;
	
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
		if (stack == null || !(stack.getItem() instanceof IBauble) ||
				!((IBauble) stack.getItem()).canEquip(stack, player))
			return false;
		
		return ((IBauble) stack.getItem()).getBaubleType(stack).hasSlot(slot);
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
