/**
 * 
 */
package org.verapdf.apps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.verapdf.processor.FormatOption;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 30 Oct 2016:21:07:53
 */
@XmlRootElement(name = "appConfig")
final class VeraAppConfigImpl implements VeraAppConfig {
	private static final VeraAppConfig defaultInstance = Applications.Builder.defaultBuilder().build();
	@XmlAttribute
	private final ProcessType type;
	@XmlAttribute
	private final int maxFails;
	@XmlAttribute
	private final boolean isOverwrite;
	@XmlAttribute
	private final FormatOption format;
	@XmlAttribute
	private final boolean isVerbose;
	@XmlElement
	private final String fixerFolder;
	@XmlElement
	private final String wikiPath;
	@XmlElement
	private final String reportFile;
	@XmlElement
	private final String reportFolder;
	@XmlElement
	private final String policyFile;
	@XmlElement
	private final String pluginsFolder;

	@SuppressWarnings("unused")
	VeraAppConfigImpl() {
		this(defaultInstance.getProcessType(), defaultInstance.getMaxFailsDisplayed(),
				defaultInstance.isOverwriteReport(), defaultInstance.getFixesFolder(), defaultInstance.getFormat(),
				defaultInstance.isVerbose(), defaultInstance.getWikiPath(), defaultInstance.getReportFile(),
				defaultInstance.getReportFolder(), defaultInstance.getPolicyFile(), defaultInstance.getPluginsFolder());
	}

