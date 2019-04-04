package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public class PacketSync extends Packet {

    public int playerId;
    public byte slot = 0;
    public ItemStack bauble;

    public PacketSync() {
    }

    public PacketSync(EntityPlayer p, int slot, ItemStack bauble) {
        this.slot = (byte) slot;
        this.bauble = bauble;
        playerId = p.getEntityId();
    }

    @Override
    void client(EntityPlayerSP player) {
        World world = Baubles.proxy.getClientWorld();
        if (world == null) return;
        Entity p = world.getEntityByID(playerId);
        if (p instanceof EntityPlayer) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) p);
            baubles.setStackInSlot(slot, bauble);
        }
    }

    @Override
    void encode(Packet packet, PacketBuffer buf) {
        PacketSync packetSync = (PacketSync) packet;
        buf.writeInt(packetSync.playerId);
        buf.writeByte(packetSync.slot);
        buf.writeItemStack(packetSync.bauble);
    }

    @Override
    Packet decode(PacketBuffer buf) {
        playerId = buf.readInt();
        slot = buf.readByte();
        bauble = buf.readItemStack();
        return this;
    }
}
