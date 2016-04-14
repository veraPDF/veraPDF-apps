package org.verapdf.gui;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.verapdf.core.ValidationException;
import org.verapdf.features.pb.PBFeatureParser;
import org.verapdf.features.tools.FeaturesCollection;
import org.verapdf.processor.ProcessingResult;
import org.verapdf.processor.Processor;
import org.verapdf.processor.ProcessorImpl;
import org.verapdf.processor.config.Config;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.processor.config.ProcessingType;
import org.verapdf.metadata.fixer.impl.MetadataFixerImpl;
import org.verapdf.metadata.fixer.impl.pb.FixerConfigImpl;
import org.verapdf.metadata.fixer.utils.FileGenerator;
import org.verapdf.metadata.fixer.utils.FixerConfig;
import org.verapdf.model.ModelParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.results.MetadataFixerResult;
import org.verapdf.pdfa.results.MetadataFixerResult.RepairStatus;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.validation.ValidationProfile;
import org.verapdf.pdfa.validators.Validators;
import org.verapdf.report.HTMLReport;
import org.verapdf.report.ItemDetails;
import org.verapdf.report.MachineReadableReport;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Validates PDF in a new threat.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<Void, Integer> {

    private static final Logger LOGGER = Logger.getLogger(ValidateWorker.class);

    private File pdf;
    private CheckerPanel parent;
    private Config settings;
    private File xmlReport = null;
    private File htmlReport = null;
	private ProcessingResult processingResult = null;
    
    ValidateWorker(CheckerPanel parent, File pdf, ValidationProfile profile,
            Config settings) {
        if (pdf == null || !pdf.isFile() || !pdf.canRead()) {
            throw new IllegalArgumentException(
                    "PDF file doesn't exist or it can not be read");
        }
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Profile doesn't exist or it can not be read");
        }
        this.parent = parent;
        this.pdf = pdf;
        this.settings = settings;
    }

    @Override
    protected Void doInBackground() {
		try {
			this.xmlReport = File.createTempFile("veraPDF-tempXMLReport",
					".xml");
			this.xmlReport.deleteOnExit();
			this.htmlReport = null;
		} catch (IOException e) {
			//TODO: handle
		}
		try (InputStream toProcess = new FileInputStream(pdf);
		OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
			Processor processor = new ProcessorImpl();
			processingResult = processor.validate(toProcess, ItemDetails.fromFile(pdf),
					settings, mrrReport);
		} catch (IOException e) {
			//TODO: handle
		}

        writeHtmlReport();

		return null;
    }

    @Override
    protected void done() {
        this.parent.validationEnded(this.xmlReport, this.htmlReport,
				this.processingResult);
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
						GUIConstants.ERROR_IN_SAVING_HTML_REPORT,
						GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
				LOGGER.error("Exception saving the HTML report", e);
				this.htmlReport = null;
			}
        } catch (IOException | JAXBException e) {
            JOptionPane.showMessageDialog(this.parent,
                    GUIConstants.ERROR_IN_SAVING_XML_REPORT,
                    GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
            LOGGER.error("Exception saving the XML report", e);
            this.xmlReport = null;
        }
    }
}
