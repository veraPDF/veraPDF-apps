/**
 * 
 */
package org.verapdf.apps;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.verapdf.ReleaseDetails;
import org.verapdf.SemanticVersionNumber;
import org.verapdf.Versions;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 26 May 2017:01:23:43
 */

public class SoftwareUpdaterImpl implements SoftwareUpdater {
	private final static Logger logger = Logger.getLogger(SoftwareUpdaterImpl.class.getCanonicalName());
	private final static SAXParserFactory saxFact = SAXParserFactory.newInstance();
	private final static String jenkinsRoot = "http://jenkins.openpreservation.org/"; //$NON-NLS-1$
	private final static String latestGF = jenkinsRoot + "job/veraPDF-apps/lastStableBuild/api/xml"; //$NON-NLS-1$
	private final static String latestPDFBox = jenkinsRoot + "job/veraPDF-apps/lastStableBuild/api/xml"; //$NON-NLS-1$

	/**
	 * 
	 */
	SoftwareUpdaterImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isOnline() {
		try {
			URL url = new URL(jenkinsRoot);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("HEAD"); //$NON-NLS-1$
			huc.connect();
			return huc.getResponseCode() == 200;
		} catch (MalformedURLException excep) {
			throw new IllegalStateException(String.format("Problem parsing hard coded URL %s", jenkinsRoot), excep); //$NON-NLS-1$
		} catch (IOException excep) {
			logger.log(Level.INFO, "Couldn't get latest version info from Jenkins.", excep); //$NON-NLS-1$
		}
		return false;
	}

	@Override
	public SemanticVersionNumber getLatestAppsVersion() {
		return getLatestVersionFromUrl(latestGF);
	}

	@Override
	public SemanticVersionNumber getLatestPdfBoxAppsVersion() {
		return getLatestVersionFromUrl(latestPDFBox);
	}

	@Override
	public boolean isUpdateAvailable() {
		return isUpdateAvailable(ReleaseDetails.byId("gui"));
	}

	@Override
	public boolean isUpdateAvailable(final String versionString) {
		if (!this.isOnline())
			return false;
		SemanticVersionNumber current = Versions.fromString(versionString);
		String endpoint = versionString.endsWith("PDFBOX") ? latestPDFBox : latestGF;
		SemanticVersionNumber available = getLatestVersionFromUrl(endpoint);
		return current.compareTo(available) < 0;
	}

	@Override
	public boolean isUpdateAvailable(final ReleaseDetails details) {
		return isUpdateAvailable(details.getVersion());
	}

	@Override
	public String getLatestVersion(final String versionString) {
		if (!this.isOnline())
			return versionString;
		SemanticVersionNumber current = Versions.fromString(versionString);
		String endpoint = versionString.endsWith("PDFBOX") ? latestPDFBox : latestGF;
		SemanticVersionNumber available = getLatestVersionFromUrl(endpoint);
		return available.getVersionString();
	}

	@Override
	public String getLatestVersion(final ReleaseDetails details) {
		return getLatestVersion(details.getVersion());
	}

	private static final SemanticVersionNumber getLatestVersionFromUrl(final String endpoint) {
		try {
			URL url = new URL(endpoint);
			SAXParser saxParser = saxFact.newSAXParser();
			VersionParser versionParser = new VersionParser();
			saxParser.parse(new InputSource(url.openStream()), versionParser);
			return versionParser.getVersion();
		} catch (IOException | ParserConfigurationException | SAXException excep) {
			throw new IllegalStateException(String.format("Problem parsing version number from URL %s", endpoint), //$NON-NLS-1$
					excep);
		}
	}

	static class VersionParser extends DefaultHandler {
		private static final String verQName = "displayName"; //$NON-NLS-1$
		private SemanticVersionNumber versionNumber = Versions.fromInts(0, 0, 0);
		private boolean isVersion = false;

		public SemanticVersionNumber getVersion() {
			return this.versionNumber;
		}

		@Override
		public void startElement(final String namespaceURI, final String localName, final String qName,
				final Attributes atts) {
			this.isVersion = qName.equalsIgnoreCase(verQName);

		}

		@Override
		public void characters(final char ch[], final int start, final int length) {
			if (!this.isVersion)
				return;
			String version = new String(ch, start, length);
			this.versionNumber = Versions.fromString(version);
			this.isVersion = false;
		}
	}
}
