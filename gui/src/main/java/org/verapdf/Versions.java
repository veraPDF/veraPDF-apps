/**
 * 
 */
package org.verapdf;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 26 May 2017:01:48:14
 */

public final class Versions {
	private static final String pdfBoxBuildInfo = "-PDFBOX"; //$NON-NLS-1$
	private static final String snapshotBuildInfo = "-SNAPSHOT"; //$NON-NLS-1$
	private static final String versionPrefix = "v"; //$NON-NLS-1$

	/**
	 * 
	 */
	private Versions() {
		throw new AssertionError("Should never be here");
	}

	public static SemanticVersionNumber fromString(final String versionString) {
		return VersionNumberImpl.fromString(versionString);
	}

	public static SemanticVersionNumber fromStrings(final String[] parts) {
		return VersionNumberImpl.fromStrings(parts);
	}

	public static SemanticVersionNumber fromInts(final int[] parts) {
		return VersionNumberImpl.fromInts(parts);
	}

	public static SemanticVersionNumber fromInts(final int major, final int minor, final int revision) {
		return VersionNumberImpl.fromInts(major, minor, revision);
	}

}
