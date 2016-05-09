package baubles.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.lib.PlayerHandler;

public class ContainerPlayerExpanded extends Container
{
    /**
     * The crafting matrix inventory.
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public InventoryBaubles baubles;
    /**
     * Determines if inventory manipulation should be handled.
     */
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;
    private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerPlayerExpanded(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
    {
        this.isLocalWorld = par2;
        this.thePlayer = player;
        baubles = new InventoryBaubles(player);
        baubles.setEventHandler(this);
        if (!player.worldObj.isRemote) {
        	baubles.stackList = PlayerHandler.getPlayerBaubles(player).stackList;
        }
        
        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));
        
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }


		for (int k = 0; k < 4; k++) 
		{
			final EntityEquipmentSlot slot = equipmentSlots[k];
			this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18)
            {
                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }
                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null)
                    {
                        return false;
                    }
                    else
                    {
                        return stack.getItem().isValidArmor(stack, slot, thePlayer);
                    }
                }
                @Override
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
                }
            });
		}
		
		
        
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.AMULET,0,77,8 ));
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.RING,1,77,8 + 1 * 18));
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.RING,2,77,8 + 2 * 18));
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.BELT,3,77,8 + 3 * 18));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
        
        this.addSlotToContainer(new Slot(playerInv, 40, 97, 62)
        {
        	@Override
            public boolean isItemValid(ItemStack stack)
            {
                return super.isItemValid(stack);
            }
        	@Override
            @SideOnly(Side.CLIENT)
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });

        this.onCraftMatrixChanged(this.craftMatrix);
        
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

            if (itemstack != null)
            {
                player.dropItem(itemstack, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, (ItemStack)null);
        if (!player.worldObj.isRemote) {
        	PlayerHandler.setPlayerBaubles(player, baubles);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 9+4, 45+4, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 >= 1 && par2 < 5)
            {
                if (!this.mergeItemStack(itemstack1, 9+4, 45+4, false))
                {
                    return null;
                }
            }
            else if (par2 >= 5 && par2 < 9)
            {
                if (!this.mergeItemStack(itemstack1, 9+4, 45+4, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof ItemArmor)
            {
				ItemArmor armor = (ItemArmor) itemstack1.getItem();
				int armorSlot = 8 - armor.armorType.getIndex();
				
                if (!this.mergeItemStack(itemstack1, armorSlot, armorSlot + 1, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof IBauble && 
            		((IBauble)itemstack.getItem()).getBaubleType(itemstack)==BaubleType.AMULET &&
    				((IBauble)itemstack.getItem()).canEquip(itemstack, thePlayer) &&
            		!((Slot)this.inventorySlots.get(9)).getHasStack())
            {
                int j = 9;
                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (par2>11 && itemstack.getItem() instanceof IBauble && 
            		((IBauble)itemstack.getItem()).getBaubleType(itemstack)==BaubleType.RING &&
    				((IBauble)itemstack.getItem()).canEquip(itemstack, thePlayer) &&
            		!((Slot)this.inventorySlots.get(10)).getHasStack())
            {
                int j = 10;
                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (par2>11 && itemstack.getItem() instanceof IBauble && 
            		((IBauble)itemstack.getItem()).getBaubleType(itemstack)==BaubleType.RING &&
    				((IBauble)itemstack.getItem()).canEquip(itemstack, thePlayer) &&
            		!((Slot)this.inventorySlots.get(11)).getHasStack())
            {
                int j = 11;
                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof IBauble && 
            		((IBauble)itemstack.getItem()).getBaubleType(itemstack)==BaubleType.BELT &&
    				((IBauble)itemstack.getItem()).canEquip(itemstack, thePlayer) &&
            		!((Slot)this.inventorySlots.get(12)).getHasStack())
            {
                int j = 12;
                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (par2 >= 9+4 && par2 < 36+4)
            {
                if (!this.mergeItemStack(itemstack1, 36+4, 45+4, false))
                {
                    return null;
                }
            }
            else if (par2 >= 36+4 && par2 < 45+4)
            {
                if (!this.mergeItemStack(itemstack1, 9+4, 36+4, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 9+4, 45+4, false, slot))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
    
    private void unequipBauble(ItemStack stack) {
//    	if (stack.getItem() instanceof IBauble) {
//    		((IBauble)stack.getItem()).onUnequipped(stack, thePlayer);
//    	}
    }
    
    
    
    @Override
	public void putStacksInSlots(ItemStack[] p_75131_1_) {
		baubles.blockEvents=true;
		super.putStacksInSlots(p_75131_1_);
	}
    
    
    

	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4, Slot ss)
    {
        boolean flag1 = false;
        int k = par2;

        if (par4)
        {
            k = par3 - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (par1ItemStack.isStackable())
        {
            while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2))
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 != null && itemstack1.getItem() == par1ItemStack.getItem() && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1))
                {
                    int l = itemstack1.stackSize + par1ItemStack.stackSize;
                    if (l <= par1ItemStack.getMaxStackSize())
                    {
                    	if (ss instanceof SlotBauble) unequipBauble(par1ItemStack);
                    	par1ItemStack.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize())
                    {
                    	if (ss instanceof SlotBauble) unequipBauble(par1ItemStack);
                        par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = par1ItemStack.getMaxStackSize();
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (par4)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        if (par1ItemStack.stackSize > 0)
        {
            if (par4)
            {
                k = par3 - 1;
            }
            else
            {
                k = par2;
            }

            while (!par4 && k < par3 || par4 && k >= par2)
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (itemstack1 == null)
                {
                	if (ss instanceof SlotBauble) unequipBauble(par1ItemStack);
                    slot.putStack(par1ItemStack.copy());
                    slot.onSlotChanged();
                    par1ItemStack.stackSize = 0;
                    flag1 = true;
                    break;
                }

                if (par4)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }
        return flag1;
    }

    @Override
    public boolean canMergeSlot(ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.craftResult && super.canMergeSlot(par1ItemStack, par2Slot);
    }

}
