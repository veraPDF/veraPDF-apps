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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.policy.PolicyChecker;
import org.verapdf.processor.BatchProcessor;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;
import org.verapdf.processor.reports.BatchSummary;
import org.verapdf.report.HTMLReport;
import org.xml.sax.SAXException;

/**
 * Validates PDF in a new thread.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<BatchSummary, Integer> {

    private static final Logger logger = Logger.getLogger(ValidateWorker.class.getCanonicalName());

    private static final String ERROR_IN_OPEN_STREAMS = "Can't open stream from PDF file or can't open stream to temporary XML report file"; //$NON-NLS-1$
    private static final String ERROR_IN_PROCESSING = "Error during the processing"; //$NON-NLS-1$
    private static final String ERROR_IN_CREATING_TEMP_FILE = "Can't create temporary file for XML report"; //$NON-NLS-1$
    private static final String ERROR_IN_OBTAINING_POLICY_FEATURES = "Can't obtain enabled features from policy files"; //$NON-NLS-1$

    private List<File> pdfs;
    private ValidationProfile customProfile;
    private File policy;
    private CheckerPanel parent;
    private ConfigManager configManager;
    private File xmlReport = null;
    private File htmlReport = null;
    private BatchSummary batchSummary = null;

    ValidateWorker(CheckerPanel parent, List<File> pdfs, ConfigManager configManager, ValidationProfile customProfile,
                   File policy) {
        if (pdfs == null) {
            throw new IllegalArgumentException("List of pdf files can not be null"); //$NON-NLS-1$
        }
        this.parent = parent;
        this.pdfs = pdfs;
        this.configManager = configManager;
        this.customProfile = customProfile;
        this.policy = policy;
    }

    @Override
    protected BatchSummary doInBackground() {
		try {
			this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml"); //$NON-NLS-1$//$NON-NLS-2$
			this.xmlReport.deleteOnExit();
			this.htmlReport = null;
		} catch (IOException e) {
			logger.log(Level.SEVERE, ERROR_IN_CREATING_TEMP_FILE, e);
			this.parent.handleValidationError(ERROR_IN_CREATING_TEMP_FILE + ": ", e); //$NON-NLS-1$
		}
		try (OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
			VeraAppConfig veraAppConfig = this.parent.appConfigFromState();
			ProcessType processType = veraAppConfig.getProcessType();
			boolean isPolicy = (processType == ProcessType.POLICY || processType == ProcessType.POLICY_FIX)
					&& this.policy != null;
			EnumSet<TaskType> tasks = processType.getTasks();
			ValidatorConfig validatorConfig = this.configManager.getValidatorConfig();
			FeatureExtractorConfig featuresConfig = this.configManager.getFeaturesConfig();
			if (isPolicy) {
				try (InputStream policyStream = new FileInputStream(this.policy)) {
					featuresConfig = ApplicationUtils.mergeEnabledFeaturesFromPolicy(featuresConfig, policyStream);
				} catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
					logger.log(Level.SEVERE, ERROR_IN_OBTAINING_POLICY_FEATURES, e);
					this.parent.handleValidationError(ERROR_IN_OBTAINING_POLICY_FEATURES + ": ", e);
				}
			}
			ProcessorConfig resultConfig = this.customProfile == null
					? ProcessorFactory.fromValues(validatorConfig, featuresConfig,
							this.configManager.getPluginsCollectionConfig(), this.configManager.getFixerConfig(), tasks,
							veraAppConfig.getFixesFolder())
					: ProcessorFactory.fromValues(validatorConfig, featuresConfig,
							this.configManager.getPluginsCollectionConfig(), this.configManager.getFixerConfig(), tasks,
							this.customProfile, veraAppConfig.getFixesFolder());
			try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(resultConfig)) {
				VeraAppConfig applicationConfig = this.configManager.getApplicationConfig();
				this.batchSummary = processor.process(this.pdfs,
						ProcessorFactory.getHandler(FormatOption.MRR, applicationConfig.isVerbose(), mrrReport,
								applicationConfig.getMaxFailsDisplayed(), validatorConfig.isRecordPasses()));

				if (isPolicy) {
					applyPolicy();
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, ERROR_IN_OPEN_STREAMS, e);
			this.parent.handleValidationError(ERROR_IN_OPEN_STREAMS + ": ", e); //$NON-NLS-1$
		} catch (VeraPDFException e) {
			logger.log(Level.SEVERE, ERROR_IN_PROCESSING, e);
			this.parent.handleValidationError(ERROR_IN_PROCESSING + ": ", e); //$NON-NLS-1$
		}
        if (this.batchSummary != null) {
            writeHtmlReport();
        }

        return this.batchSummary;
    }

    private void applyPolicy() throws IOException, VeraPDFException {
        File tempMrrFile = this.xmlReport;
        this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
        this.xmlReport.deleteOnExit();
        File tempPolicyResult = File.createTempFile("policyResult", "veraPDF"); //$NON-NLS-1$ //$NON-NLS-2$
        try (InputStream mrrIs = new FileInputStream(tempMrrFile);
             OutputStream policyResultOs = new FileOutputStream(tempPolicyResult);
             OutputStream mrrReport = new FileOutputStream(this.xmlReport)) {
            PolicyChecker.applyPolicy(this.policy, mrrIs, policyResultOs);
            PolicyChecker.insertPolicyReport(tempPolicyResult, tempMrrFile, mrrReport);
        }
        if (!tempPolicyResult.delete()) {
            tempPolicyResult.deleteOnExit();
        }
    }

    @Override
    protected void done() {
        this.parent.validationEnded(this.xmlReport, this.htmlReport);
    }

    private void writeHtmlReport() {
        final String extension = "html";
        final String ext = "." + extension;
        try {
            this.htmlReport = File.createTempFile("veraPDF-tempHTMLReport", ext); //$NON-NLS-1$
            this.htmlReport.deleteOnExit();
            try (InputStream xmlStream = new FileInputStream(this.xmlReport);
                 OutputStream htmlStream = new FileOutputStream(this.htmlReport)) {
                HTMLReport.writeHTMLReport(xmlStream, htmlStream, this.batchSummary.isMultiJob(),
                        this.configManager.getApplicationConfig().getWikiPath(), true);

            } catch (IOException | TransformerException excep) {
                final String message = String.format(GUIConstants.IOEXCEP_SAVING_REPORT, extension);
                JOptionPane.showMessageDialog(this.parent,
                        String.format(GUIConstants.IOEXCEP_SAVING_REPORT, extension), GUIConstants.ERROR,
                        JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, message, excep);
                this.htmlReport = null;
            }
        } catch (IOException excep) {
            final String message = String.format(GUIConstants.IOEXCEP_SAVING_REPORT, extension);
            JOptionPane.showMessageDialog(this.parent, message, GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message, excep);
            this.htmlReport = null;
        }
    }
}
