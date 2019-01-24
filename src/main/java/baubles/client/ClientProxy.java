
package baubles.client;

import java.util.Map;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import baubles.common.CommonProxy;

public class ClientProxy extends CommonProxy {

	public static final KeyBinding KEY_BAUBLES = new KeyBinding("keybind.baublesinventory", GLFW.GLFW_KEY_B, "key.categories.inventory");

	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();

		ClientRegistry.registerKeyBinding(KEY_BAUBLES);
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
	public void init() {
		Map<String, RenderPlayer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
		RenderPlayer render;
		render = skinMap.get("default");
		render.addLayer(new BaublesRenderLayer());

		render = skinMap.get("slim");
		render.addLayer(new BaublesRenderLayer());
	}
}
