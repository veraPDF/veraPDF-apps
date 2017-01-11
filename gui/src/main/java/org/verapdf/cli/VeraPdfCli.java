/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * VeraPDF Library GUI is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * VeraPDF Library GUI as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.verapdf.cli;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.verapdf.ReleaseDetails;
import org.verapdf.apps.Applications;
import org.verapdf.apps.ConfigManager;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.cli.commands.VeraCliArgs;
import org.verapdf.core.VeraPDFException;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.ProfileDirectory;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
public final class VeraPdfCli {
	private static final Logger logger = Logger.getLogger(VeraCliArgParser.class.getCanonicalName());
	private static final ConfigManager configManager = Applications.createAppConfigManager();
	private static final int MEGABYTE = (1024 * 1024);
	private static final String APP_NAME = "veraPDF"; //$NON-NLS-1$
	private static final String FLAVOURS_HEADING = APP_NAME + " supported PDF/A profiles:"; //$NON-NLS-1$
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
   public static void main(final String[] args) throws VeraPDFException, IOException {
		VeraGreenfieldFoundryProvider.initialise();
		MemoryMXBean memoryMan = ManagementFactory.getMemoryMXBean();
		ReleaseDetails.addDetailsFromResource(
				ReleaseDetails.APPLICATION_PROPERTIES_ROOT + "app." + ReleaseDetails.PROPERTIES_EXT); //$NON-NLS-1$
		VeraCliArgParser cliArgParser = new VeraCliArgParser(args);

		if (cliArgParser.isHelp()) {
			showVersionInfo();
			cliArgParser.showHelp(System.out);
			System.exit(0);
		}
		messagesFromParser(cliArgParser);
		if (isProcess(cliArgParser)) {
			try {
				VeraPdfCliProcessor processor = VeraPdfCliProcessor.createProcessorFromArgs(cliArgParser,
						configManager);
				if (args.length == 0)
					cliArgParser.showHelp(System.out);
				// FIXME: trap policy IO Exception (deliberately left un-caught for development)
				processor.processPaths(cliArgParser.getPdfPaths());
			} catch (OutOfMemoryError oome) {
				final String message = "The JVM appears to have run out of memory";
				logger.log(Level.WARNING, message, oome);
				MemoryUsage heapUsage = memoryMan.getHeapMemoryUsage();
				long maxMemory = heapUsage.getMax() / MEGABYTE;
				long usedMemory = heapUsage.getUsed() / MEGABYTE;
				System.out.println(message);
				System.out.println("Memory Use: " + usedMemory + "M/" + maxMemory + "M");
				System.out.println(
						"To increase the memory available to the JVM please assign the JAVA_OPTS environment variable.");
				System.out.println("The examples below increase the maximum heap available to the JVM to 2GB:");
				System.out.println(" - Mac or Linux users: ");
				System.out.println("   export JAVA_OPTS=\"-Xmx2048m\"");
				System.out.println(" - Windows users: ");
				System.out.println("   SET JAVA_OPTS=\"-Xmx2048m\"");
				System.exit(1);
			}
		}
	}

	private static void messagesFromParser(final VeraCliArgs parser) {

		if (parser.listProfiles()) {
			listProfiles();
		}

		if (parser.showVersion()) {
			showVersionInfo();
		}
	}

	private static void listProfiles() {
		System.out.println(FLAVOURS_HEADING);
		EnumSet<PDFAFlavour> flavs = EnumSet.copyOf(PROFILES.getPDFAFlavours());
		for (PDFAFlavour flav : flavs) {
			ValidationProfile profile = PROFILES.getValidationProfileByFlavour(flav);
			System.out.println("  " + profile.getPDFAFlavour().getId() + " - " + profile.getDetails().getName());  //$NON-NLS-1$//$NON-NLS-2$
		}
		System.out.println();
	}

	private static void showVersionInfo() {
		ReleaseDetails details = ReleaseDetails.byId("gui"); //$NON-NLS-1$
		System.out.println(APP_NAME + " " + details.getVersion()); //$NON-NLS-1$
		System.out.println("Built: " + details.getBuildDate()); //$NON-NLS-1$
		System.out.println(ReleaseDetails.rightsStatement());
		System.out.println();
	}

	private static boolean isProcess(final VeraCliArgs parser) {
		if (parser.getPdfPaths().isEmpty() && (parser.isHelp() || parser.listProfiles() || parser.showVersion())) {
			return false;
		}
		return true;
	}
}
