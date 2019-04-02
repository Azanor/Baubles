package baubles.client.gui;

import baubles.client.ClientProxy;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class GuiPlayerExpanded extends InventoryEffectRenderer {

    public static final ResourceLocation background =
            new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");

    /**
     * The old x position of the mouse pointer
     */
    private float oldMouseX;
    /**
     * The old y position of the mouse pointer
     */
    private float oldMouseY;

    public GuiPlayerExpanded(EntityPlayer player) {
        super(new ContainerPlayerExpanded(player.inventory, !player.getEntityWorld().isRemote, player));
        allowUserInput = true;
    }

    private void resetGuiLeft() {
        guiLeft = (width - xSize) / 2;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {
        ((ContainerPlayerExpanded) inventorySlots).baubles.setEventBlock(false);
        updateActivePotionEffects();
        resetGuiLeft();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        buttons.clear();
        super.initGui();
        resetGuiLeft();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 115, 8, 4210752);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
        int k = guiLeft;
        int l = guiTop;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i1);
            if (slot.getHasStack() && slot.getSlotStackLimit() == 1) {
                drawTexturedModalRect(k + slot.xPos, l + slot.yPos, 200, 0, 16, 16);
            }
        }

        GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float) (k + 51) - oldMouseX, (float) (l + 75 - 50) - oldMouseY, mc.player);
    }

    /*

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            //this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.player.getStatFileWriter()));
        }

        if (button.id == 1) {
            mc.displayGuiScreen(new GuiStats(this, mc.player.getStats()));
        }
    }*/

    @Override
    public boolean keyPressed(int par1, int par2, int par3) {
        if (par2 == ClientProxy.KEY_BAUBLES.getKey().getKeyCode()) {
            mc.player.closeScreen();
            return true;
        } else
            return super.keyPressed(par1, par2, par3);
    }

    public void displayNormalInventory() {
        GuiInventory gui = new GuiInventory(mc.player);
        try {
            ObfuscationReflectionHelper.setPrivateValue(GuiInventory.class, gui, oldMouseX, "oldMouseX"/*, "field_147048_u"*/);
            ObfuscationReflectionHelper.setPrivateValue(GuiInventory.class, gui, oldMouseY, "oldMouseY"/*, "field_147047_v"*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mc.displayGuiScreen(gui);
    }
}
