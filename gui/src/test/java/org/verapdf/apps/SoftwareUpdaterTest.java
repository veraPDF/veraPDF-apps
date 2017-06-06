/**
 * 
 */
package org.verapdf.apps;

import static org.junit.Assert.*;

import org.junit.Test;
import org.verapdf.SemanticVersionNumber;
import org.verapdf.Versions;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 26 May 2017:02:36:17
 */

public class SoftwareUpdaterTest {
	SoftwareUpdater updater = Applications.softwareUpdater();

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
