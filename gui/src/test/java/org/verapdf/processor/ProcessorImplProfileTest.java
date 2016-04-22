package org.verapdf.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.ProfileException;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.ProfileDirectory;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.pdfa.validation.ValidationProfile;

import com.beust.jcommander.JCommander;
import org.verapdf.processor.config.Config;

/**
 * @author Sergey Shemyakov
 *
 */
@SuppressWarnings("static-method")
public class ProcessorImplProfileTest {

	/**
	 * Test method for
	 * {@link org.verapdf.processor.ProcessorImpl#profileFromConfig(Config)}
	 * .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ProfileException
	 * @throws JAXBException
	 */

    @Test
    public final void testCreateProcessorFromArgsNewProfile()
            throws ProfileException, FileNotFoundException, IOException,
            JAXBException {
        Config config = new Config();
        assertTrue(ProcessorImpl.profileFromConfig(config).getPDFAFlavour()
				== PDFAFlavour.PDFA_1_B);
        ProfileDirectory directory = Profiles.getVeraProfileDirectory();
        assertTrue(directory.getValidationProfiles().size() > 0);
        for (ValidationProfile profile : directory.getValidationProfiles()) {
            File tmpProfile = File.createTempFile("verapdf", "profile");
			tmpProfile.deleteOnExit();
            try (OutputStream os = new FileOutputStream(tmpProfile)) {
                Profiles.profileToXml(profile, os, Boolean.FALSE);
                testWithProfileFile(profile.getPDFAFlavour(), tmpProfile);
            }
        }
    }

    private static void testWithProfileFile(final PDFAFlavour flavour,
            final File profileFile) throws FileNotFoundException, IOException, JAXBException {
		Config config = new Config();
		config.setValidationProfilePath(profileFile.toPath());
		try (InputStream is = new FileInputStream(profileFile)) {
			ValidationProfile profile = Profiles.profileFromXml(is);
			assertEquals(flavour,
					ProcessorImpl.profileFromConfig(config).getPDFAFlavour());
			assertTrue(profile != ProcessorImpl.profileFromConfig(config));
			assertEquals(
					Profiles.profileToXml(profile, Boolean.TRUE)
							+ "\n"
							+ Profiles.profileToXml(
									ProcessorImpl.profileFromConfig(config),
									Boolean.TRUE), profile.getRules(),
					ProcessorImpl.profileFromConfig(config).getRules());
		}
    }
}

