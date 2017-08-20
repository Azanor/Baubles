package baubles.client.gui;

import java.io.IOException;

import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class GuiPlayerExpanded extends InventoryEffectRenderer {
	
	public static final ResourceLocation background = 
			new ResourceLocation("baubles","textures/gui/expanded_inventory.png");
    
	/** The old x position of the mouse pointer */
	private float oldMouseX;
	/** The old y position of the mouse pointer */
	private float oldMouseY;

	public GuiPlayerExpanded(EntityPlayer player, int mouseX, int mouseY)
	{
		super(new ContainerPlayerExpanded(player.inventory, !player.world.isRemote, player));
		this.allowUserInput = true;
		oldMouseX = mouseX;
		oldMouseY = mouseY;
	}

    /**
     * Called from the main game loop to update the screen.
     */
    @Override 
    public void updateScreen()
    {
    	((ContainerPlayerExpanded)inventorySlots).baubles.setEventBlock(false);
        this.updateActivePotionEffects();
    }

    
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
        this.buttonList.clear();
        super.initGui();
        
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
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        this.oldMouseX = (float)par1;
        this.oldMouseY = (float)par2;
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
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.player.getStatFileWriter()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
        }
    }

	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if (par2 == Baubles.proxy.keyHandler.key.getKeyCode())
        {
            this.mc.player.closeScreen();
        } else
		super.keyTyped(par1, par2);
	}

	public void displayNormalInventory()
	{
		this.mc.displayGuiScreen(new GuiInventory(this.mc.player));
		ReflectionHelper.setPrivateValue(GuiInventory.class, (GuiInventory) this.mc.currentScreen, this.oldMouseX, "oldMouseX", "field_147048_u");
		ReflectionHelper.setPrivateValue(GuiInventory.class, (GuiInventory) this.mc.currentScreen, this.oldMouseY, "oldMouseY", "field_147047_v");
	}
}
