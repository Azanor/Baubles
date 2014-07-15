package baubles.common.event;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KeyHandler {
	public KeyBinding key = new KeyBinding("Baubles Inventory", Keyboard.KEY_B, "key.categories.inventory");

	public KeyHandler() {
		ClientRegistry.registerKeyBinding(key);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER)
			return;
		if (event.phase == Phase.START)
			if (key.getIsKeyPressed() && FMLClientHandler.instance().getClient().inGameHasFocus)
				PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory(event.player));
	}
}