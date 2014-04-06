package baubles.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import baubles.api.BaubleType;
import baubles.api.IBauble;

public class SlotBauble extends Slot
{
	
	BaubleType type;

    public SlotBauble(IInventory par2IInventory, BaubleType type, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.type = type;
    }
    

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack stack)
    {
    	return stack!=null && stack.getItem() !=null &&
        	   stack.getItem() instanceof IBauble && 
        	   ((IBauble)stack.getItem()).getBaubleType(stack)==this.type &&
        	   ((IBauble)stack.getItem()).canEquip(stack, ((InventoryBaubles)this.inventory).player.get());
    }
    

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return this.getStack()!=null &&
			   ((IBauble)this.getStack().getItem()).canUnequip(this.getStack(), player);
	}


	@Override
    public int getSlotStackLimit()
    {
        return 1;
    }
    
}
