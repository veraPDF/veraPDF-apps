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
import java.util.EnumSet;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.verapdf.features.FeatureFactory;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.ProfileDirectory;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;

/**
 * @author Sergey Shemyakov
 */
@SuppressWarnings("static-method")
public class ProcessorImplProfileTest {

	/**
	 * Test method for
	 * {@link org.verapdf.processor.ProcessorImpl#profileFromConfig(Config)} .
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ProfileException
	 * @throws JAXBException
	 */

	@Test
	public final void testCreateProcessorFromArgsNewProfile() throws FileNotFoundException, IOException, JAXBException {
		VeraGreenfieldFoundryProvider.initialise();
		ProfileDirectory directory = Profiles.getVeraProfileDirectory();
		assertTrue(directory.getValidationProfiles().size() > 0);
		for (ValidationProfile profile : directory.getValidationProfiles()) {
			File tmpProfile = File.createTempFile("verapdf", "profile");
			tmpProfile.deleteOnExit();
			try (OutputStream os = new FileOutputStream(tmpProfile)) {
				Profiles.profileToXml(profile, os, false, false);
				testWithProfileFile(profile.getPDFAFlavour(), tmpProfile);
			}
		}
	}

	private static void testWithProfileFile(final PDFAFlavour flavour, final File profileFile)
			throws FileNotFoundException, IOException, JAXBException {
		try (InputStream is = new FileInputStream(profileFile)) {
			ValidationProfile profile = Profiles.profileFromXml(is);
			ProcessorConfig config = ProcessorFactory.fromValues(ValidatorFactory.defaultConfig(),
					FeatureFactory.defaultConfig(), FixerFactory.defaultConfig(), EnumSet.noneOf(TaskType.class),
					profile);
			assertEquals(flavour,
					ProcessorFactory.fileBatchProcessor(config).getConfig().getCustomProfile().getPDFAFlavour());
			assertTrue(profile.equals(ProcessorFactory.fileBatchProcessor(config).getConfig().getCustomProfile()));
		}
	}
}
