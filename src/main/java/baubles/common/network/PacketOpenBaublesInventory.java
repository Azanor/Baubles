package baubles.common.network;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import baubles.common.Baubles;
import io.netty.buffer.ByteBuf;

public class PacketOpenBaublesInventory implements IMessage, IMessageHandler<PacketOpenBaublesInventory, IMessage> {

	public PacketOpenBaublesInventory() {}

	@Override
	public void toBytes(ByteBuf buffer) {}

	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public IMessage onMessage(PacketOpenBaublesInventory message, MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
		mainThread.addScheduledTask(new Runnable(){ public void run() {
			ctx.getServerHandler().player.openContainer.onContainerClosed(ctx.getServerHandler().player);
			ctx.getServerHandler().player.openGui(Baubles.instance, Baubles.GUI, ctx.getServerHandler().player.world, 0, 0, 0);
		}});
		return null;
	}
}
