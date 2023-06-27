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

import com.beust.jcommander.*;
import org.verapdf.apps.Applications;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.core.VeraPDFException;
import org.verapdf.core.utils.FileUtils;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.features.FeatureObjectType;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.BaseValidator;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;
import org.verapdf.processor.app.AppConfigBuilder;
import org.verapdf.processor.app.ConfigManager;
import org.verapdf.processor.app.ProcessType;
import org.verapdf.processor.app.VeraAppConfig;
import org.verapdf.processor.plugins.PluginsCollectionConfig;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class holds all command-line options used by VeraPDF application.
 *
 * @author Timur Kamalov
 */
public class VeraCliArgParser {
	private static final Logger LOGGER = Logger.getLogger(VeraCliArgParser.class.getCanonicalName());

	final static VeraCliArgParser DEFAULT_ARGS = new VeraCliArgParser();
	final static String FLAG_SEP = "-"; //$NON-NLS-1$
	final static String OPTION_SEP = "--"; //$NON-NLS-1$
	final static String HELP_FLAG = FLAG_SEP + "h"; //$NON-NLS-1$
	final static String HELP = OPTION_SEP + "help"; //$NON-NLS-1$
	final static String VERSION = OPTION_SEP + "version"; //$NON-NLS-1$
	final static String FLAVOUR_FLAG = FLAG_SEP + "f"; //$NON-NLS-1$
	final static String FLAVOUR = OPTION_SEP + "flavour"; //$NON-NLS-1$
	final static String DEFAULT_FLAVOUR_FLAG = FLAG_SEP + "df"; //$NON-NLS-1$
	final static String DEFAULT_FLAVOUR = OPTION_SEP + "defaultflavour"; //$NON-NLS-1$
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
	final static String DEBUG_FLAG = FLAG_SEP + "d"; //$NON-NLS-1$
	final static String DEBUG = OPTION_SEP + "debug"; //$NON-NLS-1$
	final static String MAX_FAILURES_DISPLAYED = OPTION_SEP + "maxfailuresdisplayed"; //$NON-NLS-1$
	final static String MAX_FAILURES = OPTION_SEP + "maxfailures"; //$NON-NLS-1$
	final static String FIX_METADATA = OPTION_SEP + "fixmetadata"; //$NON-NLS-1$
	final static String FIX_METADATA_PREFIX = OPTION_SEP + "prefix"; //$NON-NLS-1$
	final static String FIX_METADATA_FOLDER = OPTION_SEP + "savefolder"; //$NON-NLS-1$
	final static String NON_PDF_EXTENSION = OPTION_SEP + "nonpdfext";
	final static String POLICY_FILE = OPTION_SEP + "policyfile"; //$NON-NLS-1$
	final static String ADD_LOGS = OPTION_SEP + "addlogs"; //$NON-NLS-1$
	final static String DISABLE_ERROR_MESSAGES = OPTION_SEP + "disableerrormessages"; //$NON-NLS-1$
	final static String PASSWORD = OPTION_SEP + "password"; //$NON-NLS-1$
	final static String LOG_LEVEL = OPTION_SEP + "loglevel"; //$NON-NLS-1$
	final static String PROGRESS = OPTION_SEP + "progress"; //$NON-NLS-1$
	final static String PROFILES_WIKI_FLAG = FLAG_SEP + "pw";
	final static String PROFILES_WIKI = OPTION_SEP + "profilesWiki";
	final static String VALID_OFF_FLAG = FLAG_SEP + "o"; //$NON-NLS-1$
	final static String VALID_OFF = OPTION_SEP + "off"; //$NON-NLS-1$
	final static String NUMBER_OF_PROCESSES_FLAG = OPTION_SEP + "processes"; //$NON-NLS-1$
	final static String VERA_PATH_FLAG = OPTION_SEP + "verapath";
	public static final String USE_CONFIG = OPTION_SEP + "config";

	@Parameter(names = { HELP_FLAG, HELP }, description = "Shows this message and exits.", help = true)
	private boolean help = false;

	@Parameter(names = { VERSION }, description = "Displays veraPDF version information.")
	private boolean showVersion = false;

	@Parameter(names = { FLAVOUR_FLAG,
			FLAVOUR }, description = "Chooses built-in Validation Profile flavour, e.g. '1b'. Alternatively, supply '0' or no argument for automatic flavour detection based on a file's metadata.", converter = FlavourConverter.class)
	private PDFAFlavour flavour = PDFAFlavour.NO_FLAVOUR;

