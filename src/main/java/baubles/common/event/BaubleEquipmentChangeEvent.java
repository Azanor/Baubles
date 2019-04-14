package baubles.common.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nonnull;

public class BaubleEquipmentChangeEvent extends LivingEvent {
    private final int slot;
    private final ItemStack from;
    private final ItemStack to;

    public BaubleEquipmentChangeEvent(EntityLivingBase entity, int slot, @Nonnull ItemStack from, @Nonnull ItemStack to) {
        super(entity);
        this.slot = slot;
        this.from = from;
        this.to = to;
    }

    public int getSlot() {
        return this.slot;
    }

    @Nonnull
    public ItemStack getFrom() {
        return this.from;
    }

    @Nonnull
    public ItemStack getTo() {
        return this.to;
    }
}
