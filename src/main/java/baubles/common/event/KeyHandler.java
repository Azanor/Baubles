package baubles.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C16PacketClientStatus;

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

	@SideOnly(value=Side.CLIENT)
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) return;
		if (event.phase == Phase.START ) {
			if (key.getIsKeyPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
				if (!event.player.capabilities.isCreativeMode)
					Baubles.packetPipeline.sendToServer(new PacketOpenBaublesInventory(event.player));
				else {
					Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					Minecraft.getMinecraft().displayGuiScreen(new GuiContainerCreative(event.player));
				}
					
			}
		}
	}
}
