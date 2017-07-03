/**
 * 
 */
package org.verapdf.apps;

import org.verapdf.cli.VeraPdfCli;
import org.verapdf.core.VeraPDFException;
import org.verapdf.pdfa.PdfBoxFoundryProvider;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 3 Jul 2017:09:12:03
 */

public final class PdfBoxCliWrapper {

	/**
	 * 
	 */
	private PdfBoxCliWrapper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws VeraPDFException {
		// Initialise the PDF Box Foundry
		PdfBoxFoundryProvider.initialise();
		VeraPdfCli.main(args);
	}

}
