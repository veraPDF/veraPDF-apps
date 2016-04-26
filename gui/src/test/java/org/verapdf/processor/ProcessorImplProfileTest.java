package org.verapdf.processor;

import org.junit.Test;
import org.verapdf.core.ProfileException;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.ProfileDirectory;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.pdfa.validation.ValidationProfile;
import org.verapdf.processor.config.Config;

import javax.xml.bind.JAXBException;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue(ProcessorImpl.profileFromConfig(config) == null);
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

