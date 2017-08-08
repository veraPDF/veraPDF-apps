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
	 * @return an immutable list of the filtered files, i.e. all PDF extenstions
	 * @throws IllegalArgumentException
	 *             when toFilter is null
	 */
	public static List<File> filterPdfFiles(final List<File> toFilter, final boolean isRecursive) {
		Applications.checkArgNotNull(toFilter, "toFilter"); //$NON-NLS-1$
		List<File> retVal = new ArrayList<>();
		for (File file : toFilter) {
			if (!file.exists()) {
				LOGGER.log(Level.SEVERE, "File " + file.getAbsolutePath() + " doesn't exist.");
				continue;
			}
			if (file.isFile()) {
				if (FileUtils.hasExtNoCase(file.getName(), GUIConstants.PDF)) {
					retVal.add(file);
				} else {
					LOGGER.log(Level.SEVERE, "File " + file.getAbsolutePath() + " doesn't have a .pdf extension.");
				}
			} else if (file.isDirectory()) {
				retVal.addAll(filterPdfFilesFromDirs(Arrays.asList(file.listFiles()), isRecursive));
			}
		}
		return Collections.unmodifiableList(retVal);
	}

	private static List<File> filterPdfFilesFromDirs(final List<File> toFilter,
											 final boolean isRecursive) {
		Applications.checkArgNotNull(toFilter, "toFilter"); //$NON-NLS-1$
		List<File> retVal = new ArrayList<>();
		for (File file : toFilter) {
			if (file.isFile() && FileUtils.hasExtNoCase(file.getName(), GUIConstants.PDF)) {
				retVal.add(file);
			} else if (file.isDirectory() && isRecursive) {
				retVal.addAll(filterPdfFilesFromDirs(Arrays.asList(file.listFiles()), isRecursive));
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
			}
		}
		return true;
	}

	public static FeatureExtractorConfig mergeEnabledFeaturesFromPolicy(FeatureExtractorConfig currentConfig, InputStream policy) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(policy);
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		SimpleNamespaceContext nsc = new SimpleNamespaceContext();
		nsc.setPrefix(SchematronGenerator.SCH_PREFIX, SchematronGenerator.SCH_NAMESPACE);
		xpath.setNamespaceContext(nsc);
		String path = "/" + SchematronGenerator.SCH_PREFIX + ":" + SchematronGenerator.ROOT_NAME
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
		for (FeatureObjectType type : currentConfig.getEnabledFeatures()) {
			resFeatures.add(type);
		}
		return FeatureFactory.configFromValues(resFeatures);
	}
}
