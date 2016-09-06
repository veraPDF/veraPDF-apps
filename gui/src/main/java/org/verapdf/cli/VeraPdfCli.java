/**
 *
 */
package org.verapdf.cli;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.verapdf.ReleaseDetails;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.pdfa.validation.ProfileDirectory;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.pdfa.validation.ValidationProfile;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
public final class VeraPdfCli {
	private static final int MEGABYTE = (1024*1024);
	private static final String APP_NAME = "veraPDF";
	private static final String FLAVOURS_HEADING = APP_NAME + " supported PDF/A profiles:";
	private static final ProfileDirectory PROFILES = Profiles.getVeraProfileDirectory();

	private VeraPdfCli() {
		// disable default constructor
	}

	/**
	 * Main CLI entry point, process the command line arguments
	 *
	 * @param args
	 *            Java.lang.String array of command line args, to be processed
	 *            using Apache commons CLI.
	 */
	public static void main(final String[] args) {
	    MemoryMXBean memoryMan = ManagementFactory.getMemoryMXBean();
		ReleaseDetails.addDetailsFromResource(
				ReleaseDetails.APPLICATION_PROPERTIES_ROOT + "app." + ReleaseDetails.PROPERTIES_EXT);
		VeraCliArgParser cliArgParser = new VeraCliArgParser();
		JCommander jCommander = new JCommander(cliArgParser);
		jCommander.setProgramName(APP_NAME);

		try {
			jCommander.parse(args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			showVersionInfo();
			jCommander.usage();
			System.exit(1);
		}
		if (cliArgParser.isHelp()) {
			showVersionInfo();
			jCommander.usage();
			System.exit(0);
		}
		messagesFromParser(cliArgParser);
		if (isProcess(cliArgParser)) {
			try {
				VeraPdfCliProcessor processor = VeraPdfCliProcessor.createProcessorFromArgs(cliArgParser);
				processor.processPaths(cliArgParser.getPdfPaths());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OutOfMemoryError oome) {
	            MemoryUsage heapUsage = memoryMan.getHeapMemoryUsage();
	            long maxMemory = heapUsage.getMax() / MEGABYTE;
	            long usedMemory = heapUsage.getUsed() / MEGABYTE;
	            System.out.println("The JVM appears to have run out of memory");
	            System.out.println("Memory Use :" + usedMemory + "M/" + maxMemory + "M");
	            System.out.println("To increase the memory available to the JVM please assign the JAVA_OPTS environment variable.");
	            System.out.println("The examples below increase the maximum heap available to the JVM to 2GB:");
	            System.out.println(" - Mac or Linux users: ");
	            System.out.println("   export JAVA_OPTS=\"-Xmx2048m\"");
	            System.out.println(" - Windows users: ");
	            System.out.println("   SET JAVA_OPTS=\"-Xmx2048m\"");
			}
		}
	}

	private static void messagesFromParser(final VeraCliArgParser parser) {

		if (parser.listProfiles()) {
			listProfiles();
		}

		if (parser.showVersion()) {
			showVersionInfo();
		}
	}

	private static void listProfiles() {
		System.out.println(FLAVOURS_HEADING);
		for (ValidationProfile profile : PROFILES.getValidationProfiles()) {
			System.out.println("  " + profile.getPDFAFlavour().getId() + " - " + profile.getDetails().getName());
		}
		System.out.println();
	}

	private static void showVersionInfo() {
		ReleaseDetails details = ReleaseDetails.byId("gui");
		System.out.println("Version: " + details.getVersion());
		System.out.println("Built: " + details.getBuildDate());
		System.out.println(details.getRights());
		System.out.println();
	}

	private static boolean isProcess(final VeraCliArgParser parser) {
		if (parser.getPdfPaths().isEmpty() && (parser.isHelp() || parser.listProfiles() || parser.showVersion())) {
			return false;
		}
		return true;
	}
}
