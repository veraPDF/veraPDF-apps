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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.VeraProcessor;
import org.verapdf.report.ItemDetails;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
final class VeraPdfCliProcessor {
	private static final Logger LOGGER = Logger.getLogger(VeraPdfCliProcessor.class);

	private final ConfigManager configManager;
	private final VeraAppConfig appConfig;
	private boolean isStdOut = true;
	private boolean appendData = true;
	private final boolean recurse;
	private String baseDirectory = "";

	private VeraPdfCliProcessor(final VeraCliArgParser args, ConfigManager configManager) {
		this.configManager = configManager;
		this.appConfig = VeraCliArgParser.parseAppConfig(configManager.getApplicationConfig(), args);
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

	public VeraAppConfig getConfig() {
		return this.appConfig;
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

	static VeraPdfCliProcessor createProcessorFromArgs(final VeraCliArgParser args, ConfigManager config) {
		return new VeraPdfCliProcessor(args, config);
	}

	private void processDir(final File dir) {
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				int extIndex = file.getName().lastIndexOf(".");
				String ext = file.getName().substring(extIndex + 1);
				if ("pdf".equalsIgnoreCase(ext)) {
					processFile(file);
				}
			} else if (file.isDirectory()) {
				if (this.recurse) {
					processDir(file);
				}
			}
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

	private void processStream(final ItemDetails item, final InputStream toProcess) {
		VeraProcessor processor = ProcessorFactory.createProcessor(this.configManager.createProcessorConfig());
		OutputStream outputReportStream = this.getReportStream(item.getName());

		processor.process(item, toProcess);

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
