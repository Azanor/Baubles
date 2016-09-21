package baubles.common.container;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBauble extends SlotItemHandler
{
	
	BaubleType type;
	EntityPlayer player;

    public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, BaubleType type, int par3, int par4, int par5)
    {
        super(itemHandler, par3, par4, par5);
        this.type = type;
        this.player = player;
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
        	   ((IBauble)stack.getItem()).canEquip(stack, player);
    }    

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return this.getStack()!=null &&
			   ((IBauble)this.getStack().getItem()).canUnequip(this.getStack(), player);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
		if (!this.getHasStack() && !((IBaublesItemHandler)this.getItemHandler()).isEventBlocked()) {
			((IBauble)stack.getItem()).onUnequipped(this.getStack(), playerIn);
		}
		super.onPickupFromSlot(playerIn, stack);		
	}

	@Override
	public void putStack(ItemStack stack) {
		super.putStack(stack);		
		if (this.getHasStack() && !((IBaublesItemHandler)this.getItemHandler()).isEventBlocked()) {
			((IBauble)this.getStack().getItem()).onEquipped(this.getStack(), player);
		}
	}

	@Override
    public int getSlotStackLimit()
    {
        return 1;
    }
    
}
