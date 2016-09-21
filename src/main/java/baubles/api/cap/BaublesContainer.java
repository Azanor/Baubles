package baubles.api.cap;

import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BaublesContainer extends ItemStackHandler implements IBaublesItemHandler {

	public BaublesContainer()
    {
        super(4);
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
	
		
	private boolean blockEvents=false;
	
	@Override
	public boolean isEventBlocked() {
		return blockEvents;
	}

	@Override
	public void setEventBlock(boolean blockEvents) {
		this.blockEvents = blockEvents;
	}
	

}
