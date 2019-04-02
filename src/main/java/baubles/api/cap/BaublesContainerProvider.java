package baubles.api.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaublesContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

    private final BaublesContainer container;

    public BaublesContainerProvider(BaublesContainer container) {
        this.container = container;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return container.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        container.deserializeNBT(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if (cap == BaublesCapabilities.CAPABILITY_BAUBLES)
            return LazyOptional.of(() -> (T) container);
        else
            return LazyOptional.empty();
    }
}
