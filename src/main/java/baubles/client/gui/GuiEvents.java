package baubles.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiEvents {

    @SubscribeEvent
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiPlayerExpanded) {
            GuiContainer gui = (GuiContainer) event.getGui();
            event.addButton(new GuiBaublesButton(55, gui, 64, 9, 10, 10,
                    I18n.format((event.getGui() instanceof GuiInventory) ? "button.baubles" : "button.normal")));
        }
    }
}
