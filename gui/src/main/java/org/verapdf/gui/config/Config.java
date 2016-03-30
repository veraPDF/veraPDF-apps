package org.verapdf.gui.config;

import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.gui.tools.ProcessingType;
import org.verapdf.metadata.fixer.utils.MetadataFixerConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author Maksim Bezrukov
 */
class PathAdapter extends XmlAdapter<String, Path> {    //  TODO: Move adapters somewhere. Or leave here.

	@Override
	public Path unmarshal(String v) throws Exception {
		Path path = FileSystems.getDefault().getPath(v);
		return path;
	}

	@Override
	public String marshal(Path v) throws Exception {
		return v.toString();
	}
}

class ProcessingTypeAdapter extends XmlAdapter<String, ProcessingType> {

	@Override
	public ProcessingType unmarshal(String v) throws Exception {
		if(v.equalsIgnoreCase(GUIConstants.VALIDATING_AND_FEATURES))    //  Is it OK to check all one by one?
			return ProcessingType.VALIDATING_AND_FEATURES;
		if(v.equalsIgnoreCase(GUIConstants.VALIDATING))
			return ProcessingType.VALIDATING;
		if(v.equalsIgnoreCase(GUIConstants.FEATURES))
			return ProcessingType.FEATURES;

		//  TODO: throw some exception like "ProcessingType not found"
		return null;
	}

	@Override
	public String marshal(ProcessingType v) throws Exception {
		return v.toString();
	}
}

@XmlRootElement(name = "config")
public final class Config {

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Config config = (Config) o;

