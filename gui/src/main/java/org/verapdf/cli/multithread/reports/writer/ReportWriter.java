package org.verapdf.cli.multithread.reports.writer;

import org.verapdf.cli.multithread.BaseCliRunner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ReportWriter {
	private static final Logger LOGGER = Logger.getLogger(ReportWriter.class.getCanonicalName());

	protected OutputStream os;

	protected OutputStream errorStream;

	protected ReportWriter(OutputStream os, OutputStream errorStream) {
		this.os = os;
		this.errorStream = errorStream;
	}

	public abstract void write(BaseCliRunner.ResultStructure result);

	public abstract void startDocument();

	public abstract void endDocument();

	protected void merge(File report, OutputStream destination) {
		try (FileReader fis = new FileReader(report)) {
			int read;
			while ((read = fis.read()) != -1) {
				destination.write(read);
			}
			destination.flush();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't read from report file", e);
		}
	}

	protected void deleteTemp(BaseCliRunner.ResultStructure result) {
		deleteFile(result.getReportFile());
		deleteFile(result.getLogFile());
	}

	private void deleteFile(File file) {
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}

	public void closeOutputStream() {
		try {
			os.flush();
			os.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't close output stream", e);
		}
	}

	public static ReportWriter newInstance(OutputStream os, OutputFormat outputFormat, OutputStream errorStream) {
		try {
			switch (outputFormat) {
				case TEXT_OUTPUT_FORMAT:
					return new TextReportWriter(os, errorStream);
				case MRR_OUTPUT_FORMAT:
					return new MrrReportWriter(os, errorStream);
				case XML_OUTPUT_FORMAT:
					return new XmlReportWriter(os, errorStream);
				default:
					return null;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't create new ReportWriter instance", e);
			return null;
		}
	}

	public enum OutputFormat {
		MRR_OUTPUT_FORMAT("mrr"),
		XML_OUTPUT_FORMAT("xml"),
		TEXT_OUTPUT_FORMAT("text");

		private final String outputFormat;

		OutputFormat(String outputFormat) {
			this.outputFormat = outputFormat;
		}

		public static OutputFormat getOutputFormat(String outputFormat) {
			for (OutputFormat format : OutputFormat.values()) {
				if (format.outputFormat.equals(outputFormat)) {
					return format;
				}
			}
			throw new IllegalArgumentException("Format doesn't exists");
		}
	}
}
