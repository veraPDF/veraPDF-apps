/**
 * 
 */
package org.verapdf.apps;

import org.verapdf.gui.PDFValidationApplication;
import org.verapdf.pdfa.PdfBoxFoundryProvider;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 3 Jul 2017:09:12:25
 */

public final class PdfBoxGuiWrapper {

	/**
	 * 
	 */
	private PdfBoxGuiWrapper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialiser PDF Box Foundry
		PdfBoxFoundryProvider.initialise();
		PDFValidationApplication.main(args);	
	}

}
