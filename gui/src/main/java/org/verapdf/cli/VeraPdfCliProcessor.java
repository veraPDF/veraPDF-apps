/**
 *
 */
package org.verapdf.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.VeraPDFException;
import org.verapdf.processor.BatchProcessor;
import org.verapdf.processor.ItemProcessor;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.ProcessorResult;
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
	private String baseDirectory = "";

	private VeraPdfCliProcessor(final VeraCliArgParser args, ConfigManager configManager) throws VeraPDFException {
		this.configManager = configManager;
		this.appConfig = args.appConfig(configManager.getApplicationConfig());
		this.processorConfig = args.processorConfig(this.appConfig.getProcessType(),
				this.configManager.getFeaturesConfig());
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

		List<File> toProcess = new ArrayList<>();
		for (String pdfPath : pdfPaths) {
			File file = new File(pdfPath);
			if (file.isDirectory()) {
				this.baseDirectory = file.getAbsolutePath();
				processDir(file);
			} else if (checkFileCanBeProcessed(file)) {
				toProcess.add(file);
			}
			if (!toProcess.isEmpty())
				processFiles(toProcess);
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

	private void processFiles(final List<File> files) {
		try {
			BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.processorConfig);
			OutputStream reportStream = VeraPdfCliProcessor.getReportStream();
			processor.process(files,
					ProcessorFactory.getHandler(appConfig.getFormat(), appConfig.isVerbose(), reportStream));
		} catch (VeraPDFException e) {
			System.err.println("Exception raised while processing batch");
			e.printStackTrace();
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
		String reportPath = this.appConfig.getReportFile();
		BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.processorConfig);
		OutputStream reportStream = System.out;
		processor.process(dir, true,
				ProcessorFactory.getHandler(appConfig.getFormat(), appConfig.isVerbose(), reportStream));
	}

	private void processStream(final ItemDetails item, final InputStream toProcess) {
		ItemProcessor processor = ProcessorFactory.createProcessor(this.processorConfig);

		ProcessorResult result = processor.process(item, toProcess);

		OutputStream outputReportStream = VeraPdfCliProcessor.getReportStream();
		try {
			if (result.isValidPdf() && !result.isEncryptedPdf())
				ProcessorFactory.resultToXml(result, outputReportStream, true);
			else {
				String message = (result.isValidPdf()) ? item.getName() + " is and encrypted PDF."
						: item.getName() + " is not a valid PDF.";
				outputReportStream.write(message.getBytes());
			}

		} catch (JAXBException | IOException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}

		if (!this.isStdOut) {
			try {
				outputReportStream.close();
			} catch (IOException ex) {
				LOGGER.error("Cannot close the report file: " + ex.toString() + "\n");
			}
		}
	}

	@SuppressWarnings("resource")
	private static OutputStream getReportStream() {
		return System.out;
	}

	private String constructReportPath(final String itemName) {
		String reportPath = "";
		if (!this.configManager.getApplicationConfig().getReportFolder().isEmpty()) {
			Path fileAbsolutePath = Paths.get(itemName);
			String pdfFileName = fileAbsolutePath.getFileName().toString();
			String pdfFileDirectory = fileAbsolutePath.getParent().toString();
			String extension = "." + this.configManager.getApplicationConfig().getFormat().toString();
			String outputFileName = pdfFileName.replace(".pdf", extension);
			String reportFolder = this.configManager.getApplicationConfig().getReportFolder();

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
						reportFolder = this.configManager.getApplicationConfig().getReportFolder();
					}
				}
			}

			File reportFile = new File(reportFolder, outputFileName);
			reportPath = reportFile.getAbsolutePath();
			this.appendData = false;
		} else if (!this.configManager.getApplicationConfig().getReportFile().isEmpty()) {
			File reportFile = new File(this.configManager.getApplicationConfig().getReportFile());
			reportPath = reportFile.getAbsolutePath();
			this.appendData = true;
		}
		return reportPath;
	}
}
