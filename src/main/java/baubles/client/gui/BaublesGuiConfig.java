package baubles.client.gui;

import java.util.ArrayList;
import java.util.List;

import baubles.common.Baubles;
import baubles.common.Config;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class BaublesGuiConfig extends GuiConfig {
	public BaublesGuiConfig(GuiScreen parent) {
		super(parent, getConfigElements(), Baubles.MODID, false, false,
				GuiConfig.getAbridgedConfigPath(Config.config.toString()));
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

}
