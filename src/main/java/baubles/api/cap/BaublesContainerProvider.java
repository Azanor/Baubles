package baubles.api.cap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class BaublesContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {
	private final BaublesContainer inner;
	private final LazyOptional<IBaublesItemHandler> opt;

	public BaublesContainerProvider(EntityPlayer player) {
		this.inner = new BaublesContainer(player);
		this.opt = LazyOptional.of(() -> inner);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		return BaublesCapabilities.CAPABILITY_BAUBLES.orEmpty(capability, opt);
	}

	@Override
	public NBTTagCompound serializeNBT () {
		return this.inner.serializeNBT();
	}

	@Override
	public void deserializeNBT (NBTTagCompound nbt) {
		this.inner.deserializeNBT(nbt);
	}
}