	@Parameter(names = { DEFAULT_FLAVOUR_FLAG,
	                     DEFAULT_FLAVOUR }, description = "Chooses built-in Validation Profile default flavour, e.g. '1b'. This flavour will be applied if automatic flavour detection based on a file's metadata doesn't work.", converter = FlavourConverter.class)
	private PDFAFlavour defaultFlavour = PDFAFlavour.PDFA_1_B;

	@Parameter(names = { SUCCESS, PASSED }, description = "Logs successful validation checks.")
	private boolean passed = ValidatorFactory.defaultConfig().isRecordPasses();

	@Parameter(names = { LIST_FLAG, LIST }, description = "Lists built-in Validation Profiles.")
	private boolean listProfiles = false;

	@Parameter(names = { LOAD_PROFILE_FLAG,
			LOAD_PROFILE }, description = "Loads a Validation Profile from given path and exits if loading fails. This overrides any choice or default implied by the -f / --flavour option.", validateWith = FileValidator.class)
	private File profileFile;

	@Parameter(names = { EXTRACT_FLAG,
	                     EXTRACT }, description = "Extracts and reports PDF features. Features must be passed separated by commas without spaces between them.", converter = FeatureConverter.class)
	private List<FeatureObjectType> features;

	@Parameter(names = { FORMAT }, description = "Chooses output format.", converter = FormatConverter.class)
	private FormatOption format = Applications.defaultConfig().getFormat();

	@Parameter(names = { RECURSE_FLAG,
			RECURSE }, description = "Recurses through directories. Only files with .pdf extensions are processed.")
	private boolean isRecurse = false;

	@Parameter(names = {SERVER_MODE}, description = "Run veraPDF in server mode. Changes output and ignore " + NUMBER_OF_PROCESSES_FLAG + "argument.", hidden = true)
	private boolean isServerMode = false;

	@Parameter(names = { VERBOSE_FLAG, VERBOSE }, description = "Adds failed test information to text output.")
	private boolean isVerbose = false;

	@Parameter(names = { DEBUG_FLAG, DEBUG }, description = "Outputs all processed file names.")
	private boolean debug = false;

	@Parameter(names = {
			MAX_FAILURES_DISPLAYED }, description = "Sets maximum amount of failed checks displayed for each rule. -1 for unlimited number of failed checks.")
	private int maxFailuresDisplayed = BaseValidator.DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS;

	@Parameter(names = { MAX_FAILURES }, description = "Sets maximum amount of failed checks.")
	private int maxFailures = ValidatorFactory.defaultConfig().getMaxFails();

	@Parameter(names = { FIX_METADATA }, description = "Performs metadata fixes.")
	private boolean fixMetadata = false;

	@Parameter(names = { ADD_LOGS }, description = "Add logs to mrr, json or html report.")
	private boolean addLogs = false;

	@Parameter(names = {DISABLE_ERROR_MESSAGES}, description = "Disable detailed error messages in the validation report.")
	private boolean disableErrorMessages = false;

	@Parameter(names = { PASSWORD }, description = "Sets the password for an encrypted document.")
	private String password;

	@Parameter(names = { LOG_LEVEL }, description = "Enables logs with level: 0 - OFF, 1 - SEVERE, 2 - WARNING, SEVERE (default), 3 - CONFIG, INFO, WARNING, SEVERE, 4 - ALL.")
	private int logLevel = 2;

	@Parameter(names = { PROGRESS }, description = "Shows the current status of the validation job.")
	private boolean showProgress;

	@Parameter(names = { FIX_METADATA_PREFIX }, description = "Sets file name prefix for any fixed files.")
	private String prefix = FixerFactory.defaultConfig().getFixesPrefix();

	@Parameter(names = { FIX_METADATA_FOLDER }, description = "Sets output directory for any fixed files.")
	private String saveFolder = ""; //$NON-NLS-1$

	@Parameter(names = { NON_PDF_EXTENSION }, description = "Select files without .pdf extension")
	private boolean nonPdfExt = false;

	@Parameter(names = {
			POLICY_FILE }, description = "Select a policy schematron or XSL file.", validateWith = FileValidator.class)
	private File policyFile;

	 @Parameter(names = { PROFILES_WIKI_FLAG,
	                      PROFILES_WIKI }, description = "Sets location of the Validation Profiles wiki.")
	 private String profilesWikiPath = Applications.defaultConfig().getWikiPath();

