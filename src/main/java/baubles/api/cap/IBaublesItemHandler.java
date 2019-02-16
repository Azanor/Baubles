package baubles.api.cap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IBaublesItemHandler extends IItemHandlerModifiable {

	public boolean isItemValidForSlot(int slot, ItemStack stack);

	/**
	 * Used internally to prevent equip/unequip events from triggering when they shouldn't
	 */
	public boolean isEventBlocked();
	public void setEventBlock(boolean blockEvents);

	void tick();
	EntityLivingBase getHolder();
}
