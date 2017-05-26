/**
 * 
 */
package org.verapdf.apps;

import org.verapdf.ReleaseDetails;
import org.verapdf.SemanticVersionNumber;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 26 May 2017:01:05:39
 */

public interface SoftwareUpdater {
	/**
	 * @return true if the online version information service is available
	 */
	public boolean isOnline();

	/**
	 * @return the latest available version of the Greenfield veraPDF apps
	 *         package
	 */
	public SemanticVersionNumber getLatestAppsVersion();

	/**
	 * @return the latest available version of the PDFBox veraPDF apps package
	 */
	public SemanticVersionNumber getLatestPdfBoxAppsVersion();

	/**
	 * @return true if there's a later version of the apps available
	 */
	public boolean isUpdateAvailable();

	/**
	 * @return true if there's a later version of the apps than that indicated
	 *         by the passed string
	 */
	public boolean isUpdateAvailable(String versionString);

	/**
	 * @return true if there's a later version of the apps than that indicated
	 *         by the passed ReleaseDetails
	 */
	public boolean isUpdateAvailable(ReleaseDetails details);

	/**
	 * @return The latest version string for the software whose version string
	 *         is passed
	 */
	public String getLatestVersion(String versionString);

	/**
	 * @return The latest version string for the software whose version string
	 *         is passed
	 */
	public String getLatestVersion(ReleaseDetails details);
}
