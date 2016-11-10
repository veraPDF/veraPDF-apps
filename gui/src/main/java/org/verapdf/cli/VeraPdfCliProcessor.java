/**
 *
 */
package org.verapdf.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.VeraPDFException;
import org.verapdf.processor.*;
import org.verapdf.report.ItemDetails;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
final class VeraPdfCliProcessor {
	private static final Logger LOGGER = Logger.getLogger(VeraPdfCliProcessor.class);

	private final ConfigManager configManager;
	private final ProcessorConfig processorConfig;
	private final VeraAppConfig appConfig;
	private boolean isStdOut = true;
	private boolean appendData = true;
	private final boolean recurse;
	private String baseDirectory = "";

	private VeraPdfCliProcessor(final VeraCliArgParser args, ConfigManager configManager) throws VeraPDFException {
		this.configManager = configManager;
		this.appConfig = args.appConfig(configManager.getApplicationConfig());
		this.processorConfig = args.processorConfig(this.appConfig.getProcessType(),
				this.configManager.getFeaturesConfig());
		this.recurse = args.isRecurse();
		if (this.configManager.getApplicationConfig().isOverwriteReport()) {
			File file = new File(this.configManager.getApplicationConfig().getReportFile());
			if (file.exists()) {
				try {
					file.delete();
				} catch (SecurityException ex) {
					LOGGER.warn("Cannot delete older report file.", ex);
				}
			}
		}

	}

	VeraAppConfig getConfig() {
		return this.appConfig;
	}

	ProcessorConfig getProcessorConfig() {
		return this.processorConfig;
	}

	void processPaths(final List<String> pdfPaths) {
		// If the path list is empty then
		if (pdfPaths.isEmpty()) {
			System.out.println("veraPDF is processing STDIN and is expecting an EOF marker.");
			System.out.println("If this isn't your intention you can terminate by typing an EOF equivalent:");
			System.out.println(" - Linux or Mac users should type CTRL-D");
			System.out.println(" - Windows users should type CTRL-Z");
			ItemDetails item = ItemDetails.fromValues("STDIN");
			processStream(item, System.in);
		}

		for (String pdfPath : pdfPaths) {
			File file = new File(pdfPath);
			if (file.isDirectory()) {
				this.baseDirectory = file.getAbsolutePath();
				processDir(file);
			} else {
				processFile(file);
			}
		}
	}

	static VeraPdfCliProcessor createProcessorFromArgs(final VeraCliArgParser args, ConfigManager config)
			throws VeraPDFException {
		return new VeraPdfCliProcessor(args, config);
	}

	private void processDir(final File dir) {

		try {
			processBatch(dir);
		} catch (VeraPDFException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}
	}

	private void processFile(final File pdfFile) {
		if (checkFileCanBeProcessed(pdfFile)) {
			try (InputStream toProcess = new FileInputStream(pdfFile)) {
				processStream(ItemDetails.fromFile(pdfFile), toProcess);
			} catch (IOException e) {
				System.err.println("Exception raised while processing " + pdfFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	private static boolean checkFileCanBeProcessed(final File file) {
		if (!file.isFile()) {
			System.err.println("Path " + file.getAbsolutePath() + " is not an existing file.");
			return false;
		} else if (!file.canRead()) {
			System.err.println("Path " + file.getAbsolutePath() + " is not a readable file.");
			return false;
		}
		return true;
	}

	private void processBatch(File dir) throws VeraPDFException {
		BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.processorConfig);
		Writer wrtStdOut = new PrintWriter(System.out);
		processor.process(dir, true, ProcessorFactory.getHandler(appConfig.getFormat(),
				appConfig.isVerbose(), System.out));
	}

	private void processStream(final ItemDetails item, final InputStream toProcess) {
		ItemProcessor processor = ProcessorFactory.createProcessor(this.processorConfig);

		ProcessorResult result = processor.process(item, toProcess);

		OutputStream outputReportStream = this.getReportStream(item.getName());
		try {
			ProcessorFactory.resultToXml(result, System.out, true);
			ProcessorFactory.configToXml(this.processorConfig, System.out, true);
		} catch (JAXBException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}

		if (this.isStdOut == false) {
			try {
				outputReportStream.close();
			} catch (IOException ex) {
				LOGGER.error("Cannot close the report file: " + ex.toString() + "\n");
			}
		}
	}

	@SuppressWarnings("resource")
	private OutputStream getReportStream(final String itemName) {
		OutputStream reportStream = System.out;
		String reportFileName = this.constructReportPath(itemName);
		if (!reportFileName.isEmpty()) {
			try {
				reportStream = new FileOutputStream(reportFileName, this.appendData);
				this.isStdOut = false;
			} catch (FileNotFoundException ex) {
				LOGGER.warn("Can't open report file:" + itemName, ex);
				reportStream = System.out;
				this.isStdOut = true;
			}
		}
		return reportStream;
	}

	private String constructReportPath(final String itemName) {
		String reportPath = "";
		if (!this.configManager.getApplicationConfig().getReportFolder().toString().isEmpty()) {
			Path fileAbsolutePath = Paths.get(itemName);
			String pdfFileName = fileAbsolutePath.getFileName().toString();
			String pdfFileDirectory = fileAbsolutePath.getParent().toString();
			String extension = "." + this.configManager.getApplicationConfig().getFormat().toString();
			String outputFileName = pdfFileName.replace(".pdf", extension);
			String reportFolder = this.configManager.getApplicationConfig().getReportFolder().toString();

			if (pdfFileDirectory.length() > this.baseDirectory.length()) {
				StringBuilder reportFolderBuilder = new StringBuilder();
				reportFolderBuilder.append(reportFolder);

				String subDirectory = pdfFileDirectory.substring(this.baseDirectory.length());
				reportFolderBuilder.append(subDirectory);

				reportFolder = reportFolderBuilder.toString();

				File dir = new File(reportFolder);

				if (!dir.exists()) {
					try {
						dir.mkdirs();
					} catch (SecurityException ex) {
						LOGGER.error("Cannot create subdirectories the: " + ex.toString() + "\n");
						reportFolder = this.configManager.getApplicationConfig().getReportFolder().toString();
					}
				}
			}

			File reportFile = new File(reportFolder, outputFileName);
			reportPath = reportFile.getAbsolutePath();
			this.appendData = false;
		} else if (!this.configManager.getApplicationConfig().getReportFile().toString().isEmpty()) {
			File reportFile = new File(this.configManager.getApplicationConfig().getReportFile());
			reportPath = reportFile.getAbsolutePath();
			this.appendData = true;
		}
		return reportPath;
	}
}
