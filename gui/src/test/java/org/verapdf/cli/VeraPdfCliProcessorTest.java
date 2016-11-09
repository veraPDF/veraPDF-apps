/**
 *
 */
package org.verapdf.cli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.apps.ConfigManager;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.VeraPDFException;
import org.verapdf.processor.FormatOption;

import com.beust.jcommander.JCommander;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@SuppressWarnings("static-method")
public class VeraPdfCliProcessorTest {
    private final static String APP_NAME = VeraPdfCliProcessorTest.class
            .getName();

    /**
     * Test method for
     * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(org.verapdf.cli.commands.VeraCliArgParser)}
     * .
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ProfileException
     */
    @Test
    public final void testCreateProcessorFromArgsFormat() throws IOException, VeraPDFException {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] {});
        ConfigManager manager = Applications.createConfigManager(Files.createTempDirectory("").toFile());
        VeraPdfCliProcessor proc = VeraPdfCliProcessor
                .createProcessorFromArgs(parser, manager);
        assertNotNull(proc.getConfig());
        assertTrue(proc.getConfig().getFormat() == FormatOption.MRR);
        for (FormatOption format : FormatOption.values()) {
            parser = new VeraCliArgParser();
            jCommander = initialiseJCommander(parser);
            jCommander.parse(new String[] { "--format", format.getOption() });
            proc = VeraPdfCliProcessor.createProcessorFromArgs(parser, manager);
            assertTrue("Expected:" + format + " == " + proc.getConfig().getFormat(), proc.getConfig().getFormat() == format);
        }
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(org.verapdf.cli.commands.VeraCliArgParser)}
     * .
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ProfileException
     */
//    @Test
//    public final void testCreateProcessorFromArgsLogPassed() {
//        String[] argVals = new String[] { "--passed", "--success" };
//        VeraCliArgParser parser = new VeraCliArgParser();
//        JCommander jCommander = initialiseJCommander(parser);
//        jCommander.parse(new String[] {});
//        VeraPdfCliProcessor proc = VeraPdfCliProcessor
//                .createProcessorFromArgs(parser);
//        assertFalse(proc.getConfig().getProcessorConfig().getValidatorConfig().isRecordPasses());
//
//        for (String argVal : argVals) {
//            jCommander.parse(new String[] { argVal });
//            proc = VeraPdfCliProcessor.createProcessorFromArgs(parser);
//            assertTrue(proc.getConfig().getProcessorConfig().getValidatorConfig().isRecordPasses());
//            parser = new VeraCliArgParser();
//            jCommander = initialiseJCommander(parser);
//        }
//    }

    /**
     * Test method for
     * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(org.verapdf.cli.commands.VeraCliArgParser)}
     * .
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ProfileException
     */
//    @Test
//    public final void testCreateProcessorFromArgsExtract() {
//        String[] argVals = new String[] { "-x", "--extract" };
//        VeraCliArgParser parser = new VeraCliArgParser();
//        JCommander jCommander = initialiseJCommander(parser);
//        jCommander.parse(new String[] {});
//        VeraPdfCliProcessor proc = VeraPdfCliProcessor
//                .createProcessorFromArgs(parser);
//        assertFalse(proc.getConfig().getProcessorConfig().getTasks().contains(TaskType.EXTRACT_FEATURES));
//        for (String argVal : argVals) {
//            jCommander.parse(new String[] { argVal });
//            proc = VeraPdfCliProcessor.createProcessorFromArgs(parser);
//            assertTrue(proc.getConfig().getProcessorConfig().getTasks().contains(TaskType.EXTRACT_FEATURES));
//            parser = new VeraCliArgParser();
//            jCommander = initialiseJCommander(parser);
//        }
//    }

    /**
     * Test method for
     * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(org.verapdf.cli.commands.VeraCliArgParser)}
     * .
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ProfileException
     */

//    @Test
//    public final void testCreateProcessorFromArgsFlavour() {
//        String[] argVals = new String[] { "-f", "--flavour" };
//        VeraCliArgParser parser = new VeraCliArgParser();
//        JCommander jCommander = initialiseJCommander(parser);
//        jCommander.parse(new String[] {});
//        VeraPdfCliProcessor proc = VeraPdfCliProcessor
//                .createProcessorFromArgs(parser);
//        assertTrue(proc.getConfig().getProcessorConfig().getValidatorConfig().getFlavour() == PDFAFlavour.NO_FLAVOUR);
//        ProfileDirectory directory = Profiles.getVeraProfileDirectory();
//        assertTrue(directory.getValidationProfiles().size() > 0);
//        for (String argVal : argVals) {
//            for (ValidationProfile profile : directory
//                    .getValidationProfiles()) {
//                jCommander.parse(new String[] { argVal,
//                        profile.getPDFAFlavour().getId() });
//                proc = VeraPdfCliProcessor.createProcessorFromArgs(parser);
//                assertTrue(proc.getConfig().getProcessorConfig().getValidatorConfig().getFlavour() == profile.getPDFAFlavour());
//                parser = new VeraCliArgParser();
//                jCommander = initialiseJCommander(parser);
//            }
//        }
//    }

    // TODO: remove this test: first assert is the same as in previous test; second assert is unnecessary.
    // TODO: validator is in Processor now and it cannot be null: it is created with profile obtained by flavour from
    // TODO: ModelParser that can not be NO_FLAVOUR or AUTO.
//    /**
//     * Test method for
//     * {@link org.verapdf.cli.VeraPdfCliProcessor#createProcessorFromArgs(org.verapdf.cli.commands.VeraCliArgParser)}
//     * .
//     *
//     * @throws IOException
//     * @throws FileNotFoundException
//     * @throws ProfileException
//     */
//    @Test
//    public final void testCreateProcessorFromArgsNoFlavour()
//            throws ProfileException, FileNotFoundException, IOException {
//        String[] argVals = new String[] { "-f", "--flavour" };
//        VeraCliArgParser parser = new VeraCliArgParser();
//        JCommander jCommander = initialiseJCommander(parser);
//        jCommander.parse(new String[] {});
//        VeraPdfCliProcessor proc = VeraPdfCliProcessor
//                .createProcessorFromArgs(parser);
//        assertTrue(proc.validator.getProfile().getPDFAFlavour() == PDFAFlavour.PDFA_1_B);
//        for (String argVal : argVals) {
//            jCommander.parse(new String[] { argVal,
//                    PDFAFlavour.NO_FLAVOUR.getId() });
//            proc = VeraPdfCliProcessor.createProcessorFromArgs(parser);
//            assertTrue(proc.validator == null);
//            parser = new VeraCliArgParser();
//            jCommander = initialiseJCommander(parser);
//        }
//    }

    static final JCommander initialiseJCommander(final VeraCliArgParser parser) {
        JCommander jCommander = new JCommander(parser);
        jCommander.setProgramName(APP_NAME);
        jCommander.setAllowParameterOverwriting(true);
        return jCommander;
    }

}
