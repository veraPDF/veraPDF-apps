/**
 * 
 */
package org.verapdf.apps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 31 Oct 2016:18:07:02
 */

public class VeraAppConfigTest {

	/**
	 * Test method for {@link org.verapdf.apps.VeraAppConfigImpl#defaultInstance()}.
	 */
	@Test
	public void testDefaultInstance() {
		VeraAppConfig defaultInstance = Applications.defaultConfig();
		VeraAppConfig defaultCopy = Applications.createConfigBuilder(defaultInstance).build();
		assertTrue(defaultInstance == Applications.defaultConfig());
		assertTrue(defaultInstance.equals(Applications.defaultConfig()));
		assertTrue(defaultCopy.equals(defaultInstance));
		assertFalse(defaultCopy == defaultInstance);
	}

    /**
     * Test method for {@link org.verapdf.pdfa.results.ValidationResultImpl#hashCode()}.
     */
    @SuppressWarnings("static-method")
	@Test
    public final void testHashCodeAndEquals() {
        EqualsVerifier.forClass(VeraAppConfigImpl.class).verify();
    }

	/**
	 * Test method for {@link org.verapdf.apps.VeraAppConfigImpl#toXml(org.verapdf.apps.VeraAppConfig, java.io.OutputStream, java.lang.Boolean)}.
	 */
	@Test
	public void testToXmlVeraAppConfigOutputStreamBoolean() throws IOException, JAXBException {
		File temp = Files.createTempFile("", "").toFile();
		VeraAppConfig defaultInstance = Applications.defaultConfig();
		assertTrue(defaultInstance == Applications.defaultConfig());
		try (OutputStream fos = new FileOutputStream(temp)) {
			Applications.toXml(Applications.defaultConfig(), fos, Boolean.TRUE);
		}
		try (InputStream fis = new FileInputStream(temp)) {
			defaultInstance = Applications.fromXml(fis);
		}
		assertTrue(defaultInstance.equals(Applications.defaultConfig()));
		assertFalse(defaultInstance == Applications.defaultConfig());
	}

	/**
	 * Test method for {@link org.verapdf.apps.VeraAppConfigImpl#fromXml(java.lang.String)}.
	 */
	@Test
	public void testFromXmlString() throws JAXBException, IOException {
		String defaultXml = Applications.toXml(Applications.defaultConfig(), Boolean.TRUE);
		VeraAppConfig fromXml = Applications.fromXml(defaultXml);
		assertTrue(Applications.defaultConfig().equals(fromXml));
		assertFalse(fromXml == Applications.defaultConfig());
	}

}
