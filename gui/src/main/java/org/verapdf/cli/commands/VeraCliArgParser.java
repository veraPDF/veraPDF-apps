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
package org.verapdf.cli.commands;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.verapdf.apps.Applications;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.apps.utils.ApplicationUtils;
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
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class holds all command-line options used by VeraPDF application.
 *
 * @author Timur Kamalov
 */
public class VeraCliArgParser {
	final static VeraCliArgParser DEFAULT_ARGS = new VeraCliArgParser();
	final static String FLAG_SEP = "-"; //$NON-NLS-1$
	final static String OPTION_SEP = "--"; //$NON-NLS-1$
	final static String HELP_FLAG = FLAG_SEP + "h"; //$NON-NLS-1$
	final static String HELP = OPTION_SEP + "help"; //$NON-NLS-1$
	final static String VERSION = OPTION_SEP + "version"; //$NON-NLS-1$
	final static String FLAVOUR_FLAG = FLAG_SEP + "f"; //$NON-NLS-1$
	final static String FLAVOUR = OPTION_SEP + "flavour"; //$NON-NLS-1$
	final static String SUCCESS = OPTION_SEP + "success"; //$NON-NLS-1$
	final static String PASSED = OPTION_SEP + "passed"; //$NON-NLS-1$
	final static String LIST_FLAG = FLAG_SEP + "l"; //$NON-NLS-1$
	final static String LIST = OPTION_SEP + "list"; //$NON-NLS-1$
	final static String LOAD_PROFILE_FLAG = FLAG_SEP + "p"; //$NON-NLS-1$
	final static String LOAD_PROFILE = OPTION_SEP + "profile"; //$NON-NLS-1$
	final static String EXTRACT_FLAG = FLAG_SEP + "x"; //$NON-NLS-1$
	final static String EXTRACT = OPTION_SEP + "extract"; //$NON-NLS-1$
	final static String FORMAT = OPTION_SEP + "format"; //$NON-NLS-1$
	final static String RECURSE_FLAG = FLAG_SEP + "r"; //$NON-NLS-1$
	final static String RECURSE = OPTION_SEP + "recurse"; //$NON-NLS-1$
	final static String SERVER_MODE = OPTION_SEP + "servermode"; //$NON-NLS-1$
	final static String VERBOSE_FLAG = FLAG_SEP + "v"; //$NON-NLS-1$
	final static String VERBOSE = OPTION_SEP + "verbose"; //$NON-NLS-1$
	final static String MAX_FAILURES_DISPLAYED = OPTION_SEP + "maxfailuresdisplayed"; //$NON-NLS-1$
	final static String MAX_FAILURES = OPTION_SEP + "maxfailures"; //$NON-NLS-1$
	final static String FIX_METADATA = OPTION_SEP + "fixmetadata"; //$NON-NLS-1$
	final static String FIX_METADATA_PREFIX = OPTION_SEP + "prefix"; //$NON-NLS-1$
	final static String FIX_METADATA_FOLDER = OPTION_SEP + "savefolder"; //$NON-NLS-1$
	final static String POLICY_FILE = OPTION_SEP + "policyfile"; //$NON-NLS-1$
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
	final static String NUMBER_OF_PROCESSES_FLAG = OPTION_SEP + "processes"; //$NON-NLS-1$
	final static String VERA_PATH_FLAG = OPTION_SEP + "verapath";

	@Parameter(names = { HELP_FLAG, HELP }, description = "Shows this message and exits.", help = true)
	private boolean help = false;

	@Parameter(names = { VERSION }, description = "Displays veraPDF version information.")
	private boolean showVersion = false;

	@Parameter(names = { FLAVOUR_FLAG,
			FLAVOUR }, description = "Chooses built-in Validation Profile flavour, e.g. '1b'. Alternatively, supply '0' or no argument for automatic flavour detection based on a file's metadata.", converter = FlavourConverter.class)
	private PDFAFlavour flavour = PDFAFlavour.NO_FLAVOUR;

	@Parameter(names = { SUCCESS, PASSED }, description = "Logs successful validation checks.")
	private boolean passed = ValidatorFactory.defaultConfig().isRecordPasses();

	@Parameter(names = { LIST_FLAG, LIST }, description = "Lists built-in Validation Profiles.")
	private boolean listProfiles = false;

	@Parameter(names = { LOAD_PROFILE_FLAG,
			LOAD_PROFILE }, description = "Loads a Validation Profile from given path and exits if loading fails. This overrides any choice or default implied by the -f / --flavour option.", validateWith = FileValidator.class)
	private File profileFile;

	@Parameter(names = { EXTRACT_FLAG, EXTRACT }, description = "Extracts and reports PDF features.")
	private boolean features = false;

	@Parameter(names = { FORMAT }, description = "Chooses output format.", converter = FormatConverter.class)
	private FormatOption format = Applications.defaultConfig().getFormat();

