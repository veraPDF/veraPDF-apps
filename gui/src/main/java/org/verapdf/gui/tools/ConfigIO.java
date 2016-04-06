package org.verapdf.gui.tools;

import org.apache.log4j.Logger;
import org.verapdf.config.Config;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Path;

/**
 * @author Sergey Shemyakov
 */
public class ConfigIO {

	private Path configPath;
	private File configFile;
	boolean isSerializedConfig;

	private static final Logger LOGGER = Logger.getLogger(ConfigIO.class);

	public ConfigIO() {
		configPath = null;
		String appHome = System.getProperty("app.home");
		if (appHome != null) {
			File user = new File(appHome);
			File f = new File(user, "config");
			if (f.exists() || f.mkdir()) {
				configFile = new File(f, "config.properties");
				this.isSerializedConfig = true;
				this.configPath = configFile.toPath();
			}
		}
	}

	public Config readConfig()
			throws IOException, JAXBException, IllegalArgumentException {
		if(configFile == null || configPath == null) {
			return new Config();
		}
		if(!configFile.exists())
			return new Config();
		else if(!configFile.canRead()) {
			throw new IllegalArgumentException("Path should specify read accessible file");
		}
		else {
			FileInputStream inputStream = new FileInputStream(configFile);
			return Config.fromXml(inputStream);
		}
	}

	public void writeConfig(Config config) {
		if(this.isSerializedConfig)
			try {
				FileOutputStream outputStream =
						new FileOutputStream(this.configPath.toFile());        // Can we use configFile?
				BufferedWriter writer =
						new BufferedWriter(new OutputStreamWriter(outputStream));
				writer.write(Config.toXml(config, true));
				writer.close();
			}
			catch (IOException e1) {	// TODO : Is handling exception here OK?
				LOGGER.error("Can not save config", e1);
			}
			catch (JAXBException e1) {
				LOGGER.error("Can not convert config to XML", e1);
			}
	}

	public Config readConfig(Path configPath)
		throws IOException, JAXBException, IllegalArgumentException {
		if(configPath == null)
			throw new IllegalArgumentException("Path should specify a file");
		File configFile = configPath.toFile();
		if(!configFile.exists() || !configFile.canRead())
			throw new IllegalArgumentException("Path should specify existing read accessible file");
		else {
			FileInputStream inputStream = new FileInputStream(configFile);
			return Config.fromXml(inputStream);
		}
	}

	public void writeConfig(Config config, Path configPath)
			throws IOException, JAXBException, IllegalArgumentException{
		if(configPath == null)
			throw new IllegalArgumentException("Path should specify a file");
		File configFile = configPath.toFile();
		if(!configFile.exists() || !configFile.canRead())
			throw new IllegalArgumentException("Path should specify existing read accessible file");
		FileOutputStream outputStream =
				new FileOutputStream(configFile);
		BufferedWriter writer =
				new BufferedWriter(new OutputStreamWriter(outputStream));
		writer.write(Config.toXml(config, true));
		writer.close();
	}
}
