package baubles.common.network;

import baubles.common.Baubles;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenBaublesInventory implements IMessage, IMessageHandler<PacketOpenBaublesInventory, IMessage> {

	int mouseX, mouseY;

	public PacketOpenBaublesInventory() {}

	public PacketOpenBaublesInventory(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(mouseX);
		buffer.writeInt(mouseY);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		mouseX = buffer.readInt();
		mouseY = buffer.readInt();
	}

	@Override
	public IMessage onMessage(PacketOpenBaublesInventory message, MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; 
        mainThread.addScheduledTask(new Runnable(){ public void run() { 	        	
        	ctx.getServerHandler().player.openContainer.onContainerClosed(ctx.getServerHandler().player);
			ctx.getServerHandler().player.openGui(Baubles.instance, Baubles.GUI, ctx.getServerHandler().player.world, message.mouseX, message.mouseY, 0);
		}});
		return null;
	}


}
