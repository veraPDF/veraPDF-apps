/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org> All rights
 * reserved. VeraPDF Library GUI is free software: you can redistribute it
 * and/or modify it under the terms of either: The GNU General public license
 * GPLv3+. You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the
 * source tree. If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html. The Mozilla Public License
 * MPLv2+. You should have received a copy of the Mozilla Public License along
 * with VeraPDF Library GUI as the LICENSE.MPL file in the root of the source
 * tree. If a copy of the MPL was not distributed with this file, you can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.VeraPDFException;
import org.verapdf.policy.PolicyChecker;
import org.verapdf.processor.BatchProcessor;
import org.verapdf.processor.ItemProcessor;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.ProcessorResult;
import org.verapdf.processor.reports.ItemDetails;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
final class VeraPdfCliProcessor {
	private static final Logger logger = Logger.getLogger(VeraPdfCliProcessor.class.getCanonicalName());

	private final ConfigManager configManager;
	private final ProcessorConfig processorConfig;
	private final VeraAppConfig appConfig;
	private final boolean isPolicy;
	private final boolean isRecursive;
	private final File tempMrrFile;
	private final File policyFile;
	private boolean isStdOut = true;
	private boolean appendData = true;
	private String baseDirectory = ""; //$NON-NLS-1$

	private VeraPdfCliProcessor(final VeraCliArgParser args, ConfigManager configManager) throws VeraPDFException {
		this.configManager = configManager;
		this.isPolicy = args.isPolicy();
		this.isRecursive = args.isRecurse();
		try {
			this.tempMrrFile = (this.isPolicy) ? File.createTempFile("mrr", "veraPDF") : null; //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException excep) {
			throw new VeraPDFException(CliConstants.EXCEP_TEMP_MRR_CREATE, excep);
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
					String message = String.format(CliConstants.EXCEP_REPORT_OVERWRITE, file.getPath());
					logger.log(Level.WARNING, message, ex);
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
		// If the path list is empty then process the STDIN stream
		if (pdfPaths.isEmpty()) {
			processStdIn();
		} else {
			processFilePaths(pdfPaths);
		}

		if (this.isPolicy) {
			applyPolicy();
		}
	}

	static VeraPdfCliProcessor createProcessorFromArgs(final VeraCliArgParser args, ConfigManager config)
			throws VeraPDFException {
		return new VeraPdfCliProcessor(args, config);
	}

	private void processStdIn() {
		for (String messageLine : CliConstants.MESS_PROC_STDIN) {
			System.out.println(messageLine);
		}
		ItemDetails item = ItemDetails.fromValues(CliConstants.NAME_STDIN);
		processStream(item, System.in);

	}

	private void processFilePaths(final List<String> paths) {
		List<File> toFilter = new ArrayList<>();
		for (String path : paths) {
			toFilter.add(new File(path));
		}
		List<File> toProcess = ApplicationUtils.filterPdfFiles(toFilter, this.isRecursive);
		if (toProcess.isEmpty()) {
			logger.log(Level.SEVERE, "There is no files to process.");
			return;
		}
		try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.processorConfig);
				OutputStream reportStream = this.getReportStream()) {
			processor.process(toProcess,
					ProcessorFactory.getHandler(this.appConfig.getFormat(), this.appConfig.isVerbose(), reportStream,
							this.appConfig.getMaxFailsDisplayed(),
							this.processorConfig.getValidatorConfig().isRecordPasses()));
		} catch (VeraPDFException excep) {
			String message = CliConstants.EXCEP_VERA_BATCH;
			System.err.println(message);
			logger.log(Level.SEVERE, message, excep);
		} catch (IOException excep) {
			logger.log(Level.FINE, CliConstants.EXCEP_TEMP_MRR_CLOSE, excep);
		}
	}

	private void processStream(final ItemDetails item, final InputStream toProcess) {
		try (ItemProcessor processor = ProcessorFactory.createProcessor(this.processorConfig)) {

			ProcessorResult result = processor.process(item, toProcess);

			OutputStream outputReportStream = this.getReportStream();
			try {
				if (result.isPdf() && !result.isEncryptedPdf())
					ProcessorFactory.resultToXml(result, outputReportStream, true);
				else {
					String message = String.format(
							(result.isPdf()) ? CliConstants.MESS_PDF_ENCRYPTED : CliConstants.MESS_PDF_NOT_VALID,
							item.getName());
					outputReportStream.write(message.getBytes());
				}

			} catch (JAXBException | IOException excep) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, CliConstants.EXCEP_REPORT_MARSHAL, excep);
			}

			if (!this.isStdOut) {
				try {
					outputReportStream.close();
				} catch (IOException ex) {
					logger.log(Level.SEVERE, CliConstants.EXCEP_REPORT_CLOSE, ex);
				}
			}
		} catch (IOException excep) {
			logger.log(Level.FINER, CliConstants.EXCEP_PROCESSOR_CLOSE, excep);
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
			logger.log(Level.FINE, "Exception raised closing temporary policy file.", excep);
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
						logger.log(Level.SEVERE, "Cannot create subdirectories the: " + ex.toString() + "\n");
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
