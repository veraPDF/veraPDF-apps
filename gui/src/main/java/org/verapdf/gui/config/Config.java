package org.verapdf.gui.config;

import org.apache.log4j.Logger;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.gui.tools.ProcessingType;
import org.verapdf.metadata.fixer.utils.MetadataFixerConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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

	private static final Logger LOGGER = Logger.getLogger(ProcessingTypeAdapter.class);
	@Override
	public ProcessingType unmarshal(String v) throws Exception {
		try {
			return ProcessingType.fromString(v);
		}
		catch(IllegalArgumentException e) {
			LOGGER.error("Can't construct ProcessingType from string \"" + v + "\", setting ProcessingType to default", e);
		}
        return Config.getDefaultProcessingType();
    }

	@Override
	public String marshal(ProcessingType v) throws Exception {
		return v.toString();
	}
}

@XmlRootElement(name = "config")
public final class Config {

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


	private boolean showPassedRules;
	private int maxNumberOfFailedChecks;
	private int maxNumberOfDisplayedFailedChecks;
	private String metadataFixerPrefix;
	private Path fixMetadataPathFolder;
	private String profileWikiPath;
	private ProcessingType processingType;
	private boolean isFixMetadata;
	//private final ValidationProfile profile;

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

	public Config() {
		this.showPassedRules = DEFAULT_SHOW_PASSED_RULES;
		this.maxNumberOfFailedChecks = DEFAULT_MAX_NUMBER_OF_FAILED_CHECKS;
		this.maxNumberOfDisplayedFailedChecks = DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS;
		this.metadataFixerPrefix = DEFAULT_METADATA_FIXER_PREFIX;
		this.fixMetadataPathFolder = DEFAULT_FIX_METADATA_PATH_FOLDER;
		this.profileWikiPath = DEFAULT_PROFILES_WIKI_PATH;
		this.isFixMetadata = DEFAULT_IS_FIX_METADATA;
		this.processingType = DEFAULT_PROCESSING_TYPE;
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

    public static Config buildDefaultConfig() {
        return DEFAULT_CONFIG;
    }

    public static ProcessingType getDefaultProcessingType() {
        return DEFAULT_PROCESSING_TYPE;
    }

	/**
	 * @return selected number for maximum displayed fail checks for a rule. If not selected returns -1
	 */
    @XmlElement
	public int getMaxNumberOfDisplayedFailedChecks() {
		return maxNumberOfDisplayedFailedChecks;
	}

	/**
	 * @return selected number for maximum fail checks for a rule. If not selected returns -1
	 */
    @XmlElement
	public int getMaxNumberOfFailedChecks() {
		return maxNumberOfFailedChecks;
	}

	/**
	 * @return true if desplay passed pules option selected
	 */
    @XmlElement
	public boolean isShowPassedRules() {
		return showPassedRules;
	}

	/**
	 * @return String representation of prefix for fixed files
	 */
    @XmlElement
	public String getMetadataFixerPrefix() {
		return metadataFixerPrefix;
	}

	/**
	 * @return path to the folder in which fixed file will be placed
	 */
    @XmlElement
    @XmlJavaTypeAdapter(PathAdapter.class)
	public Path getFixMetadataPathFolder() {
		return fixMetadataPathFolder;
	}

	/**
	 * @return type of operation to be performed, e. g. validation & feature describing
	 */
    @XmlElement
    @XmlJavaTypeAdapter(ProcessingTypeAdapter.class)
	public ProcessingType getProcessingType() { return processingType; }

	/**
	 * @return true if metadata fixes are to be performed
	 */
    @XmlElement
	public boolean isFixMetadata() { return isFixMetadata; }

    /**
     * @return path to the profiles wiki
     */
    @XmlElement
    public String getProfileWikiPath() {
        return profileWikiPath;
    }

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

	public static Config fromXml(final InputStream toConvert)
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
     * Changes settings parameters
     *
     * @param metadataFixerPrefix a prefix which will be added to the fixed file
     * @throws IllegalArgumentException parameter can not be null
     */
    public void setMetadataFixerPrefix(String metadataFixerPrefix) {
        if (metadataFixerPrefix == null) {
            throw new IllegalArgumentException("Prefix for metadata fixer can not be null");
        }
        for (char c : metadataFixerPrefix.toCharArray()) {
            if (!isValidFileNameCharacter(c)) {
                throw new IllegalArgumentException("Prefix for metadata fixer contains forbidden symbols");
            }
        }
        this.metadataFixerPrefix = metadataFixerPrefix;
    }

    /**
     * Changes settings parameter
     *
     * @param showPassedRules true for show passed rules at the report
     */
    public void setShowPassedRules(boolean showPassedRules) {
        this.showPassedRules = showPassedRules;
    }

    /**
     * Changes settings parameter
     *
     * @param maxNumberOfFailedChecks a natural number that indicates maximum number of failed checks for rule or -1 for unlimited
     * @throws IllegalArgumentException if parameter is not a natural number or -1
     */
    public void setMaxNumberOfFailedChecks(int maxNumberOfFailedChecks) {
        if (maxNumberOfFailedChecks > 0 || maxNumberOfFailedChecks == -1) {
            this.maxNumberOfFailedChecks = maxNumberOfFailedChecks;
        } else {
            throw new IllegalArgumentException("Max number of failed checks for rule for setter method is not a natural or -1");
        }
    }

    /**
     * Changes settings parameter
     *
     * @param maxNumberOfDisplayedFailedChecks a non negative integer number that indicates maximum number of displayed
     *                                         failed checks for rule or -1 for infinite
     * @throws IllegalArgumentException if parameter is less than -1
     */
    public void setMaxNumberOfDisplayedFailedChecks(int maxNumberOfDisplayedFailedChecks) {
        if (maxNumberOfDisplayedFailedChecks >= -1) {
            this.maxNumberOfDisplayedFailedChecks = maxNumberOfDisplayedFailedChecks;
        } else {
            throw new IllegalArgumentException("Max number of displayed failed checks for rule for setter method is less than -1");
        }
    }

    /**
     * Changes settings parameters
     *
     * @param fixMetadataPathFolder a path to the folder in which fixed files will be saved
     * @throws IllegalArgumentException parameter should be an empty path or a path to an existing and write acceptable directory
     */
    public void setFixMetadataPathFolder(Path fixMetadataPathFolder) {
        if (isValidFolderPath(fixMetadataPathFolder)) {
            this.fixMetadataPathFolder = fixMetadataPathFolder;
        } else {
            throw new IllegalArgumentException("Path should be an empty path or a path to an existing and write acceptable directory");
        }
    }

    public void setProfileWikiPath(String profileWikiPath) {
        this.profileWikiPath = profileWikiPath;
    }

    public void setFixMetadata(boolean fixMetadata) {
        isFixMetadata = fixMetadata;
    }

    public void setProcessingType(ProcessingType processingType) {   // Should we check validity?
        this.processingType = processingType;
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