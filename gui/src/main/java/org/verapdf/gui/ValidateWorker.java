package org.verapdf.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.core.VeraPDFException;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.processor.*;
import org.verapdf.processor.reports.BatchSummary;
import org.verapdf.report.HTMLReport;
import org.verapdf.report.ItemDetails;

/**
 * Validates PDF in a new thread.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<Object, Integer> {

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
	private ProcessorResult processingResult = null;
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
	protected Object doInBackground() {
		try {
			this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
			this.xmlReport.deleteOnExit();
			this.htmlReport = null;
		} catch (IOException e) {
			LOGGER.error(ERROR_IN_CREATING_TEMP_FILE, e);
			this.parent.errorInValidatingOccur(ERROR_IN_CREATING_TEMP_FILE + ": ", e);
		}
		try (OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
			if (this.pdf.isFile()) {
				try (InputStream toProcess = new FileInputStream(this.pdf)) {
					ProcessorConfig processorConfig = this.configManager.createProcessorConfig();
					ProcessorConfig resultConfig = this.customProfile == null ?
							processorConfig :
							ProcessorFactory.fromValues(this.configManager.getValidatorConfig(),
									this.configManager.getFeaturesConfig(),
									this.configManager.getFixerConfig(),
									processorConfig.getTasks(),
									customProfile);
					ItemProcessor processor = ProcessorFactory.createProcessor(resultConfig);
					this.processingResult = processor.process(ItemDetails.fromFile(this.pdf), toProcess);
					ProcessorFactory.resultToXml(this.processingResult, mrrReport, true);
				} catch (JAXBException e) {
					LOGGER.error(ERROR_IN_SAVING_REPORT, e);
					this.parent.errorInValidatingOccur(ERROR_IN_SAVING_REPORT + ": ", e);
				}
			} else if (this.pdf.isDirectory()) {
				BatchProcessor processor = ProcessorFactory.fileBatchProcessor(this.configManager.createProcessorConfig());
				VeraAppConfig applicationConfig = this.configManager.getApplicationConfig();
				try {
					this.batchSummary = processor.process(this.pdf, true, ProcessorFactory.getHandler(
							applicationConfig.getFormat(),
							applicationConfig.isVerbose(),
							mrrReport));
				}  catch (VeraPDFException e) {
					LOGGER.error(ERROR_IN_PROCESSING, e);
					this.parent.errorInValidatingOccur(ERROR_IN_PROCESSING + ": ", e);
				}
			}
		} catch (IOException e) {
			LOGGER.error(ERROR_IN_OPEN_STREAMS, e);
			this.parent.errorInValidatingOccur(ERROR_IN_OPEN_STREAMS + ": ", e);
		}

		writeHtmlReport();

		return this.processingResult == null ? this.batchSummary : this.processingResult;
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
