package baubles.common.container;

import baubles.api.IBauble;
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
    	return stack!=null && stack.getItem() !=null &&
        	   stack.getItem() instanceof IBauble && 
        	   ((IBauble)stack.getItem()).getBaubleType(stack).hasSlot(baubleSlot) &&
        	   ((IBauble)stack.getItem()).canEquip(stack, player);
    }    

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return getStack()!=null &&
			   ((IBauble)getStack().getItem()).canUnequip(getStack(), player);
	}	
	
	@Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
		if (!getHasStack() && !((IBaublesItemHandler)getItemHandler()).isEventBlocked()) {
			((IBauble)stack.getItem()).onUnequipped(stack, playerIn);
		}
		super.onPickupFromSlot(playerIn, stack);		
	}

	@Override
	public void putStack(ItemStack stack) {		
		if (getHasStack() && !((IBaublesItemHandler)getItemHandler()).isEventBlocked()) {
			((IBauble)getStack().getItem()).onUnequipped(getStack(), player);
		}
		
		super.putStack(stack);	
		
		if (this.getHasStack() && !((IBaublesItemHandler)getItemHandler()).isEventBlocked()) {
			((IBauble)getStack().getItem()).onEquipped(getStack(), player);
		}
	}

	@Override
    public int getSlotStackLimit()
    {
        return 1;
    }
    
}
