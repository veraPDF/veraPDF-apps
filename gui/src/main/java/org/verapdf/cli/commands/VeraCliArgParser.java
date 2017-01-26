/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org> All rights
 * reserved. VeraPDF Library GUI is free software: you can redistribute it
 * and/or modify it under the terms of either: The GNU General public license
 * GPLv3+. You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the
 * source tree. If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html. The Mozilla Public License
 * MPLv2+. You should have received a copy of the Mozilla Public License along
 * with VeraPDF Library GUI as the LICENSE.MPL file in the root of the source
 * tree. If a copy of the MPL was not distributed with this file, you can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.cli.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBException;

import org.verapdf.apps.Applications;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.plugins.PluginsCollectionConfig;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * This class holds all command-line options used by VeraPDF application.
 *
 * @author Timur Kamalov
 */
public class VeraCliArgParser implements VeraCliArgs {
	final static String FLAG_SEP = ""; //$NON-NLS-1$
	final static String OPTION_SEP = ""; //$NON-NLS-1$
	final static String HELP_FLAG = FLAG_SEP + "h"; //$NON-NLS-1$
	final static String HELP = OPTION_SEP + "help"; //$NON-NLS-1$
	final static String HELP_MESSAGE = "Shows this message and exits.";
	final static String VERSION = OPTION_SEP + "version"; //$NON-NLS-1$
	final static String VERSION_MESSAGE = "Displays veraPDF version information.";
	final static String FLAVOUR_FLAG = FLAG_SEP + "f"; //$NON-NLS-1$
	final static String FLAVOUR = OPTION_SEP + "flavour"; //$NON-NLS-1$
	final static String FLAVOUR_MESSAGE = "Chooses built-in Validation Profile flavour, e.g. '1b'. Alternatively, supply '0' or no argument for automatic flavour detection based on a file's metadata.";
	final static String SUCCESS = OPTION_SEP + "success"; //$NON-NLS-1$
	final static String PASSED = OPTION_SEP + "passed"; //$NON-NLS-1$
	final static String SUCCESS_MESSAGE = "Logs successful validation checks.";
	final static String LIST_FLAG = FLAG_SEP + "l"; //$NON-NLS-1$
	final static String LIST = OPTION_SEP + "list"; //$NON-NLS-1$
	final static String LIST_MESSAGE = "Lists built-in Validation Profiles.";
	final static String LOAD_PROFILE_FLAG = FLAG_SEP + "p"; //$NON-NLS-1$
	final static String LOAD_PROFILE = OPTION_SEP + "profile"; //$NON-NLS-1$
	final static String LOAD_PROFILE_MESSAGE = "Loads a Validation Profile from given path and exits if loading fails. This overrides any choice or default implied by the -f / --flavour option.";
	final static String EXTRACT_FLAG = FLAG_SEP + "x"; //$NON-NLS-1$
	final static String EXTRACT = OPTION_SEP + "extract"; //$NON-NLS-1$
	final static String EXTRACT_MESSAGE = "Extracts and reports PDF features.";
	final static String FORMAT = OPTION_SEP + "format"; //$NON-NLS-1$
	final static String FORMAT_DESCRIPTION = "Chooses output format.";
	final static String RECURSE_FLAG = FLAG_SEP + "r"; //$NON-NLS-1$
	final static String RECURSE = OPTION_SEP + "recurse"; //$NON-NLS-1$
	final static String RECURSE_DESCRIPTION = "Recurses through directories. Only files with .pdf extensions are processed.";
	final static String VERBOSE_FLAG = FLAG_SEP + "v"; //$NON-NLS-1$
	final static String VERBOSE = OPTION_SEP + "verbose"; //$NON-NLS-1$
	final static String VERBOSE_DESCRIPTION = "Adds failed test information to text output.";
	final static String MAX_FAILURES_DISPLAYED = OPTION_SEP + "maxfailuresdisplayed"; //$NON-NLS-1$
	final static String MAX_FAILURES_DISPLAYED_DESCRIPTION = "Sets maximum amount of failed checks displayed for each rule.";
	final static String MAX_FAILURES = OPTION_SEP + "maxfailures"; //$NON-NLS-1$
	final static String MAX_FAILURES_DESCRIPTION = "Sets maximum amount of failed checks.";
	final static String FIX_METADATA = OPTION_SEP + "fixmetadata"; //$NON-NLS-1$
	final static String FIX_METADATA_DESCRIPTION = "Performs metadata fixes.";
	final static String FIX_METADATA_PREFIX = OPTION_SEP + "prefix"; //$NON-NLS-1$
	final static String FIX_METADATA_PREFIX_DESCRIPTION = "Sets file name prefix for any fixed files.";
	final static String FIX_METADATA_FOLDER = OPTION_SEP + "savefolder"; //$NON-NLS-1$
	final static String FIX_METADATA_FOLDER_DESCRIPTION = "Sets output directory for any fixed files.";
	final static String POLICY_FILE = OPTION_SEP + "policyfile"; //$NON-NLS-1$
	final static String POLICY_FILE_DESCRIPTION = "Select a policy schematron or XSL file.";
	// final static String PROFILES_WIKI_FLAG = FLAG_SEP + "pw";
	// final static String LOAD_CONFIG_FLAG = FLAG_SEP + "c";
	// final static String LOAD_CONFIG = OPTION_SEP + "config";
	// final static String PROFILES_WIKI = OPTION_SEP + "profilesWiki";
	// final static String POLICY_PROFILE = OPTION_SEP + "policyProfile";
	// final static String REPORT_FILE = OPTION_SEP + "reportfile";
	// final static String REPORT_FOLDER = OPTION_SEP + "reportfolder";
	// final static String OVERWRITE_REPORT_FILE = OPTION_SEP +
	// "overwriteReportFile";
	final static String VALID_OFF_FLAG = FLAG_SEP + "o"; //$NON-NLS-1$
	final static String VALID_OFF = OPTION_SEP + "off"; //$NON-NLS-1$
	final static String VALID_OFF_DESCRIPTION = "Turns off PDF/A validation";

