package org.verapdf.gui;

import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.policy.PolicyChecker;
import org.verapdf.processor.*;
import org.verapdf.processor.reports.BatchSummary;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunnableWorker implements Runnable{
    private static final Logger logger = Logger.getLogger(TestRunnableWorker.class.getCanonicalName());

    private static final String ERROR_IN_OPEN_STREAMS = "Can't open stream from PDF file or can't open stream to temporary XML report file"; //$NON-NLS-1$
    private static final String ERROR_IN_PROCESSING = "Error during the processing"; //$NON-NLS-1$
    private static final String ERROR_IN_CREATING_TEMP_FILE = "Can't create temporary file for XML report"; //$NON-NLS-1$
    private static final String ERROR_IN_OBTAINING_POLICY_FEATURES = "Can't obtain enabled features from policy files"; //$NON-NLS-1$

    private CheckerPanel parent;
    private File policy;

    private File xmlReport;
    private File htmlReport;
    private ConfigManager configManager;
    private ValidationProfile customProfile;
    private BatchSummary batchSummary;

    private List<File> pdfs = new ArrayList<>();

    public TestRunnableWorker(CheckerPanel parent, File policy, File xmlReport, File htmlReport, ConfigManager configManager, ValidationProfile customProfile, BatchSummary batchSummary, File pdf) {
        this.parent = parent;
        this.policy = policy;
        this.xmlReport = xmlReport;
        this.htmlReport = htmlReport;
        this.configManager = configManager;
        this.customProfile = customProfile;
        this.batchSummary = batchSummary;
        this.pdfs.add(pdf);
    }

    @Override
    public void run() {
        try {
            this.xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml"); //$NON-NLS-1$//$NON-NLS-2$
            this.xmlReport.deleteOnExit();

            this.xmlReport = new File("C:\\Users\\duallab\\Desktop\\veraPDFreports\\testReport" + System.currentTimeMillis() + ".xml");

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
}
