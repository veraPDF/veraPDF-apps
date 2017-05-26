/**
 * 
 */
package org.verapdf.apps.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.verapdf.core.utils.FileUtils;
import org.verapdf.gui.utils.GUIConstants;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 26 May 2017:14:37:56
 */

public final class ApplicationUtils {

	/**
	 * 
	 */
	public ApplicationUtils() {
		throw new AssertionError("Should never happen"); //$NON-NLS-1$
	}

	public static List<File> filterPdfFiles(final File[] listOfFiles) {
		List<File> retVal = new ArrayList<>();
		for (File file : listOfFiles) {
			if (file.isFile() && FileUtils.hasExtNoCase(file.getName(), GUIConstants.PDF)) {
				retVal.add(file);
			} else if (file.isDirectory()) {
				retVal.addAll(filterPdfFiles(file.listFiles()));
			}
		}
		return retVal;
	}

	public static boolean areAllExists(File[] files) {
		if (files == null || files.length == 0) {
			return false;
		}
		for (File file : files) {
			if (file == null || !file.exists()) {
				return false;
			}
		}
		return true;
	}

	public static boolean isLegalExtension(File[] files, String[] extensions) {
		if (files == null || files.length == 0) {
			return true;
		}
		for (File file : files) {
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
