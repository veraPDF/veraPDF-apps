/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2025, veraPDF Consortium <info@verapdf.org>
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
package org.verapdf.apps.utils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javanet.staxutils.SimpleNamespaceContext;
import org.verapdf.apps.Applications;
import org.verapdf.core.utils.FileUtils;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.features.FeatureObjectType;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.policy.SchematronGenerator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 26 May 2017:14:37:56
 */

public final class ApplicationUtils {

	private static final Logger LOGGER = Logger.getLogger(ApplicationUtils.class.getCanonicalName());

	/**
	 * Private constructor should never be called
	 */
	private ApplicationUtils() {
		throw new AssertionError("Should never happen"); //$NON-NLS-1$
	}

	/**
	 * Filters the passed list files by removing all files without a ".pdf"
	 * extension.
	 *
	 * @param toFilter
	 *            the list of files to filter, can not be null
	 * @return an immutable list of the filtered files, i.e. all PDF extensions
	 * @throws IllegalArgumentException
	 *             when toFilter is null
	 */
	public static List<File> filterPdfFiles(final List<File> toFilter, final boolean isRecursive, boolean nonPdfExt) {
		Applications.checkArgNotNull(toFilter, "toFilter"); //$NON-NLS-1$
		List<File> retVal = new ArrayList<>();
		for (File file : toFilter) {
			if (!file.exists()) {
				LOGGER.log(Level.SEVERE, "File " + file.getAbsolutePath() + " doesn't exist.");
				continue;
			}
			if (file.isFile()) {
				if (nonPdfExt || FileUtils.hasExtNoCase(file.getName(), GUIConstants.PDF) ||
						FileUtils.hasExtNoCase(file.getName(), GUIConstants.ZIP)) {
					retVal.add(file);
				} else {
					LOGGER.log(Level.SEVERE, "File " + file.getAbsolutePath() + " doesn't have a .pdf extension. Try using --nonpdfext flag");
				}
			} else if (file.isDirectory()) {
				retVal.addAll(filterPdfFilesFromDirs(Arrays.asList(file.listFiles()), isRecursive, nonPdfExt));
			}
		}
		return Collections.unmodifiableList(retVal);
	}

	private static List<File> filterPdfFilesFromDirs(final List<File> toFilter,
											 final boolean isRecursive, final boolean nonPdfExt) {
		Applications.checkArgNotNull(toFilter, "toFilter"); //$NON-NLS-1$
		List<File> retVal = new ArrayList<>();
		for (File file : toFilter) {
			if (file.isFile() && (nonPdfExt || FileUtils.hasExtNoCase(file.getName(), GUIConstants.PDF))) {
				retVal.add(file);
			} else if (file.isDirectory() && isRecursive) {
				retVal.addAll(filterPdfFilesFromDirs(Arrays.asList(file.listFiles()), isRecursive, nonPdfExt));
			}
		}
		return Collections.unmodifiableList(retVal);
	}

	/**
	 * Checks all files in a list to ensure that they exist and returns true if
	 * and only if all files in the list exist.
	 *
	 * @param toCheck
	 *            the list of files to test
	 * @return true if all files in the list exist, otherwise false.
	 * @throws IllegalArgumentException
	 *             when toCheck is null
	 */
	public static boolean doAllFilesExist(final List<File> toCheck) {
		Applications.checkArgNotNull(toCheck, "toCheck"); //$NON-NLS-1$
		if (toCheck.isEmpty()) {
			return false;
		}
		for (File file : toCheck) {
			if (file == null || !file.exists()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks a list of files to ensure that they all have an extension supplied
	 * in the list of extensions
	 *
	 * @param toCheck
	 *            the list of files to check the extensions of
	 * @param extensions
	 *            the list of allowed extensions
	 * @return true if all files in toCheck have an extension listed in
	 *         extensions
	 * @throws IllegalArgumentException
	 *             when toCheck is null
	 */
	public static boolean isLegalExtension(final List<File> toCheck, final String[] extensions) {
		Applications.checkArgNotNull(toCheck, "toCheck"); //$NON-NLS-1$
		for (File file : toCheck) {
			if (file.isFile()) {
				boolean isExtMatch = false;
				for (String extension : extensions) {
					if (FileUtils.hasExtNoCase(file.getName(), extension)) {
						isExtMatch = true;
					}
				}
				if (!isExtMatch) {
					return false;
				}
			} else if (file.isDirectory()) {
				if (!isLegalExtension(Arrays.asList(file.listFiles()), extensions)) {
					return false;
				}
			}
		}
		return true;
	}

	public static FeatureExtractorConfig mergeEnabledFeaturesFromPolicy(FeatureExtractorConfig currentConfig, InputStream policy) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to secure policy processing");
		}
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(policy);
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		SimpleNamespaceContext nsc = new SimpleNamespaceContext();
		nsc.setPrefix(SchematronGenerator.SCH_PREFIX, SchematronGenerator.SCH_NAMESPACE);
		xpath.setNamespaceContext(nsc);
		String path = '/' + SchematronGenerator.SCH_PREFIX + ':' + SchematronGenerator.ROOT_NAME
				+ "/@" + SchematronGenerator.ENABLED_FEATURES_ATTRIBUTE_NAME;
		String value = (String) xpath.evaluate(path,
				document,
				XPathConstants.STRING);
		if (value == null) {
			return currentConfig;
		}
		String[] values = value.split(",");
		if (values.length == 0) {
			return currentConfig;
		}
		EnumSet<FeatureObjectType> resFeatures = EnumSet.noneOf(FeatureObjectType.class);
		for (String featureName : values) {
			FeatureObjectType feature = FeatureObjectType.getFeatureObjectTypeByFullName(featureName);
			if (feature != null) {
				resFeatures.add(feature);
			}
		}
		resFeatures.addAll(currentConfig.getEnabledFeatures());
		return FeatureFactory.configFromValues(resFeatures);
	}
}