	@Parameter(names = { VALID_OFF_FLAG, VALID_OFF }, description = "Turns off validation")
	private boolean isValidationOff = false;

	@Parameter(names = {NUMBER_OF_PROCESSES_FLAG}, description = "The number of processes which will be used.")
	private int numberOfProcesses = 1;

	@Parameter(names = {USE_CONFIG},
	           description = "Sets settings from the config files, if no cli parameters are specified.")
	private boolean useConfig = false;

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
	 * @return true if to output file names of all processed files
	 */
	public boolean isDebug() {
		return debug;
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
	 * @return true if adding logs to xml report is enabled
	 */
	public boolean addLogs() {
		return this.addLogs;
	}

	/**
	 * @return logging level set by user:
	 * 0 - OFF
	 * 1 - SEVERE
	 * 2 - WARNING, SEVERE (default)
	 * 3 - CONFIG, INFO, WARNING, SEVERE
	 * 4 - ALL
	 */
	public int getLogLevel() {
		return this.logLevel;
	}

	public Level getLoggerLevel() {
		switch (this.getLogLevel()) {
			case 0:
				return Level.OFF;
			case 1:
				return Level.SEVERE;
			case 3:
				return Level.CONFIG;
			case 4:
				return Level.ALL;
			default:
				return Level.WARNING;
		}
	}

	public int getLoggerLevel(Level level) {
		switch (level.toString()) {
			case "OFF":
				return 0;
			case "SEVERE":
				return 1;
			case "CONFIG":
				return 3;
			case "ALL":
				return 4;
			default:
				return 2;
		}
	}

	/**
	 * @return number of checks per which set by user:
	 */
	public boolean getShowProgress() {
		return this.showProgress;
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

	public boolean nonPdfExt() {
		return this.nonPdfExt;
	}

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
	 * @return format if policy file is not specified,
	 * otherwise ignore all report format settings and always use mrr report
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
		return this.features != null || this.isPolicy();
	}

	public PDFAFlavour getDefaultFlavour() {
		return this.defaultFlavour;
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

	public String getPolicyFileName() {
		if (isPolicy()) {
			return getPolicyFile().getAbsolutePath();
		}
		return null;
	}

	public File getVeraCLIPath() {
		return veraCLIPath;
	}

	public int getNumberOfProcesses() {
		return numberOfProcesses;
	}

	public boolean useConfig() {
		return useConfig;
	}

	/**
	 * @return the list of file paths
	 */
	public List<String> getPdfPaths() {
		return this.pdfPaths;
	}

	 /**
	 * @return path to validation profiles wiki
	 */
	 public String getProfilesWikiPath() {
	 return this.profilesWikiPath;
	 }

	public boolean isValidationOff() {
		return this.isValidationOff | this.isPolicy();
	}

	public boolean isDisableErrorMessages() {
		return disableErrorMessages;
	}

	public String getPassword() {
		return password;
	}

	public void setValuesFromConfig(ConfigManager configManager) {
	 	ValidatorConfig validatorConfig = configManager.getValidatorConfig();
	 	this.flavour = validatorConfig.getFlavour();
	 	this.defaultFlavour = validatorConfig.getDefaultFlavour();
	 	this.passed = validatorConfig.isRecordPasses();
	 	this.debug = validatorConfig.isDebug();
	 	this.addLogs = validatorConfig.isLogsEnabled();
	 	this.logLevel = getLoggerLevel(validatorConfig.getLoggingLevel());
	 	this.maxFailures = validatorConfig.getMaxFails();
	 	this.maxFailuresDisplayed = validatorConfig.getMaxNumberOfDisplayedFailedChecks();
	 	this.disableErrorMessages = !validatorConfig.showErrorMessages();

	 	VeraAppConfig veraAppConfig = configManager.getApplicationConfig();
	 	this.saveFolder = veraAppConfig.getFixesFolder();
	 	this.format = veraAppConfig.getFormat();
	 	this.isVerbose = veraAppConfig.isVerbose();
	 	this.policyFile = veraAppConfig.getPolicyFile().isEmpty() ? null : new File(veraAppConfig.getPolicyFile());
	 	this.profilesWikiPath = veraAppConfig.getWikiPath();

	 	EnumSet<TaskType> taskTypes =  veraAppConfig.getProcessType().getTasks();
		this.fixMetadata = taskTypes.contains(TaskType.FIX_METADATA);

		this.features = new ArrayList<>();
		if (taskTypes.contains(TaskType.EXTRACT_FEATURES)) {
			this.features.addAll(configManager.getFeaturesConfig().getEnabledFeatures());
		}

	 	this.prefix = configManager.getFixerConfig().getFixesPrefix();
	}

	public boolean isMultiprocessing() {
	 	return this.numberOfProcesses > 1 && !this.isServerMode;
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
				if (flavourLocal.getId().equalsIgnoreCase(value)) {
					return flavourLocal;
				}
			}
			throw new ParameterException("Illegal --flavour argument:" + value);
		}

	}

