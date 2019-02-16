package baubles.api.cap;

import baubles.api.IBauble;
import net.minecraft.nbt.INBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class BaublesCapabilities {
	@CapabilityInject(IBaublesItemHandler.class)
	public static final Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

	@CapabilityInject(IBauble.class)
	public static final Capability<IBauble> CAPABILITY_ITEM_BAUBLE = null;
}
