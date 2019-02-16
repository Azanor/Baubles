package baubles.client.gui;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

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

public class GuiBaublesButton extends GuiButton {

	private final GuiContainer parentGui;

	public GuiBaublesButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height, String buttonText) {
		super(buttonId, x + parentGui.getGuiLeft(), parentGui.getGuiTop() + y, width, height, buttonText);
		this.parentGui = parentGui;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (parentGui instanceof GuiInventory) {
			PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
		} else {
			PacketHandler.INSTANCE.sendToServer(new PacketOpenNormalInventory());
			this.displayNormalInventory(mouseX, mouseY);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
			Minecraft.getInstance().getTextureManager().bindTexture(GuiPlayerExpanded.background);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
			int k = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0, 0, 200);
			if (k==1) {
				this.drawTexturedModalRect(x, this.y, 200, 48, 10, 10);
			} else {
				this.drawTexturedModalRect(x, this.y, 210, 48, 10, 10);
				this.drawCenteredString(fontrenderer, I18n.format(this.displayString), x + 5, this.y + this.height, 0xffffff);
			}
			GlStateManager.popMatrix();
		}
	}

	private void displayNormalInventory(double oldMouseX, double oldMouseY) {
		GuiInventory gui = new GuiInventory(Minecraft.getInstance().player);
		ObfuscationReflectionHelper.setPrivateValue(GuiInventory.class, gui, (float) oldMouseX, "field_147048_u");
		ObfuscationReflectionHelper.setPrivateValue(GuiInventory.class, gui, (float) oldMouseY, "field_147047_v");
		Minecraft.getInstance().displayGuiScreen(gui);
	}
}