	public static final class FeatureConverter implements IStringConverter<FeatureObjectType> {

		@Override
		public FeatureObjectType convert(final String value) {
			for (FeatureObjectType type : FeatureObjectType.values()) {
				if (type.getNodeName().equalsIgnoreCase(value)) {
					return type;
				}
			}
			throw new ParameterException("Illegal --extract argument:" + value);
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
		return ValidatorFactory.createConfig(this.flavour, this.defaultFlavour, this.logPassed(), this.maxFailures,
				this.debug, this.addLogs(), getLoggerLevel(), this.maxFailuresDisplayed, !isDisableErrorMessages(),
				                             this.getPassword(), this.getShowProgress(), this.nonPdfExt());
	}

	public FeatureExtractorConfig featureExtractorConfig() {
		if (this.features != null) {
			return FeatureFactory.configFromValues(this.features.isEmpty() ? EnumSet.noneOf(FeatureObjectType.class)
			                                                               : EnumSet.copyOf(this.features));
		}
		return FeatureFactory.defaultConfig();
	}

	public MetadataFixerConfig fixerConfig() {
		return FixerFactory.configFromValues(this.prefix, true);
	}

	public VeraAppConfig appConfig(final VeraAppConfig base) {
		AppConfigBuilder configBuilder = Applications.createConfigBuilder(base);
		configBuilder.format(this.getFormat()).isVerbose(this.isVerbose()).fixerFolder(this.saveFolder)
		             .wikiPath(this.getProfilesWikiPath()).policyFile(this.getPolicyFileName());
		configBuilder.type(typeFromArgs(this));
		return configBuilder.build();
	}

	public ProcessorConfig processorConfig(final ProcessType procType, PluginsCollectionConfig plugConfig)
			throws VeraPDFException {
		FeatureExtractorConfig featuresConfig = this.featureExtractorConfig();
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

		veraPDFParameters.add(SERVER_MODE);
		if (cliArgParser.extractFeatures()) {
			veraPDFParameters.add(EXTRACT_FLAG);
		}
		if (cliArgParser.fixMetadata()) {
			veraPDFParameters.add(FIX_METADATA);
		}
		if (cliArgParser.addLogs()) {
			veraPDFParameters.add(ADD_LOGS);
		}
		if (cliArgParser.isDisableErrorMessages()) {
			veraPDFParameters.add(DISABLE_ERROR_MESSAGES);
		}
		veraPDFParameters.add(LOG_LEVEL);
		veraPDFParameters.add(String.valueOf(cliArgParser.getLogLevel()));
		veraPDFParameters.add(PROFILES_WIKI);
		veraPDFParameters.add(String.valueOf(cliArgParser.getProfilesWikiPath()));
		if (cliArgParser.getPassword() != null) {
			veraPDFParameters.add(PASSWORD);
			veraPDFParameters.add(cliArgParser.getPassword());
		}
		veraPDFParameters.add(DEFAULT_FLAVOUR);
		veraPDFParameters.add(String.valueOf(cliArgParser.getDefaultFlavour()));
		veraPDFParameters.add(FLAVOUR);
		veraPDFParameters.add(String.valueOf(cliArgParser.getFlavour()));
		veraPDFParameters.add(FORMAT);
		if (cliArgParser.getFormat() == FormatOption.HTML) {
			veraPDFParameters.add(String.valueOf(FormatOption.XML));
		} else {
			veraPDFParameters.add(String.valueOf(cliArgParser.getFormat()));
		}
		if (cliArgParser.listProfiles()) {
			veraPDFParameters.add(LIST);
		}
		veraPDFParameters.add(MAX_FAILURES);
		veraPDFParameters.add(String.valueOf(cliArgParser.maxFailures()));
		veraPDFParameters.add(MAX_FAILURES_DISPLAYED);
		veraPDFParameters.add(String.valueOf(cliArgParser.maxFailuresDisplayed()));
		if (cliArgParser.isValidationOff()) {
			veraPDFParameters.add(VALID_OFF);
		}
		File policyFile = cliArgParser.getPolicyFile();
		if (policyFile != null) {
			veraPDFParameters.add(POLICY_FILE);
			veraPDFParameters.add(policyFile.getAbsolutePath());
		}
		veraPDFParameters.add(FIX_METADATA_PREFIX);
		veraPDFParameters.add(cliArgParser.prefix());
		File profileFile = cliArgParser.getProfileFile();
		if (profileFile != null) {
			veraPDFParameters.add(LOAD_PROFILE_FLAG);
			veraPDFParameters.add(profileFile.getAbsolutePath());
		}
		veraPDFParameters.add(FIX_METADATA_FOLDER);
		veraPDFParameters.add(cliArgParser.saveFolder());
		if (cliArgParser.logPassed()) {
			veraPDFParameters.add(SUCCESS);
		}
		if (cliArgParser.isVerbose()) {
			veraPDFParameters.add(VERBOSE);
		}
		if (cliArgParser.isDebug()) {
			veraPDFParameters.add(DEBUG);
		}
		if (cliArgParser.nonPdfExt()) {
			veraPDFParameters.add(NON_PDF_EXTENSION);
		}

		return veraPDFParameters;
	}

