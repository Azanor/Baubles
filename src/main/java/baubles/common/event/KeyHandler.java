package baubles.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import baubles.common.Baubles;
import baubles.common.network.PacketOpenBaublesInventory;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KeyHandler {
	
	public KeyBinding key = new KeyBinding("Baubles Inventory", 
			Keyboard.KEY_B, "key.categories.inventory");
	
	public KeyHandler() {
		 ClientRegistry.registerKeyBinding(key);
	}
	
	boolean keyDown = false;

	@SideOnly(value=Side.CLIENT)
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) return;
		if (event.phase == Phase.START ) {
			if (GameSettings.isKeyDown(key))
            {
				if (!keyDown) {
					if (FMLClientHandler.instance().getClient().inGameHasFocus) {
						Baubles.packetPipeline.sendToServer(new PacketOpenBaublesInventory(event.player));					
					} else 
					if (Minecraft.getMinecraft().currentScreen!=null && 
						Minecraft.getMinecraft().currentScreen instanceof GuiContainer){
						Minecraft.getMinecraft().thePlayer.closeScreen();
					}
				}
                keyDown = true;
            } else {
            	keyDown = false;
            }
		}
	}
}
