package baubles.common.event;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;

public class KeyHandler {

	public KeyBinding key = new KeyBinding("keybind.baublesinventory", Keyboard.KEY_B, "key.categories.inventory");

	public KeyHandler() {
		 ClientRegistry.registerKeyBinding(key);
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) return;
		if (event.phase == Phase.START ) {
			if (key.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
					PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory(event.player));
			}
		}
	}
}

