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

	public static final String EXCEP_PROCESSOR_CLOSE = "IOException raised when closing ItemProcessor";
	public static final String EXCEP_REPORT_MARSHAL = "JAXBException raised when marshalling report.";
	public static final String EXCEP_REPORT_CLOSE = "Cannot close the report file.";
	public static final String EXCEP_TEMP_MRR_CREATE = "Failed to create temporary MRR file";
	public static final String EXCEP_TEMP_MRR_CLOSE = "Exception raised closing MRR temp file.";
	public static final String EXCEP_REPORT_OVERWRITE = "Cannot delete existing report file : %s.";
	public static final String EXCEP_VERA_BATCH = "VeraPDFException raised while processing batch";

	public static final String MESS_PDF_ENCRYPTED = "%s is an encrypted PDF document.";
	public static final String MESS_PDF_NOT_VALID = "%s is not a valid PDF document.";
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

	/**
	 * All valid exit codes from veraPDF CLI
	 * 
	 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
	 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
	 *
	 * @version 0.1
	 */
	public enum ExitCodes {
		/** All files parsed and valid */
		VALID(0, "All files validated."),
		/** All files parsed, some invalid */
		INVALID(1, "Invalid PDF/A file(s) found."),
		/** Bad command line parameters */
		BAD_PARAMS(2, "Invalid command line parameters."),
		/** Out of Memory */
		OOM(3, "Out of Java heap space (memory)."),
		/** No files from passed list or directory */
		NO_FILES(4, "No files to process."),
		/** Java I/O Exception during processing */ 
		IO_EXCEPTION(6, "I/O Exception while processing."),
		/** Failed to parse one or more files */
		FAILED_PARSING(7, "Failed to parse one or more files."),
		/** Some PDF files encrypted. */
		ENCRYPTED_FILES(8, "Some PDFs encrypted."),
		/** veraPDF exception thrown while processing */
		VERAPDF_EXCEPTION(9, "VeraPDF exception while processing."),
		/** JAXB exception thrown while processing */
		JAXB_EXCEPTION(10, "Java XML marshalling exception while processing result."),
		/** Failed to start multiprocess */
		FAILED_MULTIPROCESS_START(11, "Failed to start multiprocess"),
		/** Interrupted exception */
		INTERRUPTED_EXCEPTION(12, "Interrupted exception while processing");

		/** The numeric exit code for return to OS. */
		public final int value;
		/** The appropriate help message for the exitCode */
		public final String message;
		
		ExitCodes(final int exitCode, final String message) {
			this.value = exitCode;
			this.message = message;
		}

		public static ExitCodes fromValue(int code) {
			for (ExitCodes exitCode : ExitCodes.values()) {
				if (code == exitCode.value) {
					return exitCode;
				}
			}
			return null;
		}
	}

	public static final String NAME_STDIN = "STDIN";
}
