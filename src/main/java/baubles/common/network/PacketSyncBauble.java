package baubles.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import baubles.common.lib.PlayerHandler;

public class PacketSyncBauble extends AbstractPacket {
	
	int slot;
	ItemStack bauble=null;
	
	public PacketSyncBauble() {}
	
	public PacketSyncBauble(EntityPlayer player, int slot) {
		this.slot = slot;
		this.bauble = PlayerHandler.getPlayerBaubles(player).getStackInSlot(slot);
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeByte(slot);
		PacketBuffer pb = new PacketBuffer(buffer);
		try { pb.writeItemStackToBuffer(bauble); } catch (IOException e) {}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) 
	{
		slot = buffer.readByte();
		PacketBuffer pb = new PacketBuffer(buffer);
		try { bauble = pb.readItemStackFromBuffer(); } catch (IOException e) {}
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		PlayerHandler.getPlayerBaubles(player).stackList[slot]=bauble;
	}

	@Override
	public void handleServerSide(EntityPlayer player) {}


}
