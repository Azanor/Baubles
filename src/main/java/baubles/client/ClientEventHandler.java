package baubles.client;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.common.Baubles;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void registerItemModels(ModelRegistryEvent event) {
        //ModelLoader.setCustomModelResourceLocation(ItemRing.RING, 0, new ModelResourceLocation("baubles:ring", "inventory"));
    }

    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT && event.phase == Phase.START) {
            if (ClientProxy.KEY_BAUBLES.isPressed() && Minecraft.getInstance().isGameFocused()) {
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