	@Parameter(names = { RECURSE_FLAG,
			RECURSE }, description = "Recurses through directories. Only files with .pdf extensions are processed.")
	private boolean isRecurse = false;

	@Parameter(names = {SERVER_MODE}, description = "Run veraPDF in server mode. Changes output and ignore " + NUMBER_OF_PROCESSES_FLAG + "argument.", hidden = true)
	private boolean isServerMode = false;

	@Parameter(names = { VERBOSE_FLAG, VERBOSE }, description = "Adds failed test information to text output.")
	private boolean isVerbose = false;

	@Parameter(names = {
			MAX_FAILURES_DISPLAYED }, description = "Sets maximum amount of failed checks displayed for each rule.")
	private int maxFailuresDisplayed = 100;

	@Parameter(names = { MAX_FAILURES }, description = "Sets maximum amount of failed checks.")
	private int maxFailures = ValidatorFactory.defaultConfig().getMaxFails();

	@Parameter(names = { FIX_METADATA }, description = "Performs metadata fixes.")
	private boolean fixMetadata = false;

	@Parameter(names = { FIX_METADATA_PREFIX }, description = "Sets file name prefix for any fixed files.")
	private String prefix = FixerFactory.defaultConfig().getFixesPrefix();

	@Parameter(names = { FIX_METADATA_FOLDER }, description = "Sets output directory for any fixed files.")
	private String saveFolder = ""; //$NON-NLS-1$

	@Parameter(names = {
			POLICY_FILE }, description = "Select a policy schematron or XSL file.", validateWith = FileValidator.class)
	private File policyFile;

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

	@Parameter(names = { VALID_OFF_FLAG, VALID_OFF }, description = "Turns off PDF/A validation")
	private boolean isValidationOff = false;

	@Parameter(names = {NUMBER_OF_PROCESSES_FLAG}, description = "The Number of processes which will be used.")
	private int numberOfProcesses = 1;

	@Parameter(names = {VERA_PATH_FLAG}, description = "Path to veraPDF Cli", hidden = true, validateWith = FileValidator.class)
	private File veraCLIPath;

	@Parameter(description = "FILES")
	private List<String> pdfPaths = new ArrayList<>();

	/**
	 * @return true if version information requested
	 */
	public boolean showVersion() {
		return this.showVersion;
	}

	/**
	 * @return true if list of supported profiles requested
	 */
	public boolean listProfiles() {
		return this.listProfiles;
	}

	/**
	 * @return true if to output failed rules to text output
	 */
	public boolean isVerbose() {
		return this.isVerbose;
	}

	/**
	 * @return maximum amount of failed checks displayed for each rule
	 */
	public int maxFailuresDisplayed() {
		return this.maxFailuresDisplayed;
	}

	/**
	 * @return maximum amount of failed checks
	 */
	public int maxFailures() {
		return this.maxFailures;
	}

	/**
	 * @return true if metadata fix is enabled
	 */
	public boolean fixMetadata() {
		return this.fixMetadata;
	}

	/**
	 * @return the prefix of the saved file
	 */
	public String prefix() {
		return this.prefix;
	}

	/**
	 * @return the folder to save the fixed file to
	 */
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
	public boolean isRecurse() {
		return this.isRecurse;
	}

	/**
	 * @return true if server mode enabled
	 */
	public boolean isServerMode() {
		return this.isServerMode;
	}

	/**
	 * @return true if help requested
	 */
	public boolean isHelp() {
		return this.help;
	}

	/**
	 * @return true if verbose output requested
	 */
	public FormatOption getFormat() {
		return this.format;
	}

	/**
	 * @return true if log passed checks requested
	 */
	public boolean logPassed() {
		return this.passed;
	}

	/**
	 * @return true if PDF Feature extraction requested
	 */
	public boolean extractFeatures() {
		return this.features | this.isPolicy();
	}

	/**
	 * @return the validation flavour string id
	 */
	public PDFAFlavour getFlavour() {
		return this.flavour;
	}

	/**
	 * @return the {@link File} object for the validation profile
	 */
	public File getProfileFile() {
		return this.profileFile;
	}

	/**
	 * @return the {@link File} object for the validation profile
	 */
	public File getPolicyFile() {
		return this.policyFile;
	}

	public boolean isPolicy() {
		return this.policyFile != null;
	}

	public File getVeraCLIPath() {
		return veraCLIPath;
	}

	public int getNumberOfProcesses() {
		return numberOfProcesses;
	}

