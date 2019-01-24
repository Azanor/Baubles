package baubles.api.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class BaublesContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {
	private final BaublesContainer inner;
	private final OptionalCapabilityInstance<IBaublesItemHandler> oci;

	public BaublesContainerProvider(BaublesContainer inner) {
		this.inner = inner;
		this.oci = OptionalCapabilityInstance.of(() -> inner);
	}

	@Nonnull
	@Override
	public <T> OptionalCapabilityInstance<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if (capability == BaublesCapabilities.CAPABILITY_BAUBLES) return this.oci.cast();
		return OptionalCapabilityInstance.empty();
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
