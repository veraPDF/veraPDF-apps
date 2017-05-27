/**
 * 
 */
package org.verapdf.apps.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.verapdf.apps.Applications;
import org.verapdf.core.utils.FileUtils;
import org.verapdf.gui.utils.GUIConstants;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 26 May 2017:14:37:56
 */

public final class ApplicationUtils {

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
	public static List<File> filterPdfFiles(final List<File> toFilter) {
		Applications.checkArgNotNull(toFilter, "toFilter"); //$NON-NLS-1$
		List<File> retVal = new ArrayList<>();
		for (File file : toFilter) {
			if (file.isFile() && FileUtils.hasExtNoCase(file.getName(), GUIConstants.PDF)) {
				retVal.add(file);
			} else if (file.isDirectory()) {
				retVal.addAll(filterPdfFiles(Arrays.asList(file.listFiles())));
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

}
