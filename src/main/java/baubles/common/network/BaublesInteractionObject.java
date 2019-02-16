package baubles.common.network;

import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaublesInteractionObject implements IInteractionObject {
    public static final ResourceLocation ID = new ResourceLocation(Baubles.MODID, "container");

    @Nonnull
    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
        return new ContainerPlayerExpanded(playerInventory, !player.world.isRemote, player);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return ID.toString();
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return new TextComponentString(getGuiID());
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
    }
}
