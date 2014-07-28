package baubles.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

public class GuiBaublesButton extends GuiButton {

	public GuiBaublesButton(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_,
			int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
		super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
	}
	
	public void drawButton(Minecraft mc, int xx, int yy)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiPlayerExpanded.background);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = xx >= this.xPosition && yy >= this.yPosition && xx < this.xPosition + this.width && yy < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
            
            if (k==1) {
            	this.drawTexturedModalRect(this.xPosition, this.yPosition, 200, 48, 10, 10);	
            } else {
            	this.drawTexturedModalRect(this.xPosition, this.yPosition, 210, 48, 10, 10);
            	this.drawCenteredString(fontrenderer, this.displayString, 
            			this.xPosition + 5, this.yPosition + this.height, 0xffffff);
            }
            
            this.mouseDragged(mc, xx, yy);
            
            
        }
    }

}
