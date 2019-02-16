package baubles.common.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Baubles.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRing extends Item
{
	public ItemRing()
	{
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS).rarity(EnumRarity.RARE));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemRing().setRegistryName(Baubles.MODID, "ring"));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound unused) {
		IBauble bauble = new IBauble() {
			@Override
			public BaubleType getBaubleType() {
				return BaubleType.RING;
			}

			@Override
			public void onWornTick(EntityLivingBase player) {
				if (!player.world.isRemote && player.ticksExisted % 39 == 0) {
					player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40, 0, true, true));
				}
			}

			@Override
			public void onEquipped(EntityLivingBase player) {
				player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 1.9f);
			}

			@Override
			public void onUnequipped(EntityLivingBase player) {
				player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 2f);
			}
		};

		return new ICapabilityProvider() {
			private final LazyOptional<IBauble> opt = LazyOptional.of(() -> bauble);
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
				return BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.orEmpty(cap, opt);
			}
		};
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			ItemStack held = player.getHeldItem(hand);
			player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES).ifPresent(baubles -> {
				for(int i = 0; i < baubles.getSlots(); i++)
					if(!baubles.getStackInSlot(i).isEmpty() && baubles.isItemValidForSlot(i, held)) {
						ItemStack split = held.split(1);
						baubles.setStackInSlot(i, split);
						split.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE).ifPresent(b -> b.onEquipped(player));
						break;
					}
			});
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return true;
	}
}
