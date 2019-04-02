
package baubles.common;

import baubles.common.event.EventHandlerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.event.EventHandlerEntity;

public class CommonProxy{

	public World getClientWorld() {
		return null;
	}

	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new EventHandlerEntity());
		MinecraftForge.EVENT_BUS.register(new EventHandlerItem());
	}

	public void init() { }
}
