package org.verapdf.processor;

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

//    @Test
//    public final void testCreateProcessorFromArgsNewProfile()
//            throws FileNotFoundException, IOException,
//            JAXBException {
//        ProfileDirectory directory = Profiles.getVeraProfileDirectory();
//        assertTrue(directory.getValidationProfiles().size() > 0);
//        for (ValidationProfile profile : directory.getValidationProfiles()) {
//            File tmpProfile = File.createTempFile("verapdf", "profile");
//			tmpProfile.deleteOnExit();
//            try (OutputStream os = new FileOutputStream(tmpProfile)) {
//                Profiles.profileToXml(profile, os, Boolean.FALSE);
//                testWithProfileFile(profile.getPDFAFlavour(), tmpProfile);
//            }
//        }
//    }
//
//    private static void testWithProfileFile(final PDFAFlavour flavour,
//            final File profileFile) throws FileNotFoundException, IOException, JAXBException {
//        ProcessorConfig config = ProcessorFactory.defaultConfig();
//		config.setValidationProfilePath(profileFile.toPath());
//		try (InputStream is = new FileInputStream(profileFile)) {
//			ValidationProfile profile = Profiles.profileFromXml(is);
//			assertEquals(flavour,
//					new ProcessorImpl().profileFromConfig(config).getPDFAFlavour());
//			assertTrue(profile != new ProcessorImpl().profileFromConfig(config));
//			assertEquals(
//					Profiles.profileToXml(profile, Boolean.TRUE)
//							+ "\n"
//							+ Profiles.profileToXml(
//							new ProcessorImpl().profileFromConfig(config),
//									Boolean.TRUE), profile.getRules(),
//					new ProcessorImpl().profileFromConfig(config).getRules());
//		}
//    }
}

