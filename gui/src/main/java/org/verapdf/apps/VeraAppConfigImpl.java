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
package org.verapdf.apps;

import org.verapdf.processor.FormatOption;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.*;

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

	VeraAppConfigImpl() {
		this(defaultInstance.getProcessType(), defaultInstance.getMaxFailsDisplayed(),
				defaultInstance.isOverwriteReport(), defaultInstance.getFixesFolder(), defaultInstance.getFormat(),
				defaultInstance.isVerbose(), defaultInstance.getWikiPath(), defaultInstance.getReportFile(),
				defaultInstance.getReportFolder(), defaultInstance.getPolicyFile());
	}

	VeraAppConfigImpl(final ProcessType type, final int maxFails, final boolean isOverwrite, final String fixerFolder,
			final FormatOption format, final boolean isVerbose, final String wikiPath, final String reportFile,
					  final String reportFolder, final String policyFile) {
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
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#isOverwriteReport()
	 */
	@Override
	public boolean isOverwriteReport() {
		return this.isOverwrite;
	}

	/**
	 * @see org.verapdf.apps.VeraAppConfig#getFixesFolder()
	 */
	@Override
	public String getFixesFolder() {
		return this.fixerFolder;
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
		int result = this.type != null ? this.type.hashCode() : 0;
		result = 31 * result + this.maxFails;
		result = 31 * result + (this.isOverwrite ? 1 : 0);
		result = 31 * result + (this.format != null ? this.format.hashCode() : 0);
		result = 31 * result + (this.isVerbose ? 1 : 0);
		result = 31 * result + (this.fixerFolder != null ? this.fixerFolder.hashCode() : 0);
		result = 31 * result + (this.wikiPath != null ? this.wikiPath.hashCode() : 0);
		result = 31 * result + (this.reportFile != null ? this.reportFile.hashCode() : 0);
		result = 31 * result + (this.reportFolder != null ? this.reportFolder.hashCode() : 0);
		result = 31 * result + (this.policyFile != null ? this.policyFile.hashCode() : 0);
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
		return "VeraAppConfigImpl [type=" + this.type + ", maxFails=" + this.maxFails + ", isOverwrite=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ this.isOverwrite + ", format=" + this.format + ", isVerbose=" + this.isVerbose + ", fixerFolder=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ this.fixerFolder + ", wikiPath=" + this.wikiPath + ", reportFile=" + this.reportFile + ", reportFolder=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ this.reportFolder + ", policyFile=" + this.policyFile + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	static String toXml(final VeraAppConfig toConvert, Boolean prettyXml) throws JAXBException, IOException {
		String retVal = ""; //$NON-NLS-1$
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
