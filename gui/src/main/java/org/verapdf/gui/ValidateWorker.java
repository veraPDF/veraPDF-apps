package org.verapdf.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.core.VeraPDFException;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.processor.BatchProcessor;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;
import org.verapdf.processor.reports.BatchSummary;
import org.verapdf.report.HTMLReport;

/**
 * Validates PDF in a new thread.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<BatchSummary, Integer> {

	private static final Logger LOGGER = Logger.getLogger(ValidateWorker.class);

	private static final String ERROR_IN_OPEN_STREAMS = "Can't open stream from PDF file or can't open stream to temporary XML report file";
	private static final String ERROR_IN_PROCESSING = "Error during the processing";
	private static final String ERROR_IN_CREATING_TEMP_FILE = "Can't create temporary file for XML report";
	private static final String ERROR_IN_SAVING_REPORT = "Can't serialize xml report";

	private File pdf;
	private ValidationProfile customProfile;
	private CheckerPanel parent;
	private ConfigManager configManager;
	private File xmlReport = null;
	private File htmlReport = null;
	private BatchSummary batchSummary = null;

	ValidateWorker(CheckerPanel parent, File pdf, ConfigManager configManager, ValidationProfile customProfile) {
		if (pdf == null || !pdf.canRead()) {
			throw new IllegalArgumentException("PDF file doesn't exist or it can not be read");
		}
		this.parent = parent;
		this.pdf = pdf;
		this.configManager = configManager;
		this.customProfile = customProfile;
	}

	@Override
	protected BatchSummary doInBackground() {
		try {
			this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
			this.xmlReport.deleteOnExit();
			this.htmlReport = null;
		} catch (IOException e) {
			LOGGER.error(ERROR_IN_CREATING_TEMP_FILE, e);
			this.parent.errorInValidatingOccur(ERROR_IN_CREATING_TEMP_FILE + ": ", e);
		}
		try (OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
			VeraAppConfig veraAppConfig = parent.appConfigFromState();
			EnumSet<TaskType> tasks = veraAppConfig.getProcessType().getTasks();
			ValidatorConfig validatorConfig = this.configManager.getValidatorConfig();
			ProcessorConfig resultConfig = this.customProfile == null
					? ProcessorFactory.fromValues(validatorConfig,
					this.configManager.getFeaturesConfig(), this.configManager.getFixerConfig(),
					tasks, veraAppConfig.getFixesFolder())
					: ProcessorFactory.fromValues(validatorConfig,
					this.configManager.getFeaturesConfig(), this.configManager.getFixerConfig(),
					tasks, customProfile, veraAppConfig.getFixesFolder());
			BatchProcessor processor = ProcessorFactory.fileBatchProcessor(resultConfig);
			VeraAppConfig applicationConfig = this.configManager.getApplicationConfig();
			if (this.pdf.isDirectory()) {
				this.batchSummary = processor.process(this.pdf, true,
						ProcessorFactory.getHandler(FormatOption.MRR, applicationConfig.isVerbose(), mrrReport, applicationConfig.getMaxFailsDisplayed(), validatorConfig.isRecordPasses()));
			} else {
				List<File> file = new ArrayList<>(1);
				file.add(this.pdf);
				this.batchSummary = processor.process(file,
						ProcessorFactory.getHandler(FormatOption.MRR, applicationConfig.isVerbose(), mrrReport, applicationConfig.getMaxFailsDisplayed(), validatorConfig.isRecordPasses()));
			}
		} catch (IOException e) {
			LOGGER.error(ERROR_IN_OPEN_STREAMS, e);
			this.parent.errorInValidatingOccur(ERROR_IN_OPEN_STREAMS + ": ", e);
		} catch (VeraPDFException e) {
			LOGGER.error(ERROR_IN_PROCESSING, e);
			this.parent.errorInValidatingOccur(ERROR_IN_PROCESSING + ": ", e);
		}

		// TODO: change "== 1" to "> 0" when HTML report will work in case of multiply files
		if (this.batchSummary.getValidPdfaCount() + this.batchSummary.getInvalidPdfaCount() == 1) {
			writeHtmlReport();
		}

		return this.batchSummary;
	}

	@Override
	protected void done() {
		this.parent.validationEnded(this.xmlReport, this.htmlReport);
	}

	private void writeHtmlReport() {
		try {
			this.htmlReport = File.createTempFile("veraPDF-tempHTMLReport", ".html");
			this.htmlReport.deleteOnExit();
			try (InputStream xmlStream = new FileInputStream(this.xmlReport);
				 OutputStream htmlStream = new FileOutputStream(this.htmlReport)) {
				HTMLReport.writeHTMLReport(xmlStream, htmlStream,
						this.configManager.getApplicationConfig().getWikiPath(), true);

			} catch (IOException | TransformerException e) {
				JOptionPane.showMessageDialog(this.parent, GUIConstants.ERROR_IN_SAVING_HTML_REPORT + e.getMessage(),
						GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
				LOGGER.error("Exception saving the HTML report", e);
				this.htmlReport = null;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this.parent, GUIConstants.ERROR_IN_SAVING_HTML_REPORT + e.getMessage(),
					GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception saving the HTML report", e);
			this.htmlReport = null;
		}
	}
}