		if (showPassedRules != config.showPassedRules) return false;
		if (maxNumberOfFailedChecks != config.maxNumberOfFailedChecks)
			return false;
		if (maxNumberOfDisplayedFailedChecks != config.maxNumberOfDisplayedFailedChecks)
			return false;
		if (isFixMetadata != config.isFixMetadata) return false;
		if (metadataFixerPrefix != null ? !metadataFixerPrefix.equals(config.metadataFixerPrefix) : config.metadataFixerPrefix != null)
			return false;
		if (fixMetadataPathFolder != null ? !fixMetadataPathFolder.equals(config.fixMetadataPathFolder) : config.fixMetadataPathFolder != null)
			return false;
		if (profileWikiPath != null ? !profileWikiPath.equals(config.profileWikiPath) : config.profileWikiPath != null)
			return false;
		return processingType == config.processingType;

	}

	@Override
	public int hashCode() {
		int result = (showPassedRules ? 1 : 0);
		result = 31 * result + maxNumberOfFailedChecks;
		result = 31 * result + maxNumberOfDisplayedFailedChecks;
		result = 31 * result + (metadataFixerPrefix != null ? metadataFixerPrefix.hashCode() : 0);
		result = 31 * result + (fixMetadataPathFolder != null ? fixMetadataPathFolder.hashCode() : 0);
		result = 31 * result + (profileWikiPath != null ? profileWikiPath.hashCode() : 0);
		result = 31 * result + (processingType != null ? processingType.hashCode() : 0);
		result = 31 * result + (isFixMetadata ? 1 : 0);
		return result;
	}

	@XmlElement
	private final boolean showPassedRules;
	@XmlElement
	private final int maxNumberOfFailedChecks;
	@XmlElement
	private final int maxNumberOfDisplayedFailedChecks;
	@XmlElement
	private final String metadataFixerPrefix;
	@XmlElement
	@XmlJavaTypeAdapter(PathAdapter.class)
	private final Path fixMetadataPathFolder;
	@XmlElement
	private final String profileWikiPath;
	@XmlElement
	@XmlJavaTypeAdapter(ProcessingTypeAdapter.class)
	private final ProcessingType processingType;
	@XmlElement
	private final boolean isFixMetadata;
	//private final ValidationProfile profile;


	private Config() {
		Config config = Builder.buildDefaultConfig();
		this.showPassedRules = config.showPassedRules;
		this.maxNumberOfFailedChecks = config.maxNumberOfFailedChecks;
		this.maxNumberOfDisplayedFailedChecks = config.maxNumberOfDisplayedFailedChecks;
		this.metadataFixerPrefix = config.metadataFixerPrefix;
		this.fixMetadataPathFolder = config.fixMetadataPathFolder;
		this.profileWikiPath = config.profileWikiPath;
		this.isFixMetadata = config.isFixMetadata;
		this.processingType = config.processingType;
	}

	private Config(boolean showPassedRules, int maxNumberOfFailedChecks,
				   int maxNumberOfDisplayedFailedChecks, String metadataFixerPrefix,
				   Path fixMetadataPathFolder, String profileWikiPath,
				   boolean isFixMetadata, ProcessingType processingType) {
		this.showPassedRules = showPassedRules;
		this.maxNumberOfFailedChecks = maxNumberOfFailedChecks;
		this.maxNumberOfDisplayedFailedChecks = maxNumberOfDisplayedFailedChecks;
		this.metadataFixerPrefix = metadataFixerPrefix;
		this.fixMetadataPathFolder = fixMetadataPathFolder;
		this.profileWikiPath = profileWikiPath;
		this.isFixMetadata = isFixMetadata;
		this.processingType = processingType;
	}

	/**
	 * @return selected number for maximum displayed fail checks for a rule. If not selected returns -1
	 */
	public int getMaxNumberOfDisplayedFailedChecks() {
		return maxNumberOfDisplayedFailedChecks;
	}

	/**
	 * @return selected number for maximum fail checks for a rule. If not selected returns -1
	 */
	public int getMaxNumberOfFailedChecks() {
		return maxNumberOfFailedChecks;
	}

	/**
	 * @return true if desplay passed pules option selected
	 */
	public boolean isShowPassedRules() {
		return showPassedRules;
	}

	/**
	 * @return String representation of prefix for fixed files
	 */
	public String getMetadataFixerPrefix() {
		return metadataFixerPrefix;
	}

	/**
	 * @return path to the folder in which fixed file will be placed
	 */
	public Path getFixMetadataPathFolder() {
		return fixMetadataPathFolder;
	}

	/**
	 * @return type of operation to be performed, e. g. validation & feature describing
	 */
	public ProcessingType getProcessingType() { return processingType; }

	/**
	 * @return true if metadata fixes are to be performed
	 */
	public boolean isFixMetadata() { return isFixMetadata; }

	public static String toXml(final Config toConvert, Boolean prettyXml)
			throws JAXBException, IOException {
		String retVal = "";
		try (StringWriter writer = new StringWriter()) {
			toXml(toConvert, writer, prettyXml);
			retVal = writer.toString();
			return retVal;
		}
	}

	public static Config fromXml(final String toConvert)
			throws JAXBException {
		try (StringReader reader = new StringReader(toConvert)) {
			return fromXml(reader);
		}
	}

	static void toXml(final Config toConvert,
					  final OutputStream stream, Boolean prettyXml) throws JAXBException {
		Marshaller varMarshaller = getMarshaller(prettyXml);
		varMarshaller.marshal(toConvert, stream);
	}

	static Config fromXml(final InputStream toConvert)
			throws JAXBException {
		Unmarshaller stringUnmarshaller = getUnmarshaller();
		return (Config) stringUnmarshaller.unmarshal(toConvert);
	}

	static void toXml(final Config toConvert, final Writer writer,
					  Boolean prettyXml) throws JAXBException {
		Marshaller varMarshaller = getMarshaller(prettyXml);
		varMarshaller.marshal(toConvert, writer);
	}

	static Config fromXml(final Reader toConvert)
			throws JAXBException {
		Unmarshaller stringUnmarshaller = getUnmarshaller();
		return (Config) stringUnmarshaller.unmarshal(toConvert);
	}

	private static Unmarshaller getUnmarshaller() throws JAXBException {
		JAXBContext context = JAXBContext
				.newInstance(Config.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return unmarshaller;
	}

	private static Marshaller getMarshaller(Boolean setPretty)
			throws JAXBException {
		JAXBContext context = JAXBContext
				.newInstance(Config.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, setPretty);
		return marshaller;
	}

	/**
	 * @return path to the profiles wiki
	 */
	public String getProfileWikiPath() {
		return profileWikiPath;
	}

	public static final class Builder {

		private static final char[] FORBIDDEN_SYMBOLS_IN_FILE_NAME = new char[]{'\\', '/', ':', '*', '?', '\"', '<', '>', '|', '+', '\0', '%'};

		private static final boolean DEFAULT_SHOW_PASSED_RULES = false;
		private static final int DEFAULT_MAX_NUMBER_OF_FAILED_CHECKS = -1;
		private static final int DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS = 100;
		private static final String DEFAULT_METADATA_FIXER_PREFIX = MetadataFixerConstants.DEFAULT_PREFIX;
		private static final Path DEFAULT_FIX_METADATA_PATH_FOLDER = FileSystems.getDefault().getPath("");
		private static final String DEFAULT_PROFILES_WIKI_PATH = "https://github.com/veraPDF/veraPDF-validation-profiles/wiki";
		private static final boolean DEFAULT_IS_FIX_METADATA = true;
		private static final ProcessingType DEFAULT_PROCESSING_TYPE = ProcessingType.VALIDATING_AND_FEATURES;

		private static final Config DEFAULT_CONFIG = new Config(DEFAULT_SHOW_PASSED_RULES,
				DEFAULT_MAX_NUMBER_OF_FAILED_CHECKS, DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS,
				DEFAULT_METADATA_FIXER_PREFIX, DEFAULT_FIX_METADATA_PATH_FOLDER,
				DEFAULT_PROFILES_WIKI_PATH, DEFAULT_IS_FIX_METADATA,
				DEFAULT_PROCESSING_TYPE);

		private boolean showPassedRules = DEFAULT_SHOW_PASSED_RULES;
		private int maxNumberOfFailedChecks = DEFAULT_MAX_NUMBER_OF_FAILED_CHECKS;
		private int maxNumberOfDisplayedFailedChecks = DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS;
		private String metadataFixerPrefix = DEFAULT_METADATA_FIXER_PREFIX;
		private Path fixMetadataPathFolder = DEFAULT_FIX_METADATA_PATH_FOLDER;
		private String profilesWikiPath = DEFAULT_PROFILES_WIKI_PATH;
		public boolean isFixMetadata = DEFAULT_IS_FIX_METADATA;
		private ProcessingType processingType = DEFAULT_PROCESSING_TYPE;

		public Builder() {
		}

		public Config build() {
			return new Config(this.showPassedRules, this.maxNumberOfFailedChecks,
					this.maxNumberOfDisplayedFailedChecks, this.metadataFixerPrefix,
					this.fixMetadataPathFolder, this.profilesWikiPath,
					this.isFixMetadata, this.processingType);
		}

		public static Config buildDefaultConfig() {
			return DEFAULT_CONFIG;
		}

		/**
		 * Changes settings parameters
		 *
		 * @param metadataFixerPrefix a prefix which will be added to the fixed file
		 * @throws IllegalArgumentException parameter can not be null
		 */
		public Builder metadataFixerPrefix(String metadataFixerPrefix) {
			if (metadataFixerPrefix == null) {
				throw new IllegalArgumentException("Prefix for metadata fixer can not be null");
			}
			for (char c : metadataFixerPrefix.toCharArray()) {
				if (!isValidFileNameCharacter(c)) {
					throw new IllegalArgumentException("Prefix for metadata fixer contains forbidden symbols");
				}
			}
			this.metadataFixerPrefix = metadataFixerPrefix;
			return this;
		}

		/**
		 * Changes settings parameter
		 *
		 * @param showPassedRules true for show passed rules at the report
		 */
		public Builder showPassedRules(boolean showPassedRules) {
			this.showPassedRules = showPassedRules;
			return this;
		}

		/**
		 * Changes settings parameter
		 *
		 * @param maxNumberOfFailedChecks a natural number that indicates maximum number of failed checks for rule or -1 for unlimited
		 * @throws IllegalArgumentException if parameter is not a natural number or -1
		 */
		public Builder maxNumberOfFailedChecks(int maxNumberOfFailedChecks) {
			if (maxNumberOfFailedChecks > 0 || maxNumberOfFailedChecks == -1) {
				this.maxNumberOfFailedChecks = maxNumberOfFailedChecks;
			} else {
				throw new IllegalArgumentException("Max number of failed checks for rule for setter method is not a natural or -1");
			}
			return this;
		}

		/**
		 * Changes settings parameter
		 *
		 * @param maxNumberOfDisplayedFailedChecks a non negative integer number that indicates maximum number of displayed
		 *                                         failed checks for rule or -1 for infinite
		 * @throws IllegalArgumentException if parameter is less than -1
		 */
		public Builder maxNumberOfDisplayedFailedChecks(int maxNumberOfDisplayedFailedChecks) {
			if (maxNumberOfDisplayedFailedChecks >= -1) {
				this.maxNumberOfDisplayedFailedChecks = maxNumberOfDisplayedFailedChecks;
			} else {
				throw new IllegalArgumentException("Max number of displayed failed checks for rule for setter method is less than -1");
			}
			return this;
		}

		/**
		 * Changes settings parameters
		 *
		 * @param fixMetadataPathFolder a path to the folder in which fixed files will be saved
		 * @throws IllegalArgumentException parameter should be an empty path or a path to an existing and write acceptable directory
		 */
		public Builder fixMetadataPathFolder(Path fixMetadataPathFolder) {
			if (isValidFolderPath(fixMetadataPathFolder)) {
				this.fixMetadataPathFolder = fixMetadataPathFolder;
				return this;
			} else {
				throw new IllegalArgumentException("Path should be an empty path or a path to an existing and write acceptable directory");
			}
		}

		public Builder profilesWikiPath(String profilesWikiPath) {
			this.profilesWikiPath = profilesWikiPath;
			return this;
		}

        public Builder isFixMetadata(boolean isFixMetadata) {
            this.isFixMetadata = isFixMetadata;
            return this;
        }

        public Builder processingType(ProcessingType processingType) {   // Should we check validity?
            this.processingType = processingType;
            return this;
        }

		/**
		 * Checks is the parameter path a valid for saving fixed file
		 *
		 * @param path path for check
		 * @return true if it is valid
		 */
		public static boolean isValidFolderPath(Path path) {
			if (path == null) {
				return false;
			}
			File f = path.toFile();
			return path.toString().isEmpty() || (f.isDirectory() && f.canWrite());
		}

		/**
		 * Checks is the character valid for file name
		 *
		 * @param c character to be checked
		 * @return true if it is valid
		 */
		public static boolean isValidFileNameCharacter(char c) {
			for (char ch : FORBIDDEN_SYMBOLS_IN_FILE_NAME) {
				if (ch == c) {
					return false;
				}
			}
			return true;
		}
	}
}