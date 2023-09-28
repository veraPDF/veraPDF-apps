/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * VeraPDF Library GUI is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * VeraPDF Library GUI as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.verapdf.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import org.junit.Test;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.validators.BaseValidator;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.app.ConfigManager;
import org.verapdf.processor.app.VeraAppConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 31 Oct 2016:22:13:33
 */

@SuppressWarnings("static-method")
public class ConfigManagerTests {

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#getValidatorConfig()}.
	 */
	@Test
	public void testGetValidatorConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertEquals(newManager.getValidatorConfig(), ValidatorFactory.defaultConfig());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#getFeaturesConfig()}.
	 */
	@Test
	public void testGetFeaturesConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertEquals(newManager.getFeaturesConfig(), FeatureFactory.defaultConfig());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#getFixerConfig()}.
	 */
	@Test
	public void testGetFixerConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertEquals(newManager.getFixerConfig(), FixerFactory.defaultConfig());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#getApplicationConfig()}.
	 */
	@Test
	public void testGetApplicationConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertEquals(newManager.getApplicationConfig(), Applications.defaultConfig());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#getConfigDir()}.
	 */
	@Test
	public void testGetConfigDir() throws IOException {
		File tempDir = Files.createTempDirectory("").toFile();
		tempDir.deleteOnExit();
		ConfigManager newManager = Applications.createConfigManager(tempDir);
		assertSame(newManager.getConfigDir(), tempDir);
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#updateValidatorConfig(org.verapdf.pdfa.validation.validators.ValidatorConfig)}.
	 */
	@Test
	public void testUpdateValidatorConfig() throws JAXBException, IOException {
		ValidatorConfig defaultConfig = ValidatorFactory.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertNotSame(defaultConfig, newManager.getValidatorConfig());
		assertEquals(defaultConfig, newManager.getValidatorConfig());
		ValidatorConfig newConfig = ValidatorFactory.createConfig(PDFAFlavour.PDFA_1_A, PDFAFlavour.PDFA_1_B,
				!defaultConfig.isRecordPasses(), defaultConfig.getMaxFails() + 20, false, false,
				Level.WARNING, BaseValidator.DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS, false, null, false, false);
		newManager.updateValidatorConfig(newConfig);
		assertNotEquals(defaultConfig, newManager.getValidatorConfig());
	}

	/**
	 * FIXME
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#updateFeaturesConfig(org.verapdf.features.FeatureExtractorConfig)}.
	 */
	@Test
	public void testUpdateFeaturesConfig() throws JAXBException, IOException {
		FeatureExtractorConfig defaultConfig = FeatureFactory.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertEquals(defaultConfig, newManager.getFeaturesConfig());
		FeatureExtractorConfig newConfig = FeatureFactory.configFromValues(EnumSet.complementOf(defaultConfig.getEnabledFeatures()));
		newManager.updateFeaturesConfig(newConfig);
		assertNotEquals(defaultConfig, newManager.getFeaturesConfig());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#updateFixerConfig(org.verapdf.metadata.fixer.MetadataFixerConfig)}.
	 */
	@Test
	public void testUpdateFixerConfig() throws JAXBException, IOException {
		MetadataFixerConfig defaultConfig = FixerFactory.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertNotSame(defaultConfig, newManager.getFixerConfig());
		assertEquals(defaultConfig, newManager.getFixerConfig());
		MetadataFixerConfig newConfig = FixerFactory.configFromValues("NOT_DEFAULT");
		newManager.updateFixerConfig(newConfig);
		assertNotEquals(defaultConfig, newManager.getFixerConfig());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.app.ConfigManagerImpl#updateAppConfig(org.verapdf.processor.app.VeraAppConfig)}.
	 */
	@Test
	public void testUpdateAppConfig() throws JAXBException, IOException {
		VeraAppConfig defaultConfig = Applications.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertNotSame(defaultConfig, newManager.getApplicationConfig());
		assertEquals(defaultConfig, newManager.getApplicationConfig());
		VeraAppConfig newConfig = Applications.createConfigBuilder(defaultConfig).isVerbose(!defaultConfig.isVerbose()).build();
		newManager.updateAppConfig(newConfig);
		assertNotEquals(defaultConfig, newManager.getApplicationConfig());
	}

}
