/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2024, veraPDF Consortium <info@verapdf.org>
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
package org.verapdf.apps;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.verapdf.ReleaseDetails;
import org.verapdf.version.SemanticVersionNumber;
import org.verapdf.version.Versions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 26 May 2017:01:23:43
 */

public class SoftwareUpdaterImpl implements SoftwareUpdater {
	private static final Logger logger = Logger.getLogger(SoftwareUpdaterImpl.class.getCanonicalName());
	private static final String latestGF = "https://search.maven.org/solrsearch/select?q=g:org.verapdf.apps+AND+a:greenfield-apps&core=gav&rows=1&wt=xml";
	private static final String latestPDFBox = "https://search.maven.org/solrsearch/select?q=g:org.verapdf.apps+AND+a:pdfbox-apps&core=gav&rows=1&wt=xml";
	private final String currentVersion = Applications.getAppDetails().getVersion();

	/**
	 * 
	 */
	SoftwareUpdaterImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isOnline() {
		String stringURL = getEndpointForVersion(this.currentVersion);
		try {
			URL url = new URL(stringURL);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("GET"); //$NON-NLS-1$
			huc.connect();
			if (huc.getResponseCode() == 200) {
				url.openStream();
				return true;
			}
		} catch (MalformedURLException excep) {
			throw new IllegalStateException(String.format("Problem parsing hard coded URL %s", stringURL), excep); //$NON-NLS-1$
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
		return isUpdateAvailable(Applications.getAppDetails());
	}

	@Override
	public boolean isUpdateAvailable(final String versionString) {
		if (!this.isOnline()) {
			return false;
		}
		SemanticVersionNumber current = Versions.fromString(versionString);
		String endpoint = getEndpointForVersion(versionString);
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
		String endpoint = getEndpointForVersion(versionString);
		SemanticVersionNumber available = getLatestVersionFromUrl(endpoint);
		return available.getVersionString();
	}

	@Override
	public String getLatestVersion(final ReleaseDetails details) {
		return getLatestVersion(details.getVersion());
	}

	private static SemanticVersionNumber getLatestVersionFromUrl(final String endpoint) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			builder.setErrorHandler(null);
			URL url = new URL(endpoint);
			Document doc = builder.parse(new InputSource(url.openStream()));
			XPath path = XPathFactory.newInstance().newXPath();
			NodeList versions = ((NodeList) path.evaluate("//str[@name='v']", doc, XPathConstants.NODESET));
			return Versions.fromString(versions.item(0).getFirstChild().getNodeValue());
		} catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException excep) {
			excep.printStackTrace();
			throw new IllegalStateException(String.format("Problem parsing version number from URL %s", endpoint), //$NON-NLS-1$
					excep);
		}
	}

	private static String getEndpointForVersion(final String versionString) {
		return versionString.endsWith(Versions.PDFBOX_BUILD_INFO) ? latestPDFBox : latestGF;
	}
}
