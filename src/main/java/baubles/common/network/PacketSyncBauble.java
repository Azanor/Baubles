package baubles.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import baubles.common.Baubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncBauble implements IMessage, IMessageHandler<PacketSyncBauble, IMessage> {
	
	int slot;
	int playerId;
	ItemStack bauble=null;
	
	public PacketSyncBauble() {}
	
	public PacketSyncBauble(EntityPlayer player, int slot) {
		this.slot = slot;
		this.bauble = PlayerHandler.getPlayerBaubles(player).getStackInSlot(slot);
		this.playerId = player.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeByte(slot);
		buffer.writeInt(playerId);
		PacketBuffer pb = new PacketBuffer(buffer);
		try { pb.writeItemStackToBuffer(bauble); } catch (IOException e) {}
	}

	@Override
	public void fromBytes(ByteBuf buffer) 
	{
		slot = buffer.readByte();
		playerId = buffer.readInt();
		PacketBuffer pb = new PacketBuffer(buffer);
		try { bauble = pb.readItemStackFromBuffer(); } catch (IOException e) {}
	}

	@Override
	public IMessage onMessage(PacketSyncBauble message, MessageContext ctx) {
		World world = Baubles.proxy.getClientWorld();
		if (world==null) return null;
		Entity p = world.getEntityByID(message.playerId);
		if (p !=null && p instanceof EntityPlayer) {
			PlayerHandler.getPlayerBaubles((EntityPlayer) p).stackList[message.slot]=message.bauble;
		}
		return null;
	}


}
