
package baubles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import baubles.client.gui.GuiEvents;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import baubles.common.CommonProxy;
import baubles.common.Config;
import baubles.common.event.KeyHandler;

public class ClientProxy extends CommonProxy {
	
	
	@Override
	public void registerKeyBindings() {
		keyHandler = new KeyHandler();
		FMLCommonHandler.instance().bus().register(keyHandler);
		MinecraftForge.EVENT_BUS.register(new GuiEvents());
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (world instanceof WorldClient) {
			switch (ID) {
				case Baubles.GUI: return new GuiPlayerExpanded(player);
			}
		}
		return null;
	}
				
	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
	
	@Override
	public void registerItemModels() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Config.itemRing, 0, new ModelResourceLocation(Baubles.MODID + ":ring", "inventory"));
		ModelBakery.addVariantName(Config.itemRing, Baubles.MODID + ":ring");
	}
}
