package baubles.common.event;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.common.Baubles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventHandlerItem {
    private static ResourceLocation capabilityResourceLocation = new ResourceLocation(Baubles.MODID, "bauble_cap");

    /**
     * Handles backwards compatibility with items that implement IBauble instead of exposing it as a capability.
     * This adds a IBauble capability wrapper for all items, if the item:
     * - does implement the IBauble interface
     * - does not already have the capability
     * - did not get the capability by another event handler earlier in the chain
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.isEmpty() || !(stack.getItem() instanceof IBauble) || stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).isPresent()
                || event.getCapabilities().values().stream().anyMatch(c -> c.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).isPresent()))
            return;

        event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
                return cap == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE
                        ? LazyOptional.of(() -> (T) stack.getItem())
                        : LazyOptional.empty();
            }
        });
    }
}