	VeraAppConfigImpl(final ProcessType type, final int maxFails, final boolean isOverwrite, final String fixerFolder,
			final FormatOption format, final boolean isVerbose, final String wikiPath, final String reportFile,
					  final String reportFolder, final String policyFile, final String pluginsFolder) {
		super();
		this.type = type;
		this.maxFails = maxFails;
		this.isOverwrite = isOverwrite;
		this.format = format;
		this.isVerbose = isVerbose;
		this.wikiPath = wikiPath;
		this.fixerFolder = fixerFolder;
		this.reportFile = reportFile;
		this.reportFolder = reportFolder;
		this.policyFile = policyFile;
		this.pluginsFolder = pluginsFolder;
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#isOverwriteReport()
	 */
	@Override
	public boolean isOverwriteReport() {
		return this.isOverwrite;
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#getFixerFolder()
	 */
	@Override
	public String getFixesFolder() {
		return this.fixerFolder;
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#getPluginsFolder()
	 */
	@Override
	public String getPluginsFolder() {
		return this.pluginsFolder;
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#getReportFile()
	 */
	@Override
	public String getReportFile() {
		return this.reportFile;
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#getPolicyFile()
	 */
	@Override
	public String getPolicyFile() {
		return this.policyFile;
	}

	@Override
	public ProcessType getProcessType() {
		return this.type;
	}

	@Override
	public String getReportFolder() {
		return this.reportFolder;
	}

	@Override
	public String getWikiPath() {
		return this.wikiPath;
	}

	@Override
	public FormatOption getFormat() {
		return this.format;
	}

	@Override
	public boolean isVerbose() {
		return this.isVerbose;
	}

	@Override
	public int getMaxFailsDisplayed() {
		return this.maxFails;
	}

	public static VeraAppConfig defaultInstance() {
		return defaultInstance;
	}

	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + maxFails;
		result = 31 * result + (isOverwrite ? 1 : 0);
		result = 31 * result + (format != null ? format.hashCode() : 0);
		result = 31 * result + (isVerbose ? 1 : 0);
		result = 31 * result + (fixerFolder != null ? fixerFolder.hashCode() : 0);
		result = 31 * result + (wikiPath != null ? wikiPath.hashCode() : 0);
		result = 31 * result + (reportFile != null ? reportFile.hashCode() : 0);
		result = 31 * result + (reportFolder != null ? reportFolder.hashCode() : 0);
		result = 31 * result + (policyFile != null ? policyFile.hashCode() : 0);
		result = 31 * result + (pluginsFolder != null ? pluginsFolder.hashCode() : 0);
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VeraAppConfigImpl)) {
			return false;
		}
		VeraAppConfigImpl other = (VeraAppConfigImpl) obj;
		if (this.fixerFolder == null) {
			if (other.fixerFolder != null) {
				return false;
			}
		} else if (!this.fixerFolder.equals(other.fixerFolder)) {
			return false;
		}
		if (this.format != other.format) {
			return false;
		}
		if (this.isVerbose != other.isVerbose) {
			return false;
		}
		if (this.isOverwrite != other.isOverwrite) {
			return false;
		}
		if (this.maxFails != other.maxFails) {
			return false;
		}
		if (this.pluginsFolder == null) {
			if (other.pluginsFolder != null) {
				return false;
			}
		} else if (!this.pluginsFolder.equals(other.pluginsFolder)) {
			return false;
		}
		if (this.policyFile == null) {
			if (other.policyFile != null) {
				return false;
			}
		} else if (!this.policyFile.equals(other.policyFile)) {
			return false;
		}
		if (this.reportFile == null) {
			if (other.reportFile != null) {
				return false;
			}
		} else if (!this.reportFile.equals(other.reportFile)) {
			return false;
		}
		if (this.reportFolder == null) {
			if (other.reportFolder != null) {
				return false;
			}
		} else if (!this.reportFolder.equals(other.reportFolder)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		if (this.wikiPath == null) {
			if (other.wikiPath != null) {
				return false;
			}
		} else if (!this.wikiPath.equals(other.wikiPath)) {
			return false;
		}
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VeraAppConfigImpl [type=" + this.type + ", maxFails=" + this.maxFails + ", isOverwrite="
				+ this.isOverwrite + ", format=" + this.format + ", isVerbose=" + this.isVerbose + ", fixerFolder="
				+ this.fixerFolder + ", wikiPath=" + this.wikiPath + ", reportFile=" + this.reportFile + ", reportFolder="
				+ this.reportFolder + ", policyFile=" + this.policyFile + ", pluginsFolder=" + this.pluginsFolder + "]";
	}

	static String toXml(final VeraAppConfig toConvert, Boolean prettyXml) throws JAXBException, IOException {
		String retVal = "";
		try (StringWriter writer = new StringWriter()) {
			toXml(toConvert, writer, prettyXml);
			retVal = writer.toString();
			return retVal;
		}
	}

	static void toXml(final VeraAppConfig toConvert, final OutputStream stream, Boolean prettyXml)
			throws JAXBException {
		Marshaller varMarshaller = getMarshaller(prettyXml);
		varMarshaller.marshal(toConvert, stream);
	}

	static VeraAppConfigImpl fromXml(final InputStream toConvert) throws JAXBException {
		Unmarshaller stringUnmarshaller = getUnmarshaller();
		return (VeraAppConfigImpl) stringUnmarshaller.unmarshal(toConvert);
	}

	static void toXml(final VeraAppConfig toConvert, final Writer writer, Boolean prettyXml) throws JAXBException {
		Marshaller varMarshaller = getMarshaller(prettyXml);
		varMarshaller.marshal(toConvert, writer);
	}

	static VeraAppConfigImpl fromXml(final Reader toConvert) throws JAXBException {
		Unmarshaller stringUnmarshaller = getUnmarshaller();
		return (VeraAppConfigImpl) stringUnmarshaller.unmarshal(toConvert);
	}

	static VeraAppConfigImpl fromXml(final String toConvert) throws JAXBException {
		try (StringReader reader = new StringReader(toConvert)) {
			return fromXml(reader);
		}
	}

	static class Adapter extends XmlAdapter<VeraAppConfigImpl, VeraAppConfig> {
		@Override
		public VeraAppConfig unmarshal(VeraAppConfigImpl validationConfigImpl) {
			return validationConfigImpl;
		}

		@Override
		public VeraAppConfigImpl marshal(VeraAppConfig validationResult) {
			return (VeraAppConfigImpl) validationResult;
		}
	}

	private static Unmarshaller getUnmarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(VeraAppConfigImpl.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return unmarshaller;
	}

	private static Marshaller getMarshaller(Boolean setPretty) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(VeraAppConfigImpl.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, setPretty);
		return marshaller;
	}

}
