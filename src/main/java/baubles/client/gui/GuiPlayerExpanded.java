package baubles.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;

public class GuiPlayerExpanded extends InventoryEffectRenderer {
	protected static final ResourceLocation background = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");

	/**
	 * x size of the inventory window in pixels. Defined as float, passed as int
	 */
	private float xSizeFloat;
	/**
	 * y size of the inventory window in pixels. Defined as float, passed as
	 * int.
	 */
	private float ySizeFloat;

	public GuiPlayerExpanded(EntityPlayer player) {
		super(new ContainerPlayerExpanded(player.inventory, !player.worldObj.isRemote, player));
		allowUserInput = true;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen() {}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		buttonList.clear();
		super.initGui();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 106, 16, 4210752);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		xSizeFloat = (float) par1;
		ySizeFloat = (float) par2;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(background);
		int k = guiLeft, l = guiTop;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

		for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); ++i1) {
			Slot slot = (Slot) inventorySlots.inventorySlots.get(i1);
			if (slot.getHasStack() && slot.getSlotStackLimit() == 1)
				drawTexturedModalRect(k + slot.xDisplayPosition, l + slot.yDisplayPosition, 200, 0, 16, 16);
		}

		drawPlayerModel(k + 51, l + 75, 30, (float) (k + 51) - xSizeFloat, (float) (l + 75 - 50) - ySizeFloat, mc.thePlayer);
	}

	public static void drawPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase playerdrawn) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, 50.0F);
		GL11.glScalef((float) (-scale), (float) scale, (float) scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = playerdrawn.renderYawOffset;
		float f3 = playerdrawn.rotationYaw;
		float f4 = playerdrawn.rotationPitch;
		float f5 = playerdrawn.prevRotationYawHead;
		float f6 = playerdrawn.rotationYawHead;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float) Math.atan((double) (pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		playerdrawn.renderYawOffset = (float) Math.atan((double) (yaw / 40.0F)) * 20.0F;
		playerdrawn.rotationYaw = (float) Math.atan((double) (yaw / 40.0F)) * 40.0F;
		playerdrawn.rotationPitch = -((float) Math.atan((double) (pitch / 40.0F))) * 20.0F;
		playerdrawn.rotationYawHead = playerdrawn.rotationYaw;
		playerdrawn.prevRotationYawHead = playerdrawn.rotationYaw;
		GL11.glTranslatef(0.0F, playerdrawn.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(playerdrawn, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		playerdrawn.renderYawOffset = f2;
		playerdrawn.rotationYaw = f3;
		playerdrawn.rotationPitch = f4;
		playerdrawn.prevRotationYawHead = f5;
		playerdrawn.rotationYawHead = f6;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0)
			mc.displayGuiScreen(new GuiAchievements(this, mc.thePlayer.getStatFileWriter()));
		if (button.id == 1)
			mc.displayGuiScreen(new GuiStats(this, mc.thePlayer.getStatFileWriter()));
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 == Baubles.proxy.keyHandler.key.getKeyCode())
			mc.thePlayer.closeScreen();
		else
			super.keyTyped(par1, par2);
	}
}