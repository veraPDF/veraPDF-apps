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
package org.verapdf.cli;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.VeraPDFException;
import org.verapdf.policy.PolicyChecker;
import org.verapdf.processor.*;
import org.verapdf.processor.reports.ItemDetails;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
final class VeraPdfCliProcessor {
	private static final Logger LOGGER = Logger.getLogger(VeraPdfCliProcessor.class);

	private final ConfigManager configManager;
	private final ProcessorConfig processorConfig;
	private final VeraAppConfig appConfig;
	private final boolean isPolicy;
	private final File tempMrrFile;
	private final File policyFile;
	private boolean isStdOut = true;
	private boolean appendData = true;
	private String baseDirectory = ""; //$NON-NLS-1$

	private VeraPdfCliProcessor(final VeraCliArgParser args, ConfigManager configManager) throws VeraPDFException {
		this.configManager = configManager;
		this.isPolicy = args.isPolicy();
		try {
			this.tempMrrFile = (this.isPolicy) ? File.createTempFile("mrr", "veraPDF") : null; //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException excep) {
			throw new VeraPDFException("Failed to create temporary MRR file", excep);
		}
		this.policyFile = args.getPolicyFile();
		this.appConfig = args.appConfig(configManager.getApplicationConfig());
		this.processorConfig = args.processorConfig(this.appConfig.getProcessType(),
				this.configManager.getFeaturesConfig(), this.configManager.getPluginsCollectionConfig());
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

	void processPaths(final List<String> pdfPaths) throws VeraPDFException {
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
			if (!isFileProcessable(file)) {
				throw new VeraPDFException("Could not process file " + pdfPath);
			} else if (file.isDirectory()) {
				this.baseDirectory = file.getAbsolutePath();
				processDir(file);
			} else if (isFileProcessable(file)) {
				toProcess.add(file);
			}
		}

		if (!toProcess.isEmpty()) {
			processFiles(toProcess);
		}

		if (this.isPolicy) {
			applyPolicy();
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
		try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.processorConfig);
				OutputStream reportStream = this.getReportStream()) {
			processor.process(files,
					ProcessorFactory.getHandler(this.appConfig.getFormat(), this.appConfig.isVerbose(), reportStream,
							this.appConfig.getMaxFailsDisplayed(),
							this.processorConfig.getValidatorConfig().isRecordPasses()));
		} catch (VeraPDFException e) {
			System.err.println("Exception raised while processing batch");
			e.printStackTrace();
		} catch (IOException excep) {
			LOGGER.debug("Exception raised closing MRR temp file.", excep);
		}
	}

	private static boolean isFileProcessable(final File file) {
		if (!file.exists()) {
			System.err.println("Path " + file.getAbsolutePath() + " cannot be found.");
			return false;
		} else if (!file.canRead()) {
			System.err.println("Path " + file.getAbsolutePath() + " is not readable.");
			return false;
		}
		return true;
	}

	private void processBatch(File dir) throws VeraPDFException {
		String reportPath = this.appConfig.getReportFile();
		try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.processorConfig);
				OutputStream reportStream = this.getReportStream()) {
			processor.process(dir, true,
					ProcessorFactory.getHandler(this.appConfig.getFormat(), this.appConfig.isVerbose(), reportStream,
							this.appConfig.getMaxFailsDisplayed(),
							this.processorConfig.getValidatorConfig().isRecordPasses()));
		} catch (IOException excep) {
			LOGGER.debug("Exception raised closing MRR temp file.", excep);
		}
	}

	private void processStream(final ItemDetails item, final InputStream toProcess) {
		ItemProcessor processor = ProcessorFactory.createProcessor(this.processorConfig);

		ProcessorResult result = processor.process(item, toProcess);

		OutputStream outputReportStream = this.getReportStream();
		try {
			if (result.isPdf() && !result.isEncryptedPdf())
				ProcessorFactory.resultToXml(result, outputReportStream, true);
			else {
				String message = (result.isPdf()) ? item.getName() + " is and encrypted PDF."
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

	private OutputStream getReportStream() {
		if (this.isPolicy) {
			if (this.tempMrrFile == null)
				throw new IllegalStateException("Policy enabled BUT no temp destination");
			try {
				this.isStdOut = false;
				return new FileOutputStream(this.tempMrrFile);
			} catch (FileNotFoundException excep) {
				throw new IllegalStateException("Policy enabled BUT no temp destination", excep);
			}
		}
		return System.out;
	}

	private void applyPolicy() throws VeraPDFException {
		File tempPolicyResult = null;
		try {
			tempPolicyResult = File.createTempFile("policyResult", "veraPDF");
		} catch (IOException excep) {
			throw new VeraPDFException("Could not create temporary policy result file.", excep);
		}
		try (InputStream mrrIs = new FileInputStream(this.tempMrrFile);
				OutputStream policyResultOs = new FileOutputStream(tempPolicyResult)) {
			PolicyChecker.applyPolicy(this.policyFile, mrrIs, policyResultOs);
			PolicyChecker.insertPolicyReport(tempPolicyResult, this.tempMrrFile, System.out);
		} catch (FileNotFoundException excep) {
			throw new VeraPDFException("Could not find temporary policy result file.", excep);
		} catch (IOException excep) {
			LOGGER.debug("Exception raised closing temporary policy file.", excep);
		}
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
