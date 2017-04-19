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
package org.verapdf.cli.commands;

import com.beust.jcommander.JCommander;
import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureFactory;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.TaskType;
import org.verapdf.processor.plugins.PluginsCollectionConfig;

import static org.junit.Assert.assertTrue;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 14 Nov 2016:11:22:13
 */

public class VeraCliTasksTest {

	/**
	 * Test method for {@link org.verapdf.cli.commands.VeraCliArgParser#fixMetadata()}.
	 */
	@Test
	public void testFixMetadata() throws VeraPDFException {
		String [] args = {"-o" , "--fixmetadata"};
		ProcessorConfig config = VeraCliTasksTest.getConfig(args);
		assertTrue("taskSize = " + config.getTasks().size(), config.getTasks().size() == 2);
		assertTrue(config.getTasks().contains(TaskType.FIX_METADATA));
		assertTrue(config.getTasks().contains(TaskType.VALIDATE));
	}

	/**
	 * Test method for {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
	 */
	@Test
	public void testExtractFeatures() throws VeraPDFException {
		String [] args = {"-o" , "-x"};
		ProcessorConfig config = VeraCliTasksTest.getConfig(args);
		assertTrue("taskSize = " + config.getTasks().size(), config.getTasks().size() == 1);
		assertTrue(config.getTasks().contains(TaskType.EXTRACT_FEATURES));
	}

	/**
	 * Test method for {@link org.verapdf.cli.commands.VeraCliArgParser#validatorConfig()}.
	 */
	@Test
	public void testValidate() throws VeraPDFException {
		String [] args = {};
		ProcessorConfig config = VeraCliTasksTest.getConfig(args);
		assertTrue("taskSize = " + config.getTasks().size(), config.getTasks().size() == 1);
		assertTrue(config.getTasks().contains(TaskType.VALIDATE));
	}

	/**
	 * Test method for {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
	 */
	@Test
	public void testExtractAndValidation() throws VeraPDFException {
		String [] args = {"-x"};
		ProcessorConfig config = VeraCliTasksTest.getConfig(args);
		assertTrue("taskSize = " + config.getTasks().size(), config.getTasks().size() == 2);
		assertTrue(config.getTasks().contains(TaskType.EXTRACT_FEATURES));
		assertTrue(config.getTasks().contains(TaskType.VALIDATE));
	}

	/**
	 * Test method for {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
	 */
	@Test
	public void testExtractFixAndValidation() throws VeraPDFException {
		String [] args = {"-x", "--fixmetadata"};
		ProcessorConfig config = VeraCliTasksTest.getConfig(args);
		assertTrue("taskSize = " + config.getTasks().size(), config.getTasks().size() == 3);
		assertTrue(config.getTasks().contains(TaskType.EXTRACT_FEATURES));
		assertTrue(config.getTasks().contains(TaskType.VALIDATE));
		assertTrue(config.getTasks().contains(TaskType.FIX_METADATA));
	}

	private static ProcessorConfig getConfig (final String [] args) throws VeraPDFException {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest
                .initialiseJCommander(parser);

        // Test that "xml" value selects XML
        jCommander.parse(args);
        
        return parser.processorConfig(parser.appConfig(Applications.defaultConfig()).getProcessType(),
				FeatureFactory.defaultConfig(), PluginsCollectionConfig.defaultConfig());
	}
}
