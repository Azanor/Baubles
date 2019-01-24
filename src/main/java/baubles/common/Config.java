package baubles.common;

import java.io.File;
import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public class Config {

	public static CommentedFileConfig config;
	public static boolean renderBaubles=true;

	public static void initialize(Path file)
	{
		config = CommentedFileConfig.builder(file).build();
		config.load();

		Boolean val = config.get("client.baubleRender.enabled");
		if (val != null) {
			renderBaubles = val;
		} else {
			String desc = "Set this to false to disable rendering of baubles in the player.";
			config.set("client.baubleRender.enabled", true);
			config.setComment("client.baubleRender.enabled", desc);
		}

		config.save();
	}
}
