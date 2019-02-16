package baubles.client.gui;

import baubles.common.Baubles;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class GuiPlayerExpanded extends InventoryEffectRenderer {

	public static final ResourceLocation background =
			new ResourceLocation("baubles","textures/gui/expanded_inventory.png");

	private float oldMouseX;
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

	@Override
	public void tick() {
		((ContainerPlayerExpanded)inventorySlots).baubles.setEventBlock(false);
		resetGuiLeft();
	}

	@Override
	public void initGui() {
		this.buttons.clear();
		super.initGui();
		this.addButton(new GuiBaublesButton(55, this, 64, 9, 10, 10,
				I18n.format("button.normal")));
		resetGuiLeft();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int unknown) {
		if (Baubles.ClientInit.KEY_BAUBLES.isActiveAndMatches(InputMappings.getInputByCode(keyCode, scanCode))) {
			this.mc.player.closeScreen();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, unknown);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(I18n.format("container.crafting"), 115, 8, 4210752);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		this.oldMouseX = (float) mouseX;
		this.oldMouseY = (float) mouseY;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		// todo 1.13: what is this doing?
		for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
		{
			Slot slot = this.inventorySlots.inventorySlots.get(i1);
			if (slot.getHasStack() && slot.getSlotStackLimit()==1)
			{
				this.drawTexturedModalRect(i+slot.xPos, j+slot.yPos, 200, 0, 16, 16);
			}
		}

		GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);
	}
}
