package baubles.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import baubles.common.Baubles;
import baubles.common.Config;

public class BaublesGuiFactory extends DefaultGuiFactory {

	public BaublesGuiFactory() {
		super(Baubles.MODID, getTitle());
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parent) {
		return new GuiConfig(parent, getConfigElements(), Baubles.MODID, false, false, getTitle());
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();

		list.addAll(new ConfigElement(Config.config
				.getCategory(Configuration.CATEGORY_GENERAL))
				.getChildElements());
		list.addAll(new ConfigElement(Config.config
				.getCategory(Configuration.CATEGORY_CLIENT))
				.getChildElements());

		return list;
	}

	private static String getTitle() {
		return GuiConfig.getAbridgedConfigPath(Config.config.toString());
	}
}
