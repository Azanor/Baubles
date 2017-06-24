
package baubles.client;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import baubles.client.gui.GuiEvents;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import baubles.common.CommonProxy;
import baubles.common.event.KeyHandler;
import baubles.common.items.ItemRing;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void registerKeyBindings() {
		keyHandler = new KeyHandler();
		MinecraftForge.EVENT_BUS.register(new GuiEvents());
		MinecraftForge.EVENT_BUS.register(keyHandler);
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
		return FMLClientHandler.instance().getClient().world;
	}

	@SubscribeEvent
	public static void registerItemModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(ItemRing.RING, 0, new ModelResourceLocation("baubles:ring", "inventory"));
	}

	@Override
	public void init() {
		Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		RenderPlayer render;
		render = skinMap.get("default");
		render.addLayer(new BaublesRenderLayer());

		render = skinMap.get("slim");
		render.addLayer(new BaublesRenderLayer());
	}
}
