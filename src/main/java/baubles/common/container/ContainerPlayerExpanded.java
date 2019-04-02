package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ContainerPlayerExpanded extends Container {
    /**
     * The crafting matrix inventory.
     */
    public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public final InventoryCraftResult craftResult = new InventoryCraftResult();
    public IBaublesItemHandler baubles;
    /**
     * Determines if inventory manipulation should be handled.
     */
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;
    private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    public ContainerPlayerExpanded(InventoryPlayer playerInv, boolean par2, EntityPlayer player) {
        isLocalWorld = par2;
        thePlayer = player;
        baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)
                .orElse(BaublesCapabilities.CAPABILITY_BAUBLES.getDefaultInstance());

        addSlot(new SlotCrafting(playerInv.player, craftMatrix, craftResult, 0, 154, 28));

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                addSlot(new Slot(craftMatrix, j + i * 2, 116 + j * 18, 18 + i * 18));
            }
        }

        final String[] EMPTY_SLOT_NAMES = new String[]{"minecraft:items/empty_armor_slot_boots", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_helmet"};

        for (int k = 0; k < 4; k++) {
            final EntityEquipmentSlot slot = equipmentSlots[k];
            addSlot(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return (stack.getItem() instanceof ItemArmor) && ((ItemArmor) stack.getItem()).getEquipmentSlot() == slot;
                }

                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    ItemStack itemstack = getStack();
                    return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                }

                @Override
                public String getSlotTexture() {
                    return EMPTY_SLOT_NAMES[slot.getIndex()];
                }
            });
        }

        addSlot(new SlotBauble(player, baubles, 0, 77, 8));
        addSlot(new SlotBauble(player, baubles, 1, 77, 8 + 1 * 18));
        addSlot(new SlotBauble(player, baubles, 2, 77, 8 + 2 * 18));
        addSlot(new SlotBauble(player, baubles, 3, 77, 8 + 3 * 18));
        addSlot(new SlotBauble(player, baubles, 4, 96, 8));
        addSlot(new SlotBauble(player, baubles, 5, 96, 8 + 1 * 18));
        addSlot(new SlotBauble(player, baubles, 6, 96, 8 + 2 * 18));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        addSlot(new Slot(playerInv, 40, 96, 62) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack);
            }

            @Override
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });

        onCraftMatrixChanged(craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        slotChangedCraftingGrid(thePlayer.getEntityWorld(), thePlayer, craftMatrix, craftResult);
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        craftResult.clear();

        if (!player.world.isRemote) {
            clearContainer(player, player.world, craftMatrix);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            int slotShift = baubles.getSlots();

            if (index == 0) {
                if (!mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // baubles -> inv
            else if (index >= 9 && index < 9 + slotShift) {
                if (!mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // inv -> armor
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot) inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack()) {
                int i = 8 - entityequipmentslot.getIndex();

                if (!mergeItemStack(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // inv -> offhand
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot) inventorySlots.get(45 + slotShift)).getHasStack()) {
                if (!mergeItemStack(itemstack1, 45 + slotShift, 46 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // inv -> bauble
            else if (itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).isPresent()) {
                IBauble bauble = itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).orElse(null);
                for (int baubleSlot : bauble.getBaubleType(itemstack).getValidSlots()) {
                    if (bauble.canEquip(itemstack1, thePlayer) && !((Slot) inventorySlots.get(baubleSlot + 9)).getHasStack() &&
                            !mergeItemStack(itemstack1, baubleSlot + 9, baubleSlot + 10, false)) {
                        return ItemStack.EMPTY;
                    }
                    if (itemstack1.getCount() == 0) break;
                }
            } else if (index >= 9 + slotShift && index < 36 + slotShift) {
                if (!mergeItemStack(itemstack1, 36 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 36 + slotShift && index < 45 + slotShift) {
                if (!mergeItemStack(itemstack1, 9 + slotShift, 36 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty() && !baubles.isEventBlocked() && slot instanceof SlotBauble) {
                final ItemStack itemStack1 = itemstack;
                itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)
                        .ifPresent(b -> b.onUnequipped(itemStack1, playerIn));
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0) {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    //private void unequipBauble(ItemStack stack) { }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return slot.inventory != craftResult && super.canMergeSlot(stack, slot);
    }
}
