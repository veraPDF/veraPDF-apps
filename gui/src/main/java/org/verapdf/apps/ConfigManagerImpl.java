/**
 * 
 */
package org.verapdf.apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;

import javax.xml.bind.JAXBException;

import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 31 Oct 2016:09:20:14
 */

final class ConfigManagerImpl implements ConfigManager {
	private static final String defaultConfExt = ".xml";
	private static final String defaultValidName = "validator" + defaultConfExt;
	private static final String defaultFixerName = "fixer" + defaultConfExt;
	private static final String defaultFeaturesName = "features" + defaultConfExt;
	private static final String defaultAppName = "app" + defaultConfExt;
	private final File root;
	private final File validatorFile;
	private final File fixerFile;
	private final File featuresFile;
	private final File appFile;

	private ConfigManagerImpl(final File root) {
		this.root = root;
		this.validatorFile = getConfigFile(defaultValidName);
		this.fixerFile = getConfigFile(defaultFixerName);
		this.featuresFile = getConfigFile(defaultFeaturesName);
		this.appFile = getConfigFile(defaultAppName);
		this.initialise();
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getValidatorConfig()
	 */
	@Override
	public ValidatorConfig getValidatorConfig() {
		try (InputStream fis = new FileInputStream(this.validatorFile)) {
			return ValidatorFactory.createConfig(fis);
		} catch (IOException | JAXBException excep) {
			excep.printStackTrace();
			return ValidatorFactory.defaultConfig();
		}
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getFeaturesConfig()
	 */
	@Override
	public FeatureExtractorConfig getFeaturesConfig() {
		try (InputStream fis = new FileInputStream(this.featuresFile)) {
			return FeatureFactory.createConfig(fis);
		} catch (IOException | JAXBException excep) {
			excep.printStackTrace();
			return FeatureFactory.defaultConfig();
		}
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getFixerConfig()
	 */
	@Override
	public MetadataFixerConfig getFixerConfig() {
		try (InputStream fis = new FileInputStream(this.fixerFile)) {
			return FixerFactory.createConfig(fis);
		} catch (IOException | JAXBException excep) {
			excep.printStackTrace();
			return FixerFactory.defaultConfig();
		}
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getProcessorConfig()
	 */
	@Override
	public ProcessorConfig createProcessorConfig() {
		return createProcessorConfig(this.getApplicationConfig().getProcessType().getTasks());
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getProcessorConfig(EnumSet<TaskType>)
	 */
	@Override
	public ProcessorConfig createProcessorConfig(EnumSet<TaskType> tasks) {
		if (tasks == null) throw new NullPointerException("Arg tasks can not be null");
		return ProcessorFactory.fromValues(getValidatorConfig(), getFeaturesConfig(), getFixerConfig(), tasks);
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getApplicationConfig()
	 */
	@Override
	public VeraAppConfig getApplicationConfig() {
		try (InputStream fis = new FileInputStream(this.appFile)) {
			return VeraAppConfigImpl.fromXml(fis);
		} catch (IOException | JAXBException excep) {
			excep.printStackTrace();
			return VeraAppConfigImpl.defaultInstance();
		}
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#getConfigDir()
	 */
	@Override
	public File getConfigDir() {
		return this.root;
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#updateValidatorConfig(org.verapdf.pdfa.validation.validators.ValidatorConfig)
	 */
	@Override
	public void updateValidatorConfig(ValidatorConfig config) throws JAXBException, IOException {
		try (FileOutputStream fos = new FileOutputStream(this.validatorFile, false)) {
			ValidatorFactory.configToXml(config, fos);
		}
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#updateFeaturesConfig(org.verapdf.features.FeatureExtractorConfig)
	 */
	@Override
	public void updateFeaturesConfig(FeatureExtractorConfig config) throws JAXBException, IOException {
		try (FileOutputStream fos = new FileOutputStream(this.featuresFile, false)) {
			FeatureFactory.configToXml(config, fos);
		}
	}

	/**
	 * @see org.verapdf.apps.ConfigManager#updateFixerConfig(org.verapdf.metadata.fixer.MetadataFixerConfig)
	 */
	@Override
	public void updateFixerConfig(MetadataFixerConfig config) throws JAXBException, IOException {
		try (FileOutputStream fos = new FileOutputStream(this.fixerFile, false)) {
			FixerFactory.configToXml(config, fos);
		}
	}

	@Override
	public void updateAppConfig(VeraAppConfig config) throws JAXBException, IOException {
		try (FileOutputStream fos = new FileOutputStream(this.appFile, false)) {
			VeraAppConfigImpl.toXml(config, fos, Boolean.TRUE);
		}
	}

	static ConfigManagerImpl create(final File root) {
		return new ConfigManagerImpl(root);
	}

	private void initialise() {
		try {
			if ((!this.validatorFile.exists() && this.validatorFile.createNewFile())
					|| this.validatorFile.length() == 0) {
				try (OutputStream fos = new FileOutputStream(this.validatorFile, false)) {
					ValidatorFactory.configToXml(ValidatorFactory.defaultConfig(), fos);
				}
			}
			if ((!this.featuresFile.exists() && this.featuresFile.createNewFile()) || this.featuresFile.length() == 0) {
				try (OutputStream fos = new FileOutputStream(this.featuresFile, false)) {
					FeatureFactory.configToXml(FeatureFactory.defaultConfig(), fos);
				}
			}
			if ((!this.fixerFile.exists() && this.fixerFile.createNewFile()) || this.fixerFile.length() == 0) {
				try (OutputStream fos = new FileOutputStream(this.fixerFile, false)) {
					FixerFactory.configToXml(FixerFactory.defaultConfig(), fos);
				}
			}
			if ((!this.appFile.exists() && this.appFile.createNewFile()) || this.appFile.length() == 0) {
				try (OutputStream fos = new FileOutputStream(this.appFile, false)) {
					Applications.toXml(Applications.defaultConfig(), fos, Boolean.TRUE);
				}
			}
		} catch (IOException | JAXBException excep) {
			excep.printStackTrace();
			throw new IllegalStateException("Couldn't setup config", excep);
		}
	}

	private final File getConfigFile(final String name) {
		File config = new File(this.root, name);
		try {
			if (config.isDirectory() || !config.exists() && !config.createNewFile()) {
				throw new IllegalArgumentException(config.getAbsolutePath() + " must be a creatable or readable file");
			}
		} catch (IOException excep) {
			throw new IllegalArgumentException("IOException when creating: " + config.getAbsolutePath(), excep);
		}
		return config;
	}
}