	private final OptionParser parser;
	private final boolean help;
	private final boolean showVersion;
	private final PDFAFlavour flavour;
	private final boolean passed;
	private final boolean listProfiles;
	private final File profileFile;
	private final boolean features;
	private final FormatOption format;
	private final boolean isRecurse;
	private final boolean isVerbose;
	private final int maxFailuresDisplayed;
	private final int maxFailures;
	private final boolean fixMetadata;
	private final String prefix;
	private final String saveFolder;
	private final File policyFile;

	// @Parameter(names = { PROFILES_WIKI_FLAG,
	// PROFILES_WIKI }, description = "Sets location of the Validation Profiles
	// wiki.")
	// private String profilesWikiPath =
	// Applications.defaultConfig().getWikiPath();
	//
	// @Parameter(names = {
	// POLICY_PROFILE }, description = "Uses policy check output with specified
	// Policy Profile. Output format option will be ignored.")
	// private String policyProfilePath = "";
	//
	// @Parameter(names = {
	// REPORT_FOLDER }, description = "Sets output directory for any reports. If
	// a directory hierarchy is being recursed, a duplicate hierarchy will be
	// produced.")
	// private String reportFolder = "";
	//
	// @Parameter(names = { REPORT_FILE }, description = "Sets output file for
	// any reports.")
	// private String reportFile = "";
	//
	// @Parameter(names = { OVERWRITE_REPORT_FILE }, description = "Overwrites
	// report file.")
	// private boolean isOverwriteReportFile = false;
	private final boolean isValidationOff;
	private final List<File> pdfPaths;

