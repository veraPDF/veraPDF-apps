package org.verapdf.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.verapdf.apps.ConfigManager;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.ProcessorResult;
import org.verapdf.processor.VeraProcessor;
import org.verapdf.report.HTMLReport;
import org.verapdf.report.ItemDetails;

/**
 * Validates PDF in a new thread.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<ProcessorResult, Integer> {

	private static final Logger LOGGER = Logger.getLogger(ValidateWorker.class);

	private static final String ERROR_IN_OPEN_STREAMS = "Can't open stream from PDF file or can't open stream to temporary XML report file";
	private static final String ERROR_IN_CREATING_TEMP_FILE = "Can't create temporary file for XML report";

	private File pdf;
	private CheckerPanel parent;
	private ConfigManager configManager;
	private File xmlReport = null;
	private File htmlReport = null;
	private ProcessorResult processingResult = null;

	ValidateWorker(CheckerPanel parent, File pdf, ConfigManager configManager) {
		if (pdf == null || !pdf.isFile() || !pdf.canRead()) {
			throw new IllegalArgumentException("PDF file doesn't exist or it can not be read");
		}
		this.parent = parent;
		this.pdf = pdf;
		this.configManager = configManager;
	}

	@Override
	protected ProcessorResult doInBackground() {
		try {
			this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
			this.xmlReport.deleteOnExit();
			this.htmlReport = null;
		} catch (IOException e) {
			LOGGER.error(ERROR_IN_CREATING_TEMP_FILE, e);
			this.parent.errorInValidatingOccur(ERROR_IN_CREATING_TEMP_FILE + ": ", e);
		}
		try (InputStream toProcess = new FileInputStream(this.pdf);
				OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
			VeraProcessor processor = ProcessorFactory.createProcessor(this.configManager.createProcessorConfig());
			this.processingResult = processor.process(ItemDetails.fromFile(this.pdf), toProcess);
		} catch (IOException e) {
			LOGGER.error(ERROR_IN_OPEN_STREAMS, e);
			this.parent.errorInValidatingOccur(ERROR_IN_OPEN_STREAMS + ": ", e);
		}

		writeHtmlReport();

		return this.processingResult;
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
						this.configManager.getApplicationConfig().getWikiPath().toString(), true);

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
