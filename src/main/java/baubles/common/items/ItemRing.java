package baubles.common.items;

import java.util.List;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRing  extends Item implements IBauble
{

	public ItemRing()
	{
		super();
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		setCreativeTab(CreativeTabs.TOOLS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,List par3List) {
		par3List.add(new ItemStack(this,1,0));
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.RING;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) { 
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
			for(int i = 0; i < baubles.getSlots(); i++)
				if(baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, stack, player)) {
					baubles.setStackInSlot(i, stack.copy());
					if(!player.capabilities.isCreativeMode){
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}
					onEquipped(stack, player);
					break;
				}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);	
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if (itemstack.getItemDamage()==0 && player.ticksExisted%39==0) {
			player.addPotionEffect(new PotionEffect(MobEffects.HASTE,40,0,true,true));
		}
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.RARE;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		return super.getUnlocalizedName() + "." + par1ItemStack.getItemDamage();
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		if (!player.worldObj.isRemote) {
			player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.1F, 1.3f);
		}
	}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
	}
	
	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}
	
	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

}