	public void checkParametersCompatibility() {
		if (this.format != FormatOption.MRR && this.format != FormatOption.XML && this.policyFile != null
		    && !this.policyFile.getAbsolutePath().equals("")) {
			LOGGER.log(Level.WARNING, "Policy report supports only xml (mrr) output format.");
			this.format = FormatOption.XML;
		}
		if (this.format != FormatOption.MRR && this.format != FormatOption.XML && this.format != FormatOption.JSON
		    && this.format != FormatOption.HTML && this.addLogs) {
			LOGGER.log(Level.WARNING, "Log messages in report are supported only in xml (mrr), json and html formats.");
		}
		if (Foundries.defaultParserIsPDFBox() && this.fixMetadata) {
			LOGGER.log(Level.WARNING, "Fixing metadata is not supported in PDFBox validator.");
			this.fixMetadata = false;
		}
		if (Foundries.defaultParserIsPDFBox() && !this.disableErrorMessages) {
			LOGGER.log(Level.WARNING, "Detailed error messages are not supported in PDFBox validator.");
			this.disableErrorMessages = true;
		}
		if (this.fixMetadata && this.maxFailures > 0) {
			LOGGER.log(Level.WARNING, "Option maxfailures is ignored when option fixmetadata is enabled");
			this.maxFailures = -1;
		}
		if (this.maxFailuresDisplayed < -1 || this.maxFailuresDisplayed == 0) {
			LOGGER.log(Level.WARNING, "Argument " + maxFailuresDisplayed + " of option maxfailuresdisplayed is not supported and changed to 1");
			this.maxFailuresDisplayed = 1;
		}
		if (Foundries.defaultParserIsPDFBox() && this.password != null) {
			LOGGER.log(Level.WARNING, "Password handling for encrypted files is not supported in PDFBox validator.");
			this.password = null;
		}
		if (getPdfPaths().size() > 1 && this.password != null) {
			LOGGER.log(Level.WARNING, "Password handling for encrypted files is not supported for batch processing.");
			this.password = null;
		}
		if (!getPdfPaths().isEmpty() && FileUtils.hasExtNoCase(getPdfPaths().get(0), GUIConstants.ZIP) && this.password != null) {
			LOGGER.log(Level.WARNING, "Password handling for encrypted files is not supported for zip processing.");
			this.password = null;
		}
		if (isMultiprocessing() && this.showProgress) {
			LOGGER.log(Level.WARNING, "Validation progress output is not supported for multiprocessing.");
		}
		for (String pdfPath : getPdfPaths()) {
			if (FileUtils.hasExtNoCase(pdfPath, GUIConstants.ZIP)) {
				if (this.fixMetadata && saveFolder().isEmpty()) {
					LOGGER.log(Level.WARNING, "Fixing metadata is not supported for zip processing, if save folder isn't defined");
					this.fixMetadata = false;
				}
				if (isMultiprocessing()) {
					LOGGER.log(Level.WARNING, "Multiprocessing is not supported for zip processing");
				}
				break;
			}
		}
	}
}
