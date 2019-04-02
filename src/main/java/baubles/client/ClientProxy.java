
package baubles.client;

import baubles.client.gui.GuiEvents;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import baubles.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    public static final KeyBinding KEY_BAUBLES = new KeyBinding("keybind.baublesinventory", InputMappings.getInputByName("key.keyboard.b").getKeyCode(), "key.categories.inventory");

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();

        ClientRegistry.registerKeyBinding(KEY_BAUBLES);

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new GuiEvents());
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
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
