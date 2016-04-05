package org.verapdf.gui.tools;

import org.verapdf.gui.PDFValidationApplication;
import org.verapdf.gui.config.Config;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sergey Shemyakov
 */
public class ConfigIO {

	private Path configPath;
	private File configFile;

	public ConfigIO() {
		configPath = null;
		String appHome = System.getProperty("app.home");
		if (appHome != null) {
			File user = new File(appHome);
			File f = new File(user, "config");
			if (f.exists() || f.mkdir()) {
				configFile = new File(f, "config.properties");
				this.configPath = configFile.toPath();
			}
		}
	}

	public Config readConfig()
			throws IOException, JAXBException, IllegalArgumentException {
		if(!configFile.exists() || configPath == null) {
			return new Config();
		}
		else if(!configFile.canRead()) {
			throw new IllegalArgumentException("Path should specify read accessible file");
		}
		else {
			FileInputStream inputStream = new FileInputStream(configFile);
			return Config.fromXml(inputStream);
		}
	}

	public void writeConfig(Config config) throws IOException, JAXBException{
		FileOutputStream outputStream =
				new FileOutputStream(this.configPath.toFile());		// Can we write configFile?
		BufferedWriter writer =
				new BufferedWriter(new OutputStreamWriter(outputStream));
		writer.write(Config.toXml(config, true));
		writer.close();
	}

	public Config readConfig(Path configPath)
		throws IOException, JAXBException, IllegalArgumentException {
		if(configPath == null)
			return new Config();
		File configFile = configPath.toFile();
			if(!configFile.exists() || configPath == null) {
				return new Config();
			}
			else if(!configFile.canRead()) {
				throw new IllegalArgumentException("Path should specify read accessible file");
			}
			else {
				FileInputStream inputStream = new FileInputStream(configFile);
				return Config.fromXml(inputStream);
			}
		}
}
