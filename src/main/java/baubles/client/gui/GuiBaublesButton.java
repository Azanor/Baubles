package baubles.client.gui;

import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import baubles.common.network.PacketOpenNormalInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiBaublesButton extends GuiButton {

    private final GuiContainer parentGui;
    private Minecraft mc = Minecraft.getInstance();

    public GuiBaublesButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height, String buttonText) {
        super(buttonId, x, parentGui.getGuiTop() + y, width, height, buttonText);
        this.parentGui = parentGui;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int i) {
        boolean pressed = super.mouseClicked(mouseX - parentGui.getGuiLeft(), mouseY, i);
        if (pressed) {
            if (parentGui instanceof GuiInventory) {
                System.out.println("test1");
                PacketHandler.sendToServer(new PacketOpenBaublesInventory());
            } else {
                ((GuiPlayerExpanded) parentGui).displayNormalInventory();
                PacketHandler.sendToServer(new PacketOpenNormalInventory());
            }
        }
        return pressed;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            int x = this.x + parentGui.getGuiLeft();

            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiPlayerExpanded.background);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            int k = getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0, 200);
            if (k == 1) {
                drawTexturedModalRect(x, y, 200, 48, 10, 10);
            } else {
                drawTexturedModalRect(x, y, 210, 48, 10, 10);
                drawCenteredString(fontrenderer, I18n.format(displayString), x + 5, y + height, 0xffffff);
            }
            GlStateManager.popMatrix();

            mouseDragged(mouseX, mouseY, 0, 0, 0);
        }
    }
}
