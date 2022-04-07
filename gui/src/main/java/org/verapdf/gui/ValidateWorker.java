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

import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.core.VeraPDFException;
import org.verapdf.core.utils.LogsFileHandler;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.policy.PolicyChecker;
import org.verapdf.processor.*;
import org.verapdf.processor.reports.BatchSummary;
import org.verapdf.report.HTMLReport;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates PDF in a new thread.
 *
 * @author Maksim Bezrukov
 */
class ValidateWorker extends SwingWorker<ValidateWorker.ValidateWorkerSummary, Integer> {
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
	private ValidateWorkerSummary validateWorkerSummary = null;

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
	protected ValidateWorkerSummary doInBackground() {
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
			ValidatorConfig validatorConfig = this.configManager.getValidatorConfig();
			LogsFileHandler.setLoggingLevel(validatorConfig.getLoggingLevel());
			ProcessType processType = veraAppConfig.getProcessType();
			boolean isPolicy = (processType == ProcessType.POLICY || processType == ProcessType.POLICY_FIX)
					&& this.policy != null;
			EnumSet<TaskType> tasks = processType.getTasks();
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
					                              this.configManager.getPluginsCollectionConfig(),
					                              this.configManager.getFixerConfig(), tasks,
					                              veraAppConfig.getFixesFolder())
					: ProcessorFactory.fromValues(validatorConfig, featuresConfig,
					                              this.configManager.getPluginsCollectionConfig(),
					                              this.configManager.getFixerConfig(), tasks,
					                              this.customProfile, veraAppConfig.getFixesFolder());
			try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(resultConfig)) {
				VeraAppConfig applicationConfig = this.configManager.getApplicationConfig();
				BatchSummary batchSummary = processor.process(this.pdfs,
						ProcessorFactory.getHandler(FormatOption.MRR, applicationConfig.isVerbose(), mrrReport,
								validatorConfig.isRecordPasses()));
				validateWorkerSummary = new ValidateWorkerSummary(batchSummary);
				if (isPolicy) {
					applyPolicy();
					validateWorkerSummary.setPolicyNonCompliantJobCount(countFailedPolicyJobs(xmlReport));
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, ERROR_IN_OPEN_STREAMS, e);
			this.parent.handleValidationError(ERROR_IN_OPEN_STREAMS + ": ", e); //$NON-NLS-1$
		} catch (VeraPDFException | XPathExpressionException | ParserConfigurationException | SAXException  e) {
			logger.log(Level.SEVERE, ERROR_IN_PROCESSING, e);
			this.parent.handleValidationError(ERROR_IN_PROCESSING + ": ", e); //$NON-NLS-1$
		}
		if (this.validateWorkerSummary != null) {
			writeHtmlReport();
		}

		return validateWorkerSummary;
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
					HTMLReport.writeHTMLReport(xmlStream, htmlStream,
					                           this.validateWorkerSummary.getBatchSummary().isMultiJob(),
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

	private int countFailedPolicyJobs(File xmlReport) throws XPathExpressionException,
	                                                         ParserConfigurationException,
	                                                         IOException, SAXException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(xmlReport);
		XPath path = XPathFactory.newInstance().newXPath();
		int failedPolicyJobsCount = ((Number) path.evaluate("count(//policyReport[@failedChecks > 0])",
		                                                    document, XPathConstants.NUMBER)).intValue();
		return failedPolicyJobsCount;
	}

	public class ValidateWorkerSummary {
		private BatchSummary batchSummary;
		private int policyNonCompliantJobCount = -1;

		public ValidateWorkerSummary(BatchSummary batchSummary, int policyNonCompliantJobCount) {
			this.batchSummary = batchSummary;
			this.policyNonCompliantJobCount = policyNonCompliantJobCount;
		}

		public ValidateWorkerSummary(BatchSummary batchSummary) {
			this.batchSummary = batchSummary;
		}

		public BatchSummary getBatchSummary() {
			return this.batchSummary;
		}

		public int getPolicyNonCompliantJobCount() {
			return policyNonCompliantJobCount;
		}

		public void setPolicyNonCompliantJobCount(int failedPolicyJobs) {
			this.policyNonCompliantJobCount = failedPolicyJobs;
		}

		public boolean isPolicyApplied(){
			return policyNonCompliantJobCount >= 0;
		}
	}
}
