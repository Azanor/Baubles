package baubles.common.network;

import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class PacketOpenBaublesInventory extends Packet {

    public PacketOpenBaublesInventory() {
    }

    @Override
    void server(EntityPlayerMP player) {
        player.openContainer.onContainerClosed(player);
        NetworkHooks.openGui(player, new InteractionObjectPlayerExpanded());
    }

    @Override
    void encode(Packet packet, PacketBuffer buf) {

    }

    @Override
    Packet decode(PacketBuffer buf) {
        return this;
    }

    private class InteractionObjectPlayerExpanded implements IInteractionObject {

        private String name;

        @Override
        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
            return new ContainerPlayerExpanded(playerInventory, !playerIn.world.isRemote, playerIn);
        }

        @Override
        public String getGuiID() {
            name = "PlayerExpanded".toLowerCase();
            return new ResourceLocation(Baubles.MODID, name).toString();
        }

        @Override
        public ITextComponent getName() {
            return new TextComponentString(name);
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
}
