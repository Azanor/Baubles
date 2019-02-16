package baubles.common.container;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
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
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;

public class ContainerPlayerExpanded extends Container
{
	private final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	private final InventoryCraftResult craftResult = new InventoryCraftResult();
	public final IBaublesItemHandler baubles;
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
		baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES).orElseThrow(NullPointerException::new);

		this.addSlot(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));

		for (int i = 0; i < 2; ++i)
		{
			for (int j = 0; j < 2; ++j)
			{
				this.addSlot(new Slot(this.craftMatrix, j + i * 2, 116 + j * 18, 18 + i * 18));
			}
		}

		for (int k = 0; k < 4; k++) 
		{
			final EntityEquipmentSlot slot = equipmentSlots[k];
			this.addSlot(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18)
			{
				@Override
				public int getSlotStackLimit()
				{
					return 1;
				}
				@Override
				public boolean isItemValid(ItemStack stack)
				{
					return stack.getItem().isValidArmor(stack, slot, player);
				}
				@Override
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					ItemStack itemstack = this.getStack();
					return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
				}
				@Override
				public String getSlotTexture()
				{
					return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
				}
			});
		}

		this.addSlot(new SlotBauble(baubles,0,77,8 ));
		this.addSlot(new SlotBauble(baubles,1,77,8 + 1 * 18));
		this.addSlot(new SlotBauble(baubles,2,77,8 + 2 * 18));
		this.addSlot(new SlotBauble(baubles,3,77,8 + 3 * 18));
		this.addSlot(new SlotBauble(baubles,4,96,8 ));
		this.addSlot(new SlotBauble(baubles,5,96,8 + 1 * 18));
		this.addSlot(new SlotBauble(baubles,6,96,8 + 2 * 18));

		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i)
		{
			this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
		}

		this.addSlot(new Slot(playerInv, 40, 96, 62)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return super.isItemValid(stack);
			}
			@Override
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
		this.slotChangedCraftingGrid(this.thePlayer.getEntityWorld(), this.thePlayer, this.craftMatrix, this.craftResult);
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.craftResult.clear();

		if (!player.world.isRemote)
		{
			this.clearContainer(player, player.world, this.craftMatrix);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
			
			int slotShift = baubles.getSlots();

			if (index == 0)
			{
				if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, true))
				{
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (index >= 1 && index < 5)
			{
				if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (index >= 5 && index < 9)
			{
				if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
				{
					return ItemStack.EMPTY;
				}
			}

			// baubles -> inv
			else if (index >= 9 && index < 9+slotShift)
			{
				if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
				{
					return ItemStack.EMPTY;
				}
			}

			// inv -> armor
			else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack())
			{
				int i = 8 - entityequipmentslot.getIndex();

				if (!this.mergeItemStack(itemstack1, i, i + 1, false))
				{
					return ItemStack.EMPTY;
				}
			}

			// inv -> offhand
			else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45+ slotShift)).getHasStack())
			{
				if (!this.mergeItemStack(itemstack1, 45+ slotShift, 46+ slotShift, false))
				{
					return ItemStack.EMPTY;
				}
			}
			// inv -> bauble
			else if (itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE).isPresent())
			{
				IBauble bauble = itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE).orElseThrow(NullPointerException::new);
				for (int baubleSlot : bauble.getBaubleType().getValidSlots()) {
					if ( bauble.canEquip(thePlayer) && !this.inventorySlots.get(baubleSlot+9).getHasStack() &&
							!this.mergeItemStack(itemstack1, baubleSlot+9, baubleSlot + 10, false))
					{
						return ItemStack.EMPTY;
					} 
					if (itemstack1.getCount() == 0) break;
				}
			}
			else if (index >= 9+ slotShift && index < 36+ slotShift)
			{
				if (!this.mergeItemStack(itemstack1, 36+ slotShift, 45+ slotShift, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (index >= 36+ slotShift && index < 45+ slotShift)
			{
				if (!this.mergeItemStack(itemstack1, 9+ slotShift, 36+ slotShift, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 9+ slotShift, 45+ slotShift, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty() && !baubles.isEventBlocked() && slot instanceof SlotBauble &&
					itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE).isPresent()) {
				itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE)
						.ifPresent(b -> b.onUnequipped(playerIn));
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if (index == 0)
			{
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot)
	{
		return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
	}
}
