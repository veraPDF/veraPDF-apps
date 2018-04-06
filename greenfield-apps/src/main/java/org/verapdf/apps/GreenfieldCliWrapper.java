/**
 * 
 */
package org.verapdf.apps;

import org.verapdf.cli.VeraPdfCli;
import org.verapdf.core.VeraPDFException;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 3 Jul 2017:09:10:48
 */

public final class GreenfieldCliWrapper {

	/**
	 * 
	 */
	private GreenfieldCliWrapper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws VeraPDFException {
		// TODO Auto-generated method stub
		VeraGreenfieldFoundryProvider.initialise();
		VeraPdfCli.main(args);
	}

}
