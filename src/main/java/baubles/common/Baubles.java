package baubles.common; 

import java.nio.file.Paths;
import java.util.Map;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.client.BaublesRenderLayer;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.event.CommandBaubles;
import baubles.common.network.BaublesInteractionObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.INBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.network.PacketHandler;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

@Mod(Baubles.MODID)
public class Baubles {
	public static final String MODID = "baubles";
	public static final Logger log = LogManager.getLogger(MODID.toUpperCase());

	public Baubles() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		MinecraftForge.EVENT_BUS.addListener(this::serverLoad);
	}

	private void preInit(FMLCommonSetupEvent event) {
		Config.initialize(Paths.get("config", MODID + ".toml"));

		CapabilityManager.INSTANCE.register(IBaublesItemHandler.class,
				new DummyStorage<>(), () -> new BaublesContainer(null));

		CapabilityManager.INSTANCE
				.register(IBauble.class, new DummyStorage<>(), () -> new IBauble() {
					@Override
					public BaubleType getBaubleType() {
						return BaubleType.TRINKET;
					}
				});

		PacketHandler.init();
	}

	@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientInit {
		public static final KeyBinding KEY_BAUBLES = new KeyBinding("keybind.baublesinventory", GLFW.GLFW_KEY_B, "key.categories.inventory");

		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent evt) {
			ClientRegistry.registerKeyBinding(KEY_BAUBLES);
			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> ClientInit::handleGui);
		}

		@SubscribeEvent
		public static void postLoad(FMLLoadCompleteEvent evt) {
			// FMLClientSetup is too early to do this
			Map<String, RenderPlayer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
			RenderPlayer render;
			render = skinMap.get("default");
			render.addLayer(new BaublesRenderLayer());

			render = skinMap.get("slim");
			render.addLayer(new BaublesRenderLayer());
		}

		private static GuiScreen handleGui(FMLPlayMessages.OpenContainer msg) {
			if (BaublesInteractionObject.ID.equals(msg.getId())) {
				return new GuiPlayerExpanded(Minecraft.getInstance().player);
			}
			return null;
		}
	}

	private static class DummyStorage<T> implements Capability.IStorage<T> {
		@Nullable
		@Override
		public INBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) { return null; }

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, INBTBase nbt) { }
	}

	private void serverLoad(FMLServerStartingEvent event)
	{
		CommandBaubles.register(event.getCommandDispatcher());
	}
}
