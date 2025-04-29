/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2025, veraPDF Consortium <info@verapdf.org>
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
package org.verapdf.cli;

import com.beust.jcommander.JCommander;
import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureFactory;
import org.verapdf.features.FeatureObjectType;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.ProfileDirectory;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.TaskType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import org.verapdf.processor.app.ConfigManager;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
@SuppressWarnings("static-method")
public class VeraPdfCliProcessorTest {
	private static final String APP_NAME = VeraPdfCliProcessorTest.class.getName();

	/**
	 * Test method for
	 * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(VeraCliArgParser, ConfigManager)}
	 * .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws org.verapdf.core.ProfileException
	 */
	@Test
	public final void testCreateProcessorFromArgsFormat() throws IOException, VeraPDFException {
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse();
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertNotNull(proc.getConfig());
			assertSame(proc.getConfig().getFormat(), FormatOption.XML);
		}
		for (FormatOption format : FormatOption.values()) {
			parser = new VeraCliArgParser();
			jCommander = initialiseJCommander(parser);
			jCommander.parse("--format", format.getOption());
			try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
				assertSame("Expected:" + format + " == " + proc.getConfig().getFormat(), proc.getConfig().getFormat(), format);
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(VeraCliArgParser, ConfigManager)}
	 * .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws org.verapdf.core.ProfileException
	 */
	@Test
	public final void testCreateProcessorFromArgsLogPassed() throws VeraPDFException, IOException {
		String[] argVals = new String[] { "--passed", "--success" };
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse();
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertFalse(proc.getProcessorConfig().getValidatorConfig().isRecordPasses());
		}
		for (String argVal : argVals) {
			jCommander.parse(argVal);
			try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
				assertTrue(proc.getProcessorConfig().getValidatorConfig().isRecordPasses());
			}
			parser = new VeraCliArgParser();
			jCommander = initialiseJCommander(parser);
		}
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(VeraCliArgParser, ConfigManager)}
	 * .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws org.verapdf.core.ProfileException
	 */
	@Test
	public final void testCreateProcessorFromArgsExtract() throws IOException, VeraPDFException {
		String[][] argVals = new String[][] { {"-x", "informationDict"}, {"--extract", "informationDict" }};
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse();
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertFalse(proc.getProcessorConfig().getTasks().contains(TaskType.EXTRACT_FEATURES));
		}
		for (String[] argVal : argVals) {
			jCommander.parse(argVal);
			try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
				assertTrue(proc.getProcessorConfig().getTasks().contains(TaskType.EXTRACT_FEATURES));
			}
			parser = new VeraCliArgParser();
			jCommander = initialiseJCommander(parser);
		}
	}

	@Test
	public final void testCreateProcessorFromArgsExtractSeveralFeatures() throws IOException, VeraPDFException {
		String[][] argVals = new String[][] { {"-x", "shading,actions,font,page"}, {"--extract", "shading,actions,font,page" }};
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse();
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertEquals(proc.getProcessorConfig().getFeatureConfig(), FeatureFactory.defaultConfig());
		}
		for (String[] argVal : argVals) {
			jCommander.parse(argVal);
			try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
				EnumSet<FeatureObjectType> features = proc.getProcessorConfig().getFeatureConfig().getEnabledFeatures();
				assertTrue(features.contains(FeatureObjectType.SHADING));
				assertTrue(features.contains(FeatureObjectType.ACTION));
				assertTrue(features.contains(FeatureObjectType.FONT));
				assertTrue(features.contains(FeatureObjectType.PAGE));
			}
			parser = new VeraCliArgParser();
			jCommander = initialiseJCommander(parser);
		}
	}

	@Test
	public final void testCreateProcessorFromArgsConfig() throws IOException, VeraPDFException {
		String[] initialArgs = new String[] { "--verbose", "--format", "json", "-df", "1a" };
		String[] args = new String[] { "--config" };
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse(initialArgs);
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertTrue(proc.getConfig().isVerbose());
			assertEquals(FormatOption.JSON, proc.getConfig().getFormat());
			assertEquals(PDFAFlavour.PDFA_1_A, proc.getProcessorConfig().getValidatorConfig().getDefaultFlavour());

			assertFalse(manager.getApplicationConfig().isVerbose());
			assertNotEquals(FormatOption.JSON, manager.getApplicationConfig().getFormat());
			assertNotEquals(PDFAFlavour.PDFA_1_A, manager.getValidatorConfig().getDefaultFlavour());
		}
		for (String argVal : args) {
			jCommander.parse(argVal);
			parser.setValuesFromConfig(manager);
			try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
				assertFalse(proc.getConfig().isVerbose());
				assertNotEquals(FormatOption.JSON, proc.getConfig().getFormat());
				assertNotEquals(PDFAFlavour.PDFA_1_A, proc.getProcessorConfig().getValidatorConfig().getDefaultFlavour());
			}
			parser = new VeraCliArgParser();
			jCommander = initialiseJCommander(parser);
		}
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(VeraCliArgParser, ConfigManager)}
	 * .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws org.verapdf.core.ProfileException
	 */

	@Test
	public final void testCreateProcessorFromArgsFlavour() throws IOException, VeraPDFException {
		String[] argVals = new String[] { "-f", "--flavour" };
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse();
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertSame(PDFAFlavour.NO_FLAVOUR, proc.getProcessorConfig().getValidatorConfig().getFlavour());
		}
		ProfileDirectory directory = Profiles.getVeraProfileDirectory();
		assertTrue(!directory.getValidationProfiles().isEmpty());
		for (String argVal : argVals) {
			for (ValidationProfile profile : directory.getValidationProfiles()) {
				jCommander.parse(argVal, profile.getPDFAFlavour().getId());
				try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
					assertSame(profile.getPDFAFlavour(), proc.getProcessorConfig().getValidatorConfig().getFlavour());
				}
				parser = new VeraCliArgParser();
				jCommander = initialiseJCommander(parser);
			}
		}
	}

	// TODO: remove this test: first assert is the same as in previous test;
	// second assert is unnecessary.
	// TODO: validator is in Processor now and it cannot be null: it is created
	// with profile obtained by flavour from
	// TODO: ModelParser that can not be NO_FLAVOUR or AUTO.
	/**
	 * Test method for
	 * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(VeraCliArgParser, ConfigManager)}
	 * .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws org.verapdf.core.ProfileException
	 */
	@Test
	public final void testCreateProcessorFromArgsNoFlavour()
			throws IOException, VeraPDFException {
		String[] argVals = new String[] { "-f", "--flavour" };
		VeraCliArgParser parser = new VeraCliArgParser();
		JCommander jCommander = initialiseJCommander(parser);
		jCommander.parse();
		ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
		try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			assertSame(proc.getProcessorConfig().getValidatorConfig().getFlavour(), PDFAFlavour.NO_FLAVOUR);
		}
		for (String argVal : argVals) {
			jCommander.parse(argVal, PDFAFlavour.NO_FLAVOUR.getId());
			try (VeraPdfCliProcessor proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager)) {
			}
			parser = new VeraCliArgParser();
			jCommander = initialiseJCommander(parser);
		}
	}

	static final JCommander initialiseJCommander(final VeraCliArgParser parser) {
		JCommander jCommander = new JCommander(parser);
		jCommander.setProgramName(APP_NAME);
		jCommander.setAllowParameterOverwriting(true);
		return jCommander;
	}

}
