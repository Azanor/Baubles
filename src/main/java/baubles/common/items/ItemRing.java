package baubles.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRing extends Item implements IBauble {
	@SideOnly(Side.CLIENT)
	public static IIcon icon[];

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		icon = new IIcon[1];
		icon[0] = r.registerIcon("baubles:ring");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return icon[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item i, CreativeTabs t, List l) {
		l.add(new ItemStack(i, 1, 0));
	}

	@Override
	public BaubleType getBaubleType(ItemStack is) {
		return BaubleType.RING;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World par2World, EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(par3EntityPlayer);
			for (int i = 0; i < baubles.getSizeInventory(); i++)
				if (baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, is)) {
					baubles.setInventorySlotContents(i, is.copy());
					if (!par3EntityPlayer.capabilities.isCreativeMode) {
						par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, null);
					}
					onEquipped(is, par3EntityPlayer);
					break;
				}
		}

		return is;
	}

	@Override
	public void onWornTick(ItemStack is, EntityLivingBase player) {
		if (is.getItemDamage() == 0 && !player.isPotionActive(Potion.digSpeed)) {
			player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 40, 0, true));
		}
	}

	@Override
	public boolean hasEffect(ItemStack is, int a) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.rare;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		return super.getUnlocalizedName() + "." + is.getItemDamage();
	}

	@Override
	public void onEquipped(ItemStack is, EntityLivingBase player) {
		if (!player.worldObj.isRemote) {
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 1.3f);
		}
	}

	@Override
	public void onUnequipped(ItemStack is, EntityLivingBase player) {}

	@Override
	public boolean canEquip(ItemStack is, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack is, EntityLivingBase player) {
		return true;
	}
}