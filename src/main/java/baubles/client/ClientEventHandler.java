package baubles.client;

import baubles.api.cap.BaublesCapabilities;
import baubles.common.Baubles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.items.ItemRing;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;

@Mod.EventBusSubscriber(modid = Baubles.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.phase == Phase.START ) {
			if (ClientProxy.KEY_BAUBLES.isPressed() && Minecraft.getInstance().isGameFocused()) {
					PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
			}
		}
	}

	@SubscribeEvent
	public static void tooltipEvent(ItemTooltipEvent event) {
		if (!event.getItemStack().isEmpty()) {
			OptionalCapabilityInstance<IBauble> cap = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
			cap.ifPresent(bauble -> {
				BaubleType bt = bauble.getBaubleType(event.getItemStack());
				TextComponentTranslation text = new TextComponentTranslation("name." + bt);
				text.getStyle().setColor(TextFormatting.GOLD);
				event.getToolTip().add(text);
			});
		}
	}
}
