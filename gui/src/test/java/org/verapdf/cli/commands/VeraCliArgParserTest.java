/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org> All rights
 * reserved. VeraPDF Library GUI is free software: you can redistribute it
 * and/or modify it under the terms of either: The GNU General public license
 * GPLv3+. You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the
 * source tree. If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html. The Mozilla Public License
 * MPLv2+. You should have received a copy of the Mozilla Public License along
 * with VeraPDF Library GUI as the LICENSE.MPL file in the root of the source
 * tree. If a copy of the MPL was not distributed with this file, you can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.verapdf.cli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.processor.TaskType;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
@SuppressWarnings("static-method")
public class VeraCliArgParserTest {
	private final static String APP_NAME = VeraCliArgParserTest.class.getName();

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#showVersion()}.
	 */
	@Test
	public final void testVersionDefault() {
		// Test empty String[] args doesn't change that
		VeraCliArgs parser = new VeraCliArgParser(new String[] {});
		assertFalse(parser.showVersion());

		// Test other flags & options doesn't change that
		parser = new VeraCliArgParser(new String[] { "-l", "-h", "--success" });
		assertFalse(parser.showVersion());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#showVersion()}.
	 */
	@Test
	public final void testVersionOption() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "--version" });
		assertTrue(parser.showVersion());

		// Test option works with other options & flags
		parser = new VeraCliArgParser(new String[] { "--version", "-l", "--passed" });
		assertTrue(parser.showVersion());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#listProfiles()}.
	 */
	@Test
	public final void testListDefault() {
		// Test empty String[] args doesn't change that
		VeraCliArgs parser = new VeraCliArgParser(new String[] {});
		assertFalse(parser.listProfiles());

		// Test other flags & options doesn't change that
		parser = new VeraCliArgParser(new String[] { "--version", "-h", "--success" });
		assertFalse(parser.listProfiles());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#listProfiles()}.
	 */
	@Test
	public final void testListFlag() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "-l" });
		assertTrue(parser.listProfiles());

		// Test flag works with other options & flags
		parser = new VeraCliArgParser(new String[] { "--version", "-l", "--passed", "-h" });
		assertTrue(parser.listProfiles());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#listProfiles()}.
	 */
	@Test
	public final void testListOption() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "--list" });
		assertTrue(parser.listProfiles());

		// Test option works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-h", "--list", "--passed" });
		assertTrue(parser.listProfiles());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#isHelp()}.
	 */
	@Test
	public final void testHelpDefault() {
		// Test empty String[] args doesn't change that
		VeraCliArgs parser = new VeraCliArgParser(new String[] {});
		assertFalse(parser.isHelp());

		// Test other flags & options doesn't change that
		parser = new VeraCliArgParser(new String[] { "-l", "--version", "--success" });
		assertFalse(parser.isHelp());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#isHelp()}.
	 */
	@Test
	public final void testHelpFlag() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "-h" });
		assertTrue(parser.isHelp());

		// Test flag works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-l", "--format", "mrr", "-h" });
		assertTrue(parser.isHelp());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#isHelp()}.
	 */
	@Test
	public final void testHelpOption() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "--help" });
		assertTrue(parser.isHelp());

		// Test option works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-l", "--format", "xml", "--help" });
		assertTrue(parser.isHelp());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#logPassed()}.
	 */
	@Test
	public final void testLogPassedDefault() {
		// Test empty String[] args doesn't change that
		VeraCliArgs parser = new VeraCliArgParser(new String[] {});
		assertFalse(parser.logPassed());

		// Test other flags & options doesn't change that
		parser = new VeraCliArgParser(new String[] { "-l", "--flavour", "1a", "--version", "-h" });
		assertFalse(parser.logPassed());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#logPassed()}.
	 */
	@Test
	public final void testLogPassedOption() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "--passed" });
		assertTrue(parser.logPassed());

		// Test option works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-l", "--passed", "--flavour", "3b" });
		assertTrue(parser.logPassed());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#logPassed()}.
	 */
	@Test
	public final void testLogPassedSuccessOption() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "--success" });
		assertTrue(parser.logPassed());

		// Test option works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-l", "--success", "--flavour", "3b" });
		assertTrue(parser.logPassed());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
	 */
	@Test
	public final void testExtractDefault() {
		// Test empty String[] args doesn't change that
		VeraCliArgs parser = new VeraCliArgParser(new String[] {});
		assertFalse(parser.extractFeatures());

		// Test other flags & options doesn't change that
		parser = new VeraCliArgParser(new String[] { "-l", "--flavour", "1a", "-h" });
		assertFalse(parser.extractFeatures());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
	 */
	@Test
	public final void testExtractFlag() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "-x" });
		assertTrue(parser.extractFeatures());

		// Test flag works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-x", "--format", "xml", "-h" });
		assertTrue(parser.extractFeatures());
	}

	/**
	 * Test method for
	 * {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
	 */
	@Test
	public final void testExtractOption() {
		VeraCliArgs parser = new VeraCliArgParser(new String[] { "--extract" });
		assertTrue(parser.extractFeatures());

		// Test option works with other options & flags
		parser = new VeraCliArgParser(new String[] { "-l", "--extract", "--flavour", "3b" });
		assertTrue(parser.extractFeatures());
	}

	@Test
	public final void testProcessTypeParsing() {
		VeraConfigGenerator configGen = new VeraConfigGeneratorImpl();
		VeraCliArgParser parser = new VeraCliArgParser(new String[] {});
		VeraAppConfig config = configGen.appConfigFromArgs(parser, Applications.defaultConfig());
		assertFalse(parser.isValidationOff());
		assertTrue(config.getProcessType().getTasks().contains(TaskType.VALIDATE));

		// Test flag works
		parser = new VeraCliArgParser(new String[] { "-o" });
		config = configGen.appConfigFromArgs(parser, Applications.defaultConfig());
		assertTrue(parser.isValidationOff());
		assertFalse(config.getProcessType().getTasks().contains(TaskType.VALIDATE));

		// Test flag works
		parser = new VeraCliArgParser(new String[] { "--off" });
		config = configGen.appConfigFromArgs(parser, Applications.defaultConfig());
		assertTrue(parser.isValidationOff());
		assertFalse(config.getProcessType().getTasks().contains(TaskType.VALIDATE));
	}

}
