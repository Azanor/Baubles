package baubles.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import baubles.common.Baubles;

public class PacketOpenBaublesInventory extends AbstractPacket {
	
	public PacketOpenBaublesInventory() {}
	
	public PacketOpenBaublesInventory(EntityPlayer player) {}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {}

	@Override
	public void handleClientSide(EntityPlayer player) {}

	@Override
	public void handleServerSide(EntityPlayer player) {
		player.openGui(Baubles.instance, Baubles.GUI, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
	}


}
