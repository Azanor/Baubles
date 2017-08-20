package baubles.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiBaublesButton extends GuiButton {

	private final int guiLeft;

	public GuiBaublesButton(int buttonId, int guiLeft, int guiTop, int x, int y, int width, int height, String buttonText) {
		super(buttonId, guiLeft + x, guiTop + y, width, height, buttonText);
		this.guiLeft = guiLeft;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return super.mousePressed(mc, mouseX - getPositionShift(mc), mouseY);
	}

	@Override
	public void drawButton(Minecraft mc, int xx, int yy, float p_191745_4_)
	{
		if (this.visible)
		{
			int positionShift = getPositionShift(mc);

			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(GuiPlayerExpanded.background);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = xx >= this.x + positionShift && yy >= this.y &&
					xx < this.x + this.width + positionShift && yy < this.y + this.height;
			int k = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			if (k==1) {
				this.drawTexturedModalRect(this.x + positionShift, this.y, 200, 48, 10, 10);
			} else {
				this.drawTexturedModalRect(this.x + positionShift, this.y, 210, 48, 10, 10);
				this.drawCenteredString(fontrenderer, I18n.format(this.displayString, new Object[0]),
						this.x + 5 + positionShift, this.y + this.height, 0xffffff);
			}

			this.mouseDragged(mc, xx, yy);
		}
	}

	private int getPositionShift(Minecraft mc) {
		if (mc.currentScreen instanceof GuiContainer) {
			GuiContainer guiContainer = (GuiContainer) mc.currentScreen;
			return guiContainer.getGuiLeft() - this.guiLeft;
		}
		return 0;
	}
}