	/**
	 * @return the list of file paths
	 */
	public List<String> getPdfPaths() {
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

	public boolean isValidationOff() {
		return this.isValidationOff | this.isPolicy();
	}

	/**
	 * JCommander parameter converter for {@link FormatOption}, see
	 * {@link IStringConverter} and {@link FormatOption#fromOption(String)}.
	 *
	 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
	 */
	public static final class FormatConverter implements IStringConverter<FormatOption> {
		/**
		 * { @inheritDoc }
		 */
		@Override
		public FormatOption convert(final String value) {
			try {
				return FormatOption.fromOption(value);
			} catch (NoSuchElementException e) {
				throw new ParameterException("Illegal format option value: " + value, e);
			}
		}

	}

	/**
	 * JCommander parameter converter for {@link PDFAFlavour}, see
	 * {@link IStringConverter} and {@link PDFAFlavour#byFlavourId(String)}.
	 *
	 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
	 */
	public static final class FlavourConverter implements IStringConverter<PDFAFlavour> {
		/**
		 * { @inheritDoc }
		 */
		@Override
		public PDFAFlavour convert(final String value) {
			for (PDFAFlavour flavourLocal : PDFAFlavour.values()) {
				if (flavourLocal.getId().equalsIgnoreCase(value))
					return flavourLocal;
			}
			throw new ParameterException("Illegal --flavour argument:" + value);
		}

	}

	/**
	 * JCommander parameter validator for {@link File}, see
	 * {@link IParameterValidator}. Enforces an existing, readable file.
	 *
	 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
	 */
	public static final class FileValidator implements IParameterValidator {
		/**
		 * { @inheritDoc }
		 */
		@Override
		public void validate(final String name, final String value) throws ParameterException {
			File profileFileLocal = new File(value);
			if (!profileFileLocal.isFile() || !profileFileLocal.canRead()) {
				throw new ParameterException(
						"Parameter " + name + " must be the path to an existing, readable file, value=" + value);
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
		configBuilder.maxFails(this.maxFailuresDisplayed);
		return configBuilder.build();
	}

	public ProcessorConfig processorConfig(final ProcessType procType, FeatureExtractorConfig featConfig,
										   PluginsCollectionConfig plugConfig)
			throws VeraPDFException {
		FeatureExtractorConfig featuresConfig = featConfig;
		if (isPolicy()) {
			try (InputStream policyStream = new FileInputStream(this.policyFile)) {
				featuresConfig = ApplicationUtils.mergeEnabledFeaturesFromPolicy(featuresConfig, policyStream);
			} catch (SAXException | XPathExpressionException | IOException | ParserConfigurationException e) {
				throw new VeraPDFException("Problem during obtaining feature types from policy file", e);
			}
		}
		if (this.profileFile == null) {
			return ProcessorFactory.fromValues(this.validatorConfig(), featuresConfig, plugConfig, this.fixerConfig(),
					procType.getTasks(), this.saveFolder);
		}
		try (InputStream fis = new FileInputStream(this.profileFile)) {
			ValidationProfile customProfile = Profiles.profileFromXml(fis);
			return ProcessorFactory.fromValues(this.validatorConfig(), featuresConfig, plugConfig, this.fixerConfig(),
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

	public static List<String> getBaseVeraPDFParameters(VeraCliArgParser cliArgParser) {
		List<String> veraPDFParameters = new ArrayList<>();

		veraPDFParameters.add("--servermode");
		if (cliArgParser.extractFeatures()) {
			veraPDFParameters.add("-x");
		}
		if (cliArgParser.fixMetadata()) {
			veraPDFParameters.add("--fixmetadata");
		}
		veraPDFParameters.add("--flavour");
		veraPDFParameters.add(String.valueOf(cliArgParser.getFlavour()));
		veraPDFParameters.add("--format");
		veraPDFParameters.add(String.valueOf(cliArgParser.getFormat()));
		if (cliArgParser.listProfiles()) {
			veraPDFParameters.add("--list");
		}
		veraPDFParameters.add("--maxfailures");
		veraPDFParameters.add(String.valueOf(cliArgParser.maxFailures()));
		veraPDFParameters.add("--maxfailuresdisplayed");
		veraPDFParameters.add(String.valueOf(cliArgParser.maxFailuresDisplayed()));
		if (cliArgParser.isValidationOff()) {
			veraPDFParameters.add("--off");
		}
		File policyFile = cliArgParser.getPolicyFile();
		if (policyFile != null) {
			veraPDFParameters.add("--policyfile");
			veraPDFParameters.add(policyFile.getAbsolutePath());
		}
		veraPDFParameters.add("--prefix");
		veraPDFParameters.add(cliArgParser.prefix());
		File profileFile = cliArgParser.getProfileFile();
		if (profileFile!=null) {
			veraPDFParameters.add("-p");
			veraPDFParameters.add(profileFile.getAbsolutePath());
		}
		veraPDFParameters.add("--savefolder");
		veraPDFParameters.add(cliArgParser.saveFolder());
		if (cliArgParser.logPassed()) {
			veraPDFParameters.add("--success");
		}
		if (cliArgParser.isVerbose()) {
			veraPDFParameters.add("--verbose");
		}

		return veraPDFParameters;
	}
}
