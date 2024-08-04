/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2024, veraPDF Consortium <info@verapdf.org>
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
package org.verapdf.cli.multithread;

import org.verapdf.apps.Applications;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.cli.CliConstants.ExitCodes;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.reports.ResultStructure;
import org.verapdf.processor.reports.multithread.MultiThreadProcessingHandler;
import org.verapdf.processor.reports.multithread.MultiThreadProcessingHandlerImpl;
import org.verapdf.processor.reports.multithread.writer.ReportWriter;
import org.verapdf.report.HTMLReport;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadProcessor {
	private static final Logger LOGGER = Logger.getLogger(MultiThreadProcessor.class.getCanonicalName());

	private static final int DEFAULT_BUFFER_SIZE = 512;
	private static final int COEFFICIENT_BUFFER_SIZE = 1024;

	private final Queue<File> filesToProcess;

	private int filesQuantity;

	private final File veraPDFStarterPath;
	private final List<String> veraPDFParameters;
	private OutputStream os;
	private final OutputStream errorStream;

	private final ReportWriter reportWriter;
	private final MultiThreadProcessingHandler processingHandler;

	private boolean isFirstReport = true;
	private boolean isHTMLReport;
	private File xmlReport;
	private final String wikiPath;

	private ExitCodes currentExitCode = ExitCodes.VALID;
	private CountDownLatch latch;

	private MultiThreadProcessor(VeraCliArgParser cliArgParser) {
		this.isHTMLReport = cliArgParser.getFormat() == FormatOption.HTML;
		if (isHTMLReport) {
			try {
				this.xmlReport = File.createTempFile("veraPDF", "report.xml");
				this.os = new BufferedOutputStream(new FileOutputStream(xmlReport), DEFAULT_BUFFER_SIZE * COEFFICIENT_BUFFER_SIZE);
			} catch (IOException e) {
				isHTMLReport = false;
				LOGGER.log(Level.WARNING, "Problem with generating html report");
			}
		}
		if (!isHTMLReport) {
			this.os = new BufferedOutputStream(System.out, DEFAULT_BUFFER_SIZE * COEFFICIENT_BUFFER_SIZE);
		}

		this.wikiPath = cliArgParser.getProfilesWikiPath();
		this.errorStream = new BufferedOutputStream(System.err, DEFAULT_BUFFER_SIZE);

		this.veraPDFStarterPath = getVeraPdfStarterFile(cliArgParser);
		this.veraPDFParameters = VeraCliArgParser.getBaseVeraPDFParameters(cliArgParser);
		this.filesToProcess = new ConcurrentLinkedQueue<>();
		this.filesToProcess.addAll(getFiles(cliArgParser.getPdfPaths(), cliArgParser.isRecurse(), cliArgParser.nonPdfExt()));
		this.filesQuantity = filesToProcess.size();

		FormatOption outputFormat = getOutputFormat(cliArgParser.getFormat().getOption());
		this.reportWriter = ReportWriter.newInstance(os, outputFormat, errorStream);
		this.processingHandler = new MultiThreadProcessingHandlerImpl(reportWriter);
	}

	public static ExitCodes process(VeraCliArgParser cliArgParser) throws InterruptedException {
		MultiThreadProcessor processor = new MultiThreadProcessor(cliArgParser);
		if (processor.currentExitCode != ExitCodes.VALID) {
			return processor.currentExitCode;
		}
		return processor.startProcesses(cliArgParser.getNumberOfProcesses());
	}

	private File getVeraPdfStarterFile(VeraCliArgParser cliArgParser) {
		File veraPDFPath = cliArgParser.getVeraCLIPath();
		if (veraPDFPath == null || !veraPDFPath.isFile()) {
			try {
				veraPDFPath = Applications.getVeraScriptFile();
				if (veraPDFPath == null) {
					throw new IllegalStateException("Can't obtain executable veraPDF CLI script path");
				}
			} catch (IllegalStateException e) {
				LOGGER.log(Level.SEVERE, "Can't obtain veraPDF CLI script path", e);
				this.currentExitCode = ExitCodes.FAILED_MULTIPROCESS_START;
			}
		}
		return veraPDFPath;
	}

	private FormatOption getOutputFormat(String outputFormat) {
		FormatOption formatOption = FormatOption.fromOption(outputFormat);
		if (formatOption == FormatOption.HTML) {
			return FormatOption.XML;
		}
		return formatOption;
	}

	public synchronized void write(ResultStructure result) {
		if (isFirstReport) {
			processingHandler.startReport();
			processingHandler.fillReport(result);
			isFirstReport = false;
		} else {
			processingHandler.fillReport(result);
		}

		this.filesQuantity--;

		if (filesQuantity == 0) {
			processingHandler.endReport();
			if (isHTMLReport) {
				this.os = new BufferedOutputStream(System.out, DEFAULT_BUFFER_SIZE * COEFFICIENT_BUFFER_SIZE);
				try (InputStream inputStream = new FileInputStream(xmlReport)) {
					HTMLReport.writeHTMLReport(inputStream, os, true, wikiPath, true);
				} catch (IOException | TransformerException e) {
					LOGGER.log(Level.WARNING, "Problem with generating html report");
				}
			}
		}
	}

	private List<File> getFiles(List<String> pdfPaths, boolean isRecurse, boolean nonPdfExt) {
		List<File> toFilter = new ArrayList<>(pdfPaths.size());
		pdfPaths.forEach(path -> toFilter.add(new File(path)));

		return ApplicationUtils.filterPdfFiles(toFilter, isRecurse, nonPdfExt);
	}

	private ExitCodes startProcesses(int numberOfProcesses) throws InterruptedException {
		int processesQuantity = Math.min(numberOfProcesses, filesToProcess.size());
		latch = new CountDownLatch(processesQuantity);
		ExecutorService executor = Executors.newFixedThreadPool(processesQuantity);
		for (int i = 0; i < processesQuantity; i++) {
			BaseCliRunner veraPDFRunner = new BaseCliRunner(this, veraPDFStarterPath.getAbsolutePath(), veraPDFParameters, filesToProcess);
			executor.submit(veraPDFRunner);
		}
		latch.await();
		return this.currentExitCode;
	}

	public void countDown(ExitCodes exitCode) {
		if (exitCode != null && exitCode.value > this.currentExitCode.value) {
			this.currentExitCode = exitCode;
		}
		if (this.latch != null) {
			this.latch.countDown();
		}
	}
}
