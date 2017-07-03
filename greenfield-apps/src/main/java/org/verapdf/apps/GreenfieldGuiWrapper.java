/**
 * 
 */
package org.verapdf.apps;

import org.verapdf.gui.PDFValidationApplication;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 3 Jul 2017:09:11:11
 */

public final class GreenfieldGuiWrapper {

	/**
	 * 
	 */
	public GreenfieldGuiWrapper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PDFValidationApplication.main(args);	
		VeraGreenfieldFoundryProvider.initialise();
	}

}
