package baubles.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import baubles.client.ClientProxy;
import baubles.common.container.ContainerPlayerExpanded;

public class GuiPlayerExpanded extends InventoryEffectRenderer {

	public static final ResourceLocation background =
			new ResourceLocation("baubles","textures/gui/expanded_inventory.png");

	/** The old x position of the mouse pointer */
	private float oldMouseX;
	/** The old y position of the mouse pointer */
	private float oldMouseY;

	public GuiPlayerExpanded(EntityPlayer player)
	{
		super(new ContainerPlayerExpanded(player.inventory, !player.getEntityWorld().isRemote, player));
		this.allowUserInput = true;
	}

	private void resetGuiLeft()
	{
		this.guiLeft = (this.width - this.xSize) / 2;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override 
	public void updateScreen()
	{
		((ContainerPlayerExpanded)inventorySlots).baubles.setEventBlock(false);
		updateActivePotionEffects();
		resetGuiLeft();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		this.buttonList.clear();
		super.initGui();
		resetGuiLeft();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
	{
		this.fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 115, 8, 4210752);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		this.oldMouseX = (float) mouseX;
		this.oldMouseY = (float) mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int k = this.guiLeft;
		int l = this.guiTop;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
			if (slot.getHasStack() && slot.getSlotStackLimit()==1)
			{
				this.drawTexturedModalRect(k+slot.xPos, l+slot.yPos, 200, 0, 16, 16);
			}
		}

		GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float)(k + 51) - this.oldMouseX, (float)(l + 75 - 50) - this.oldMouseY, this.mc.player);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 0)
		{
			//this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.player.getStatFileWriter()));
		}

		if (button.id == 1)
		{
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if (par2 == ClientProxy.KEY_BAUBLES.getKeyCode())
		{
			this.mc.player.closeScreen();
		} else
		super.keyTyped(par1, par2);
	}

	public void displayNormalInventory()
	{
		GuiInventory gui = new GuiInventory(this.mc.player);
		ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseX, "oldMouseX", "field_147048_u");
		ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseY, "oldMouseY", "field_147047_v");
		this.mc.displayGuiScreen(gui);
	}
}
