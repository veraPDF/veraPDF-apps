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
package org.verapdf.apps;

import org.verapdf.ReleaseDetails;
import org.verapdf.version.SemanticVersionNumber;

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
