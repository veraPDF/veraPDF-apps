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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 31 Oct 2016:22:13:33
 */

@SuppressWarnings("static-method")
public class ConfigManagerTests {

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#getValidatorConfig()}.
	 */
	@Test
	public void testGetValidatorConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertTrue(newManager.getValidatorConfig().equals(ValidatorFactory.defaultConfig()));
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#getFeaturesConfig()}.
	 */
	@Test
	public void testGetFeaturesConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertTrue(newManager.getFeaturesConfig().equals(FeatureFactory.defaultConfig()));
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#getFixerConfig()}.
	 */
	@Test
	public void testGetFixerConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertTrue(newManager.getFixerConfig().equals(FixerFactory.defaultConfig()));
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#getApplicationConfig()}.
	 */
	@Test
	public void testGetApplicationConfig() {
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertTrue(newManager.getApplicationConfig().equals(Applications.defaultConfig()));
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#getConfigDir()}.
	 */
	@Test
	public void testGetConfigDir() throws IOException {
		File tempDir = Files.createTempDirectory("").toFile();
		tempDir.deleteOnExit();
		ConfigManager newManager = Applications.createConfigManager(tempDir);
		assertTrue(newManager.getConfigDir() == tempDir);
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#updateValidatorConfig(org.verapdf.pdfa.validation.validators.ValidatorConfig)}.
	 */
	@Test
	public void testUpdateValidatorConfig() throws JAXBException, IOException {
		ValidatorConfig defaultConfig = ValidatorFactory.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertFalse(defaultConfig == newManager.getValidatorConfig());
		assertTrue(defaultConfig.equals(newManager.getValidatorConfig()));
		ValidatorConfig newConfig = ValidatorFactory.createConfig(PDFAFlavour.PDFA_1_A, !defaultConfig.isRecordPasses(),
				defaultConfig.getMaxFails() + 20);
		newManager.updateValidatorConfig(newConfig);
		assertFalse(defaultConfig.equals(newManager.getValidatorConfig()));
	}

	/**
	 * FIXME
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#updateFeaturesConfig(org.verapdf.features.FeatureExtractorConfig)}.
	 */
	@Test
	public void testUpdateFeaturesConfig() throws JAXBException, IOException {
		FeatureExtractorConfig defaultConfig = FeatureFactory.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertTrue(defaultConfig.equals(newManager.getFeaturesConfig()));
		FeatureExtractorConfig newConfig = FeatureFactory.configFromValues(EnumSet.complementOf(defaultConfig.getEnabledFeatures()));
		newManager.updateFeaturesConfig(newConfig);
		assertFalse(defaultConfig.equals(newManager.getFeaturesConfig()));
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#updateFixerConfig(org.verapdf.metadata.fixer.MetadataFixerConfig)}.
	 */
	@Test
	public void testUpdateFixerConfig() throws JAXBException, IOException {
		MetadataFixerConfig defaultConfig = FixerFactory.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertFalse(defaultConfig == newManager.getFixerConfig());
		assertTrue(defaultConfig.equals(newManager.getFixerConfig()));
		MetadataFixerConfig newConfig = FixerFactory.configFromValues("NOT_DEFAULT", !defaultConfig.isFixId());
		newManager.updateFixerConfig(newConfig);
		assertFalse(defaultConfig.equals(newManager.getFixerConfig()));
	}

	/**
	 * Test method for
	 * {@link org.verapdf.apps.ConfigManagerImpl#updateAppConfig(org.verapdf.apps.VeraAppConfig)}.
	 */
	@Test
	public void testUpdateAppConfig() throws JAXBException, IOException {
		VeraAppConfig defaultConfig = Applications.defaultConfig();
		ConfigManager newManager = Applications.createTmpConfigManager();
		assertFalse(defaultConfig == newManager.getApplicationConfig());
		assertTrue(defaultConfig.equals(newManager.getApplicationConfig()));
		VeraAppConfig newConfig = Applications.createConfigBuilder(defaultConfig).overwrite(!defaultConfig.isOverwriteReport()).build();
		newManager.updateAppConfig(newConfig);
		assertFalse(defaultConfig.equals(newManager.getApplicationConfig()));
	}

}
