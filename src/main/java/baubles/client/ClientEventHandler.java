package baubles.client;

import baubles.api.cap.BaublesCapabilities;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.items.ItemRing;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;

public class ClientEventHandler
{
	@SubscribeEvent
	public void registerItemModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(ItemRing.RING, 0, new ModelResourceLocation("baubles:ring", "inventory"));
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.CLIENT && event.phase == Phase.START ) {
			if (ClientProxy.KEY_BAUBLES.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
					PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
			}
		}
	}

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        LazyOptional<IBauble> bauble = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        bauble.ifPresent(b -> {
            if (!event.getItemStack().isEmpty()) {
                BaubleType bt = b.getBaubleType(event.getItemStack());
                event.getToolTip().add(new TextComponentString(TextFormatting.GOLD + I18n.format("name." + bt)));
            }
        });
    }
}