	public VeraCliArgParser(final String[] args) {
		this.parser = new OptionParser();
		parser.acceptsAll(Arrays.asList(HELP_FLAG, HELP), HELP_MESSAGE).forHelp();
		parser.accepts(VERSION, VERSION_MESSAGE);
		OptionSpec<FlavourConvertor> flav = parser.acceptsAll(Arrays.asList(FLAVOUR_FLAG, FLAVOUR), FLAVOUR_MESSAGE)
				.withRequiredArg().ofType(FlavourConvertor.class);
		parser.acceptsAll(Arrays.asList(SUCCESS, PASSED), SUCCESS_MESSAGE);
		parser.acceptsAll(Arrays.asList(LIST_FLAG, LIST), LIST_MESSAGE);
		OptionSpec<File> prof = parser.acceptsAll(Arrays.asList(LOAD_PROFILE_FLAG, LOAD_PROFILE), LOAD_PROFILE_MESSAGE)
				.withRequiredArg().ofType(File.class);
		parser.acceptsAll(Arrays.asList(EXTRACT_FLAG, EXTRACT), EXTRACT_MESSAGE);
		OptionSpec<FormatConvertor> form = parser.accepts(FORMAT, FORMAT_DESCRIPTION).withRequiredArg()
				.ofType(FormatConvertor.class);
		parser.acceptsAll(Arrays.asList(RECURSE_FLAG, RECURSE), RECURSE_DESCRIPTION);
		parser.acceptsAll(Arrays.asList(VERBOSE_FLAG, VERBOSE), VERBOSE_DESCRIPTION);
		OptionSpec<Integer> maxFailsDisp = parser.accepts(MAX_FAILURES_DISPLAYED, MAX_FAILURES_DISPLAYED_DESCRIPTION)
				.withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> maxFails = parser.accepts(MAX_FAILURES, MAX_FAILURES_DESCRIPTION).withRequiredArg()
				.ofType(Integer.class);
		parser.accepts(FIX_METADATA, FIX_METADATA_DESCRIPTION);
		parser.accepts(FIX_METADATA_PREFIX, FIX_METADATA_PREFIX_DESCRIPTION).withRequiredArg();
		parser.accepts(FIX_METADATA_FOLDER, FIX_METADATA_FOLDER_DESCRIPTION).withRequiredArg();
		OptionSpec<File> pol = parser.accepts(POLICY_FILE, POLICY_FILE_DESCRIPTION).withRequiredArg()
				.ofType(File.class);
		parser.acceptsAll(Arrays.asList(VALID_OFF_FLAG, VALID_OFF), VALID_OFF_DESCRIPTION);
		OptionSpec<File> files = parser.nonOptions().ofType(File.class);

		OptionSet opts = parser.parse(args);
		this.help = opts.has(HELP_FLAG) || opts.has(HELP);
		this.showVersion = opts.has(VERSION);
		this.flavour = (opts.has(FLAVOUR_FLAG) || opts.has(FLAVOUR)) ? flav.value(opts).flavour : PDFAFlavour.NO_FLAVOUR;
		this.passed = (opts.has(SUCCESS) || opts.has(PASSED)) ? true : ValidatorFactory.defaultConfig().isRecordPasses();
		this.listProfiles = opts.has(LIST_FLAG) || opts.has(LIST);
		this.profileFile = (opts.has(LOAD_PROFILE_FLAG) || opts.has(LOAD_PROFILE)) ? prof.value(opts) : null;
		this.features = opts.has(EXTRACT_FLAG) || opts.has(EXTRACT);
		this.format = opts.has(FORMAT) ? form.value(opts).format : Applications.defaultConfig().getFormat();
		this.isRecurse = opts.has(RECURSE_FLAG) || opts.has(RECURSE);
		this.isVerbose = opts.has(VERBOSE_FLAG) || opts.has(VERBOSE);
		this.maxFailuresDisplayed = opts.has(MAX_FAILURES_DISPLAYED) ? maxFailsDisp.value(opts).intValue() : 100;
		this.maxFailures = opts.has(MAX_FAILURES) ? maxFails.value(opts).intValue()
				: ValidatorFactory.defaultConfig().getMaxFails();
		this.fixMetadata = opts.has(FIX_METADATA);
		this.prefix = opts.has(FIX_METADATA_PREFIX) ? (String) opts.valueOf(FIX_METADATA_PREFIX)
				: FixerFactory.defaultConfig().getFixesPrefix();
		this.saveFolder = opts.has(FIX_METADATA_FOLDER) ? (String) opts.valueOf(FIX_METADATA_FOLDER) : ""; //$NON-NLS-1$
		policyFile = opts.has(POLICY_FILE) ? pol.value(opts) : null;
		isValidationOff = opts.has(VALID_OFF_FLAG) || opts.has(VALID_OFF);
		pdfPaths = files.values(opts);

	}

	/**
	 * @return true if version information requested
	 */
	@Override
	public boolean showVersion() {
		return this.showVersion;
	}

	/**
	 * @return true if list of supported profiles requested
	 */
	@Override
	public boolean listProfiles() {
		return this.listProfiles;
	}

	/**
	 * @return true if to output failed rules to text output
	 */
	@Override
	public boolean isVerbose() {
		return this.isVerbose;
	}

	/**
	 * @return maximum amount of failed checks displayed for each rule
	 */
	@Override
	public int maxFailuresDisplayed() {
		return this.maxFailuresDisplayed;
	}

	/**
	 * @return maximum amount of failed checks
	 */
	@Override
	public int maxFailures() {
		return this.maxFailures;
	}

	/**
	 * @return true if metadata fix is enabled
	 */
	@Override
	public boolean fixMetadata() {
		return this.fixMetadata;
	}

	/**
	 * @return the prefix of the saved file
	 */
	@Override
	public String prefix() {
		return this.prefix;
	}

	/**
	 * @return the folder to save the fixed file to
	 */
	@Override
	public String saveFolder() {
		return this.saveFolder;
	}

	// /**
	// * @return the policy profile path
	// */
	// public String policyProfilePath() {
	// return this.policyProfilePath;
	// }

	/**
	 * @return true if to recursively process sub-dirs
	 */
	@Override
	public boolean isRecurse() {
		return this.isRecurse;
	}

	/**
	 * @return true if help requested
	 */
	@Override
	public boolean isHelp() {
		return this.help;
	}

	/**
	 * @return true if verbose output requested
	 */
	@Override
	public FormatOption getFormat() {
		return this.format;
	}

	/**
	 * @return true if log passed checks requested
	 */
	@Override
	public boolean logPassed() {
		return this.passed;
	}

