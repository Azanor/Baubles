/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Aug 27, 2014, 8:55:00 PM (GMT)]
 */
package baubles.client;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import baubles.common.Config;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class BaublesRenderLayer implements LayerRenderer<EntityPlayer> {

	@Override
	public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		
		if(!Config.renderBaubles || player.getActivePotionEffect(MobEffects.INVISIBILITY) != null)
			return;

		IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);

		dispatchRenders(inv, player, RenderType.BODY, partialTicks);

		float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
		float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
		float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.rotate(yawOffset, 0, -1, 0);
		GlStateManager.rotate(yaw - 270, 0, 1, 0);
		GlStateManager.rotate(pitch, 0, 0, 1);
		dispatchRenders(inv, player, RenderType.HEAD, partialTicks);
		GlStateManager.popMatrix();
	}

	private void dispatchRenders(IBaublesItemHandler inv, EntityPlayer player, RenderType type, float partialTicks) {
		for(int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && !stack.isEmpty()) {
				Item item = stack.getItem();
				if(item instanceof IRenderBauble) {
					GlStateManager.pushMatrix();
					GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255); 
					GlStateManager.color(1F, 1F, 1F, 1F);
					((IRenderBauble) stack.getItem()).onPlayerBaubleRender(stack, player, type, partialTicks);
					GlStateManager.popMatrix();
				}
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}