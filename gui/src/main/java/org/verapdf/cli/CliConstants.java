/**
 * 
 */
package org.verapdf.cli;

import java.util.Arrays;
import java.util.List;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 27 May 2017:16:50:57
 */

public final class CliConstants {
	private CliConstants() {
		throw new AssertionError("Should never be here."); //$NON-NLS-1$
	}

	public static final String APP_NAME = "veraPDF"; //$NON-NLS-1$

	public static final String EXCEP_PROCESSOR_CLOSE = "IOExeption raised when closing ItemProcessor";
	public static final String EXCEP_REPORT_MARSHAL = "JAXBEception raised when marshalling report.";
	public static final String EXCEP_REPORT_CLOSE = "Cannot close the report file.";
	public static final String EXCEP_TEMP_MRR_CREATE = "Failed to create temporary MRR file";
	public static final String EXCEP_TEMP_MRR_CLOSE = "Exception raised closing MRR temp file.";
	public static final String EXCEP_REPORT_OVERWRITE = "Cannot delete existing report file : %s.";
	public static final String EXCEP_VERA_BATCH = "VeraPDFException raised while processing batch";

	public static final String MESS_PDF_ENCRYPTED = "%s is an encrypted PDF document.";
	public static final String MESS_PDF_NOT_VALID = "%s is not a valid PDF.";
	public static final String MESS_PROC_STDIN_1 = "veraPDF is processing STDIN and is expecting an EOF marker.";
	public static final String MESS_PROC_STDIN_2 = "If this isn't your intention you can terminate by typing an EOF equivalent:";
	public static final String MESS_PROC_STDIN_3 = " - Linux or Mac users should type CTRL-D";
	public static final String MESS_PROC_STDIN_4 = " - Windows users should type CTRL-Z";
	public static final List<String> MESS_PROC_STDIN = Arrays.asList(new String[] {
			MESS_PROC_STDIN_1,
			MESS_PROC_STDIN_2,
			MESS_PROC_STDIN_3,
			MESS_PROC_STDIN_4
	});

	public static final String NAME_STDIN = "STDIN";
}
