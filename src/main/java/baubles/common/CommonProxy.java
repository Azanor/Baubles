
package baubles.common;

import baubles.common.event.EventHandlerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.event.EventHandlerEntity;

public class CommonProxy implements IGuiHandler {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
			case Baubles.GUI: return new ContainerPlayerExpanded(player.inventory, !world.isRemote, player);
		}
		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new EventHandlerEntity());
		MinecraftForge.EVENT_BUS.register(new EventHandlerItem());
	}

	public void init() { }
}
