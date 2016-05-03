package org.verapdf.gui;

import org.apache.log4j.Logger;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.processor.ProcessingResult;
import org.verapdf.processor.Processor;
import org.verapdf.processor.ProcessorImpl;
import org.verapdf.processor.config.Config;
import org.verapdf.report.HTMLReport;
import org.verapdf.report.ItemDetails;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.io.*;

/**
 * Validates PDF in a new thread.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<ProcessingResult, Integer> {

    private static final Logger LOGGER = Logger.getLogger(ValidateWorker.class);

    private static final String ERROR_IN_OPEN_STREAMS = "Can't open stream from PDF file or can't open stream to temporary XML report file";
    private static final String ERROR_IN_CREATING_TEMP_FILE = "Can't create temporary file for XML report";

    private File pdf;
    private CheckerPanel parent;
    private Config settings;
    private File xmlReport = null;
    private File htmlReport = null;
    private ProcessingResult processingResult = null;

    ValidateWorker(CheckerPanel parent, File pdf, Config settings) {
        if (pdf == null || !pdf.isFile() || !pdf.canRead()) {
            throw new IllegalArgumentException(
                    "PDF file doesn't exist or it can not be read");
        }
        this.parent = parent;
        this.pdf = pdf;
        this.settings = settings;
    }

    @Override
    protected ProcessingResult doInBackground() {
        try {
            this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
            this.xmlReport.deleteOnExit();
            this.htmlReport = null;
        } catch (IOException e) {
            LOGGER.error(ERROR_IN_CREATING_TEMP_FILE, e);
            parent.errorInValidatingOccur(ERROR_IN_CREATING_TEMP_FILE + ": ", e);
        }
        try (InputStream toProcess = new FileInputStream(pdf);
             OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
            Processor processor = new ProcessorImpl();
            processingResult = processor.validate(toProcess, ItemDetails.fromFile(pdf),
                    settings, mrrReport);
        } catch (IOException e) {
            LOGGER.error(ERROR_IN_OPEN_STREAMS, e);
            parent.errorInValidatingOccur(ERROR_IN_OPEN_STREAMS + ": ", e);
        }

        for (String errorMessage : processingResult.getErrorMessages()) {
            JOptionPane.showMessageDialog(this.parent,
                    errorMessage,
                    GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
        }

        if (processingResult.getReportSummary() == ProcessingResult.ReportSummary.REPORT_SUCCEED) {
            writeHtmlReport();
        }

        return processingResult;
    }

    @Override
    protected void done() {
        this.parent.validationEnded(this.xmlReport, this.htmlReport);
    }

    private void writeHtmlReport() {
        try {
            this.htmlReport = File.createTempFile("veraPDF-tempHTMLReport",
                    ".html");
            this.htmlReport.deleteOnExit();
            try (InputStream xmlStream = new FileInputStream(this.xmlReport);
                 OutputStream htmlStream = new FileOutputStream(
                         this.htmlReport)) {
                HTMLReport.writeHTMLReport(xmlStream, htmlStream, this.settings.getProfileWikiPath());

            } catch (IOException | TransformerException e) {
                JOptionPane.showMessageDialog(this.parent,
                        GUIConstants.ERROR_IN_SAVING_HTML_REPORT + e.getMessage(),
                        GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
                LOGGER.error("Exception saving the HTML report", e);
                this.htmlReport = null;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.parent,
                    GUIConstants.ERROR_IN_SAVING_HTML_REPORT + e.getMessage(),
                    GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
            LOGGER.error("Exception saving the HTML report", e);
            this.htmlReport = null;
        }
    }
}
