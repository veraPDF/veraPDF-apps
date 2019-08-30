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
package org.verapdf.processor.test;

import org.junit.Test;
import org.verapdf.features.FeatureFactory;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.pdfa.PdfBoxFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.ProfileDirectory;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;
import org.verapdf.processor.plugins.PluginsCollectionConfig;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.EnumSet;

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
	 * @throws JAXBException
	 */

    @Test
    public final void testCreateProcessorFromArgsNewProfile()
            throws FileNotFoundException, IOException,
            JAXBException {
    	PdfBoxFoundryProvider.initialise();
        ProfileDirectory directory = Profiles.getVeraProfileDirectory();
        assertTrue(directory.getValidationProfiles().size() > 0);
        for (ValidationProfile profile : directory.getValidationProfiles()) {
            File tmpProfile = File.createTempFile("verapdf", "profile");
            try (OutputStream os = new FileOutputStream(tmpProfile)) {
                Profiles.profileToXml(profile, os, false, false);
                testWithProfileFile(profile.getPDFAFlavour(), tmpProfile);
            }
            if (!tmpProfile.delete()) {
            	tmpProfile.deleteOnExit();
			}
        }
    }

    private static void testWithProfileFile(final PDFAFlavour flavour,
            final File profileFile) throws FileNotFoundException, IOException, JAXBException {
		try (InputStream is = new FileInputStream(profileFile)) {
			ValidationProfile profile = Profiles.profileFromXml(is);
	        ProcessorConfig config = ProcessorFactory.fromValues(ValidatorFactory.defaultConfig(), FeatureFactory.defaultConfig(), PluginsCollectionConfig.defaultConfig(), FixerFactory.defaultConfig(), EnumSet.noneOf(TaskType.class), profile);
			assertEquals(flavour, ProcessorFactory.fileBatchProcessor(config).getConfig().getCustomProfile().getPDFAFlavour());
			assertTrue(profile.equals(ProcessorFactory.fileBatchProcessor(config).getConfig().getCustomProfile()));
		}
    }
}

