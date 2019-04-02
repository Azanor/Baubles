package baubles.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketOpenNormalInventory extends Packet {

    public PacketOpenNormalInventory() {
    }

    @Override
    void server(EntityPlayerMP player) {
        player.openContainer.onContainerClosed(player);
        player.openContainer = player.inventoryContainer;
    }

    @Override
    void encode(Packet packet, PacketBuffer buf) {

    }

    @Override
    Packet decode(PacketBuffer buf) {
        return this;
    }
}
