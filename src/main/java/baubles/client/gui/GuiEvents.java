package baubles.client.gui;

import java.lang.reflect.Method;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import baubles.common.network.PacketOpenNormalInventory;

public class GuiEvents {	
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {

		if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiPlayerExpanded) {
			
			int xSize = 176;
		    int ySize = 166;
			
			int guiLeft = (event.getGui().width - xSize) / 2;
	        int guiTop = (event.getGui().height - ySize) / 2;
			
			event.getButtonList().add(new GuiBaublesButton(55, guiLeft + 64, guiTop + 9, 10, 10, 
					I18n.format((event.getGui() instanceof GuiInventory)?"button.baubles":"button.normal", new Object[0])));
		}
		
	}

	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event) {

		if (event.getGui() instanceof GuiInventory) {
			if (event.getButton().id == 55) {
				PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory(event.getGui().mc.thePlayer));
			}
		}
		
		if (event.getGui() instanceof GuiPlayerExpanded) {
			if (event.getButton().id == 55) {
				event.getGui().mc.displayGuiScreen(new GuiInventory(event.getGui().mc.thePlayer));
				PacketHandler.INSTANCE.sendToServer(new PacketOpenNormalInventory(event.getGui().mc.thePlayer));
			}
		}
	}
}
