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

import static org.junit.Assert.*;

import org.junit.Test;
import org.verapdf.version.SemanticVersionNumber;
import org.verapdf.version.Versions;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 26 May 2017:02:36:17
 */

public class SoftwareUpdaterTest {
	final SoftwareUpdater updater = Applications.softwareUpdater();

	/**
	 * Test method for {@link org.verapdf.apps.SoftwareUpdaterImpl#getLatestAppsVersion()}.
	 */
	@Test
	public final void testGetLatestAppsVersion() {
		if (!this.updater.isOnline()) {
			System.out.println("Updater is offline");
			return;
		}
		SemanticVersionNumber available = this.updater.getLatestAppsVersion();
		SemanticVersionNumber current = Versions.fromInts(0, 0, 1);
		assertTrue(current.compareTo(available) < 0);
		current = Versions.fromInts(1000, 0, 0);
		assertTrue(current.compareTo(available) > 0);
	}
}
