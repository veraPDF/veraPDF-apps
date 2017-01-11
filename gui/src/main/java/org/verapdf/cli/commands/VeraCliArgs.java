package org.verapdf.cli.commands;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.processor.FormatOption;

public interface VeraCliArgs {

	/**
	 * @return true if version information requested
	 */
	boolean showVersion();

	/**
	 * @return true if list of supported profiles requested
	 */
	boolean listProfiles();

	/**
	 * @return true if to output failed rules to text output
	 */
	boolean isVerbose();

	/**
	 * @return maximum amount of failed checks displayed for each rule
	 */
	int maxFailuresDisplayed();

	/**
	 * @return maximum amount of failed checks
	 */
	int maxFailures();

	/**
	 * @return true if metadata fix is enabled
	 */
	boolean fixMetadata();

	/**
	 * @return the prefix of the saved file
	 */
	String prefix();

	/**
	 * @return the folder to save the fixed file to
	 */
	String saveFolder();

	/**
	 * @return true if to recursively process sub-dirs
	 */
	boolean isRecurse();

	/**
	 * @return true if help requested
	 */
	boolean isHelp();

	/**
	 * @return true if verbose output requested
	 */
	FormatOption getFormat();

	/**
	 * @return true if log passed checks requested
	 */
	boolean logPassed();

	/**
	 * @return true if PDF Feature extraction requested
	 */
	boolean extractFeatures();

	/**
	 * @return the validation flavour string id
	 */
	PDFAFlavour getFlavour();

	/**
	 * @return the {@link File} object for the validation profile
	 */
	File getProfileFile();

	/**
	 * @return the {@link File} object for the validation profile
	 */
	File getPolicyFile();

	boolean isPolicy();

	/**
	 * @return the list of file paths
	 */
	List<File> getPdfPaths();

	boolean isValidationOff();
	
	void showHelp(OutputStream dest) throws IOException;

}