	/**
	 * @return true if PDF Feature extraction requested
	 */
	@Override
	public boolean extractFeatures() {
		return this.features | this.isPolicy();
	}

	/**
	 * @return the validation flavour string id
	 */
	@Override
	public PDFAFlavour getFlavour() {
		return this.flavour;
	}

	/**
	 * @return the {@link File} object for the validation profile
	 */
	@Override
	public File getProfileFile() {
		return this.profileFile;
	}

	/**
	 * @return the {@link File} object for the validation profile
	 */
	@Override
	public File getPolicyFile() {
		return this.policyFile;
	}

	@Override
	public boolean isPolicy() {
		return this.policyFile != null;
	}

	/**
	 * @return the list of file paths
	 */
	@Override
	public List<File> getPdfPaths() {
		return this.pdfPaths;
	}

	// /**
	// * @return path to validation profiles wiki
	// */
	// public String getProfilesWikiPath() {
	// return this.profilesWikiPath;
	// }
	//
	// /**
	// * @author: mancuska@digitaldocuments.org
	// * @return folder for reports
	// */
	// public String getReportFolder() {
	// return this.reportFolder;
	// }
	//
	// /**
	// * @author: mancuska@digitaldocuments.org
	// * @return output file for report
	// */
	// public String getReportFile() {
	// return this.reportFile;
	// }

	// /**
	// * @author: mancuska@digitaldocuments.org
	// * @return true if existing result file must be overwritten
	// */
	// public boolean isOverwriteReportFile() {
	// return this.isOverwriteReportFile;
	// }

	@Override
	public boolean isValidationOff() {
		return this.isValidationOff | this.isPolicy();
	}

	@Override
	public void showHelp(OutputStream dest) throws IOException {
		this.parser.printHelpOn(dest);
	}
	
	public static final class FlavourConvertor {
		public final PDFAFlavour flavour;
		private FlavourConvertor(PDFAFlavour flavour) {
			this.flavour = flavour;
		}
		public static FlavourConvertor valueOf(String value) {
			for (PDFAFlavour flavourLocal : PDFAFlavour.values()) {
				if (flavourLocal.getId().equalsIgnoreCase(value))
					return new FlavourConvertor(flavourLocal);
			}
			throw new IllegalArgumentException("Illegal --flavour argument:" + value);
		}
	}
	
	public static final class FormatConvertor {
		public final FormatOption format;
		private FormatConvertor(FormatOption format) {
			this.format = format;
		}
		
		public static FormatConvertor valueOf(final String value) {
			try {
				return new FormatConvertor(FormatOption.fromOption(value));
			} catch (NoSuchElementException e) {
				throw new IllegalArgumentException("Illegal format option value: " + value, e);
			}
		}

	}

	public ValidatorConfig validatorConfig() {
		return ValidatorFactory.createConfig(this.flavour, this.logPassed(), this.maxFailures);
	}

	public MetadataFixerConfig fixerConfig() {
		return FixerFactory.configFromValues(this.prefix, true);
	}

	public VeraAppConfig appConfig(final VeraAppConfig base) {
		Applications.Builder configBuilder = Applications.Builder.fromConfig(base);
		configBuilder.format(this.getFormat()).isVerbose(this.isVerbose()).fixerFolder(this.saveFolder);
		configBuilder.type(typeFromArgs(this));
		return configBuilder.build();
	}

	public ProcessorConfig processorConfig(final ProcessType procType, FeatureExtractorConfig featConfig,
										   PluginsCollectionConfig plugConfig)
			throws VeraPDFException {
		if (this.profileFile == null) {
			return ProcessorFactory.fromValues(this.validatorConfig(), featConfig, plugConfig, this.fixerConfig(),
					procType.getTasks(), this.saveFolder);
		}
		try (InputStream fis = new FileInputStream(this.profileFile)) {
			ValidationProfile customProfile = Profiles.profileFromXml(fis);
			return ProcessorFactory.fromValues(this.validatorConfig(), featConfig, plugConfig, this.fixerConfig(),
					procType.getTasks(), customProfile, this.saveFolder);
		} catch (IOException | JAXBException excep) {
			throw new VeraPDFException("Problem loading custom profile", excep);
		}
	}

	private static ProcessType typeFromArgs(VeraCliArgParser parser) {
		ProcessType retVal = (parser.isValidationOff() && !parser.isPolicy()) ? ProcessType.NO_PROCESS
				: ProcessType.VALIDATE;
		if (parser.extractFeatures() || parser.isPolicy())
			retVal = ProcessType.addProcess(retVal, ProcessType.EXTRACT);
		if (parser.fixMetadata())
			retVal = ProcessType.addProcess(retVal, ProcessType.FIX);
		return retVal;
	}
}
