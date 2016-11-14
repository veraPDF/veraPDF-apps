/**
 * 
 */
package org.verapdf.cli.commands;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureFactory;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.TaskType;

import com.beust.jcommander.JCommander;

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
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

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
        
        return parser.processorConfig(parser.appConfig(Applications.defaultConfig()).getProcessType(), FeatureFactory.defaultConfig());
	}
}
