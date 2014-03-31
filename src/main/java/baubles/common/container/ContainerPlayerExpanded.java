package baubles.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
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

    public ContainerPlayerExpanded(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
    {
        this.isLocalWorld = par2;
        this.thePlayer = player;
        baubles = new InventoryBaubles(player);
        baubles.setEventHandler(this);
        if (!player.worldObj.isRemote) {
        	baubles.stackList = PlayerHandler.getPlayerBaubles(player).stackList;
        }
        
        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 144, 36));
        int i;
        int j;

        for (i = 0; i < 2; ++i)
        {
            for (j = 0; j < 2; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 106 + j * 18, 26 + i * 18));
            }
        }

        for (i = 0; i < 4; ++i)
        {
            final int k = i;
            this.addSlotToContainer(new Slot(playerInv, playerInv.getSizeInventory() - 1 - i, 8, 8 + i * 18)
            {
                @Override
                public int getSlotStackLimit() { return 1; }
                @Override
                public boolean isItemValid(ItemStack par1ItemStack)
                {
                    if (par1ItemStack == null) return false;
                    return par1ItemStack.getItem().isValidArmor(par1ItemStack, k, thePlayer);
                }
            });
        }
        
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.AMULET,0,80,8 + 0 * 18));
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.RING,1,80,8 + 1 * 18));
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.RING,2,80,8 + 2 * 18));
        this.addSlotToContainer(new SlotBauble(baubles,BaubleType.BELT,3,80,8 + 3 * 18));

        for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }

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
            ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

            if (itemstack != null)
            {
                player.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, (ItemStack)null);
        if (!player.worldObj.isRemote) {
        	PlayerHandler.setPlayerBaubles(player, baubles);
        	PlayerHandler.updatePlayerBaubles(player);
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
            else if (itemstack.getItem() instanceof ItemArmor && 
            		!((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack())
            {
                int j = 5 + ((ItemArmor)itemstack.getItem()).armorType;

                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof IBauble && 
            		((IBauble)itemstack.getItem()).getBaubleType(itemstack)==BaubleType.AMULET &&
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
            else if (!this.mergeItemStack(itemstack1, 9+4, 45+4, false))
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

    @Override
    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }

}
