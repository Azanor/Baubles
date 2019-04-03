package baubles.common;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.client.ClientProxy;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.items.ItemRing;
import baubles.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Baubles.MODID)
public class Baubles {

    public static final String MODID = "baubles";
    public static final String MODNAME = "Baubles";
    public static final String VERSION = "1.5.2";

    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static Baubles instance;


    public static final Logger log = LogManager.getLogger(MODID.toUpperCase());

    public Baubles() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        try {
            ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.spec);
            Config.loadConfig(Config.spec, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
        } catch (Exception e) {
            Baubles.log.error("BAUBLES has a problem loading it's configuration");
        }
    }

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {

        CapabilityManager.INSTANCE.register(IBaublesItemHandler.class,
                new BaublesCapabilities.CapabilityBaubles<>(), BaublesContainer::new);

        CapabilityManager.INSTANCE
                .register(IBauble.class, new BaublesCapabilities.CapabilityItemBaubleStorage(), () -> new BaubleItem(BaubleType.TRINKET));

        proxy.registerEventHandlers();
        PacketHandler.init();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY,
                () -> openContainer -> new GuiPlayerExpanded(Minecraft.getInstance().player));
        //proxy.init();
    }

    @SubscribeEvent
    public void clientInit(FMLLoadCompleteEvent event) {
        proxy.init();
    }

    @SubscribeEvent
    public void serverLoad(FMLServerStartingEvent event) {
        //todo: add command registration
    }
}
