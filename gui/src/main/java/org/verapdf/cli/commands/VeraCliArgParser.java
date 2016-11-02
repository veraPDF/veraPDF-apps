package org.verapdf.cli.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.verapdf.apps.Applications;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * This class holds all command-line options used by VeraPDF application.
 *
 * @author Timur Kamalov
 */
public class VeraCliArgParser {
	final static VeraCliArgParser DEFAULT_ARGS = new VeraCliArgParser();
	final static String FLAG_SEP = "-";
	final static String OPTION_SEP = "--";
	final static String HELP_FLAG = FLAG_SEP + "h";
	final static String HELP = OPTION_SEP + "help";
	final static String VERSION = OPTION_SEP + "version";
	final static String FLAVOUR_FLAG = FLAG_SEP + "f";
	final static String FLAVOUR = OPTION_SEP + "flavour";
	final static String SUCCESS = OPTION_SEP + "success";
	final static String PASSED = OPTION_SEP + "passed";
	final static String LIST_FLAG = FLAG_SEP + "l";
	final static String LIST = OPTION_SEP + "list";
	final static String LOAD_PROFILE_FLAG = FLAG_SEP + "p";
	final static String LOAD_PROFILE = OPTION_SEP + "profile";
	final static String EXTRACT_FLAG = FLAG_SEP + "x";
	final static String EXTRACT = OPTION_SEP + "extract";
	final static String FORMAT = OPTION_SEP + "format";
	final static String RECURSE_FLAG = FLAG_SEP + "r";
	final static String RECURSE = OPTION_SEP + "recurse";
	final static String VERBOSE_FLAG = FLAG_SEP + "v";
	final static String VERBOSE = OPTION_SEP + "verbose";
	final static String MAX_FAILURES_DISPLAYED = OPTION_SEP + "maxfailuresdisplayed";
	final static String MAX_FAILURES = OPTION_SEP + "maxfailures";
	final static String FIX_METADATA = OPTION_SEP + "fixmetadata";
	final static String FIX_METADATA_PREFIX = OPTION_SEP + "prefix";
	final static String FIX_METADATA_FOLDER = OPTION_SEP + "savefolder";
	final static String PROFILES_WIKI_FLAG = FLAG_SEP + "pw";
	final static String LOAD_CONFIG_FLAG = FLAG_SEP + "c";
	final static String LOAD_CONFIG = OPTION_SEP + "config";
	final static String PROFILES_WIKI = OPTION_SEP + "profilesWiki";
	final static String POLICY_PROFILE = OPTION_SEP + "policyProfile";
	final static String REPORT_FILE = OPTION_SEP + "reportfile";
	final static String REPORT_FOLDER = OPTION_SEP + "reportfolder";
	final static String OVERWRITE_REPORT_FILE = OPTION_SEP + "overwriteReportFile";
	final static String VALID_OFF_FLAG = FLAG_SEP + "o";
	final static String VALID_OFF = OPTION_SEP + "off";

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
			LOAD_PROFILE }, description = "Loads a Validation Profile from given path and exits if loading fails. This overrides any choice or default implied by the -f / --flavour option.", validateWith = ProfileFileValidator.class)
	private File profileFile;

	@Parameter(names = { EXTRACT_FLAG, EXTRACT }, description = "Extracts and reports PDF features.")
	private boolean features = false;

	@Parameter(names = { FORMAT }, description = "Chooses output format.", converter = FormatConverter.class)
	private FormatOption format = Applications.defaultConfig().getFormat();

	@Parameter(names = { RECURSE_FLAG,
			RECURSE }, description = "Recurses through directories. Only files with .pdf extensions are processed.")
	private boolean isRecurse = false;

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
	private String saveFolder = "";

	@Parameter(names = { PROFILES_WIKI_FLAG,
			PROFILES_WIKI }, description = "Sets location of the Validation Profiles wiki.")
	private String profilesWikiPath = Applications.defaultConfig().getWikiPath();

	@Parameter(names = {
			POLICY_PROFILE }, description = "Uses policy check output with specified Policy Profile. Output format option will be ignored.")
	private String policyProfilePath = "";

	@Parameter(names = {
			REPORT_FOLDER }, description = "Sets output directory for any reports. If a directory hierarchy is being recursed, a duplicate hierarchy will be produced.")
	private String reportFolder = "";

	@Parameter(names = { REPORT_FILE }, description = "Sets output file for any reports.")
	private String reportFile = "";

	@Parameter(names = { OVERWRITE_REPORT_FILE }, description = "Overwrites report file.")
	private boolean isOverwriteReportFile = false;

	@Parameter(names = { VALID_OFF_FLAG, VALID_OFF }, description = "Turns off PDF/A validation")
	private boolean isValidationOff = false;

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

	/**
	 * @return the policy profile path
	 */
	public String policyProfilePath() {
		return this.policyProfilePath;
	}

	/**
	 * @return true if to recursively process sub-dirs
	 */
	public boolean isRecurse() {
		return this.isRecurse;
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
		return this.features;
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

	/**
	 * @author: mancuska@digitaldocuments.org
	 * @return folder for reports
	 */
	public String getReportFolder() {
		return this.reportFolder;
	}

	/**
	 * @author: mancuska@digitaldocuments.org
	 * @return output file for report
	 */
	public String getReportFile() {
		return this.reportFile;
	}

	/**
	 * @author: mancuska@digitaldocuments.org
	 * @return true if existing result file must be overwritten
	 */
	public boolean isOverwriteReportFile() {
		return this.isOverwriteReportFile;
	}

	public boolean isValidationOff() {
		return this.isValidationOff;
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
	public static final class ProfileFileValidator implements IParameterValidator {
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
		return FixerFactory.fromValues(this.prefix, true);
	}

	public VeraAppConfig appConfig(final VeraAppConfig base) {
		Applications.Builder configBuilder = Applications.Builder.fromConfig(base);
		configBuilder.policyFile(this.policyProfilePath()).wikiPath(this.getProfilesWikiPath()).format(this.getFormat())
				.reportFolder(this.getReportFolder()).reportFile(this.getReportFile())
				.overwrite(this.isOverwriteReportFile());
		configBuilder.type(typeFromArgs(this));
		return configBuilder.build();
	}

	public ProcessorConfig processorConfig(final ProcessType procType, FeatureExtractorConfig featConfig) {
		return ProcessorFactory.fromValues(this.validatorConfig(), featConfig, this.fixerConfig(),
				procType.getTasks());
	}

	private static ProcessType typeFromArgs(VeraCliArgParser parser) {
		ProcessType retVal = ProcessType.VALIDATE;
		if (parser.isValidationOff())
			retVal = ProcessType.NO_PROCESS;
		if (parser.extractFeatures())
			retVal = ProcessType.addProcess(retVal, ProcessType.EXTRACT);
		if (parser.fixMetadata())
			retVal = ProcessType.addProcess(retVal, ProcessType.FIX);
		return retVal;
	}
}
