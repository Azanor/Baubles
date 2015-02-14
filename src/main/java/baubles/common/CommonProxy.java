
package baubles.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.event.KeyHandler;

public class CommonProxy implements IGuiHandler {
	
	public KeyHandler keyHandler;
	
	public void registerHandlers() {}
	
	private static final Map<String, NBTTagCompound> baublesIDData = new HashMap<String, NBTTagCompound>();
	

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
		
	
	public void registerKeyBindings() {}
	
	public static void storeBaublesID(String name, NBTTagCompound compound){
		baublesIDData.put(name, compound);
	}

	public static NBTTagCompound getBaublesID(String name){
	return baublesIDData.remove(name);
	}

	public void registerTextures() {}
}
