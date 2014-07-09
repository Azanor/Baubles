package baubles.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import baubles.common.Baubles;

public class Config {
	public static Configuration config;

	public static void init(File configFile) {
		config = new Configuration(configFile);

		try {
			config.load();
			// config properties here
		} catch (Exception e) {
			Baubles.logger.log(Level.ERROR, "An error has occurred while loading configuration properties!", e);
		} finally {
			if (config.hasChanged())
				config.save();
		}
	}
}