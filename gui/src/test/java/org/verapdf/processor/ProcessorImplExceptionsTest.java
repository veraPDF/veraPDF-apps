package org.verapdf.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

import org.verapdf.processor.config.Config;
import org.verapdf.processor.config.ProcessingType;
import org.verapdf.report.ItemDetails;

/**
 * @author Sergey Shemyakov
 *
 */
@SuppressWarnings("static-method")
public class ProcessorImplExceptionsTest {

	private static final String BAD_PROFILE_PATH = "/org/verapdf/processor/bad-profile.xml";
	private static final String GOOD_PROFILE_PATH = "/org/verapdf/processor/good-profile.xml";
	private static final String GOOD_PDF_PATH = "/org/verapdf/processor/good.pdf";
	private static final String BAD_PDF_PATH = "/org/verapdf/processor/bad.pdf";
	private static ArrayList<Boolean> fixMetadataValues = new ArrayList<>();

	static {
		fixMetadataValues.add(true);
		fixMetadataValues.add(false);
	}
	/**
	 * Test method for
	 * {@link org.verapdf.processor.ProcessorImpl#validate(InputStream, ItemDetails, Config, OutputStream)}
	 * Uses corrupted validation profile to check exception processing in this case.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public final void testValidateWithInvalidProfile() throws
			URISyntaxException, IOException {
		Config config = new Config();
		config.setValidationProfilePath(
				new File(getSystemIndependentPath(BAD_PROFILE_PATH)).toPath());
		File pdf = new File(getSystemIndependentPath(GOOD_PDF_PATH));
		File xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
		xmlReport.deleteOnExit();
		ProcessingResult processingResult;
		for(ProcessingType type : ProcessingType.values()) {
			for (Boolean fixMetadata : this.fixMetadataValues) {
				if (!type.isValidating() && fixMetadata == true) {    // We can't fix metadata without validation
					continue;
				}
				config.setProcessingType(type);
				config.setFixMetadata(fixMetadata);
				try (InputStream toProcess = new FileInputStream(pdf);
					OutputStream mrrReport = new FileOutputStream(xmlReport)) {
					Processor processor = new ProcessorImpl();
					processingResult = processor.validate(toProcess,
							ItemDetails.fromFile(pdf), config, mrrReport);

					ProcessingResult.ValidationSummary expectedValidation = type.isValidating() ?
							ProcessingResult.ValidationSummary.ERROR_IN_VALIDATION :
							ProcessingResult.ValidationSummary.VALIDATION_DISABLED;
					assertEquals(processingResult.getValidationSummary(),
							expectedValidation);

					ProcessingResult.FeaturesSummary expectedFeatures = type.isFeatures() ?
							ProcessingResult.FeaturesSummary.FEATURES_SUCCEED :
							ProcessingResult.FeaturesSummary.FEATURES_DISABLED;
					assertEquals(processingResult.getFeaturesSummary(),
							expectedFeatures);

					ProcessingResult.MetadataFixingSummary expectedMetadata = fixMetadata ?
							ProcessingResult.MetadataFixingSummary.ERROR_IN_FIXING :
							ProcessingResult.MetadataFixingSummary.FIXING_DISABLED;
					assertEquals(processingResult.getMetadataFixerSummary(),
							expectedMetadata);
				}
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.verapdf.processor.ProcessorImpl#validate(InputStream, ItemDetails, Config, OutputStream)}
	 * Uses corrupted PDF file to check exception processing in this case.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public final void testValidateWithCorruptedPdfFile() throws
			URISyntaxException, IOException {
		Config config = new Config();
		config.setValidationProfilePath(
				new File(getSystemIndependentPath(GOOD_PROFILE_PATH)).toPath());
		File pdf = new File(getSystemIndependentPath(BAD_PDF_PATH));
		File xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
		xmlReport.deleteOnExit();
		ProcessingResult processingResult;
		for(ProcessingType type : ProcessingType.values()) {
			for (Boolean fixMetadata : this.fixMetadataValues) {
				if(!type.isValidating() && fixMetadata == true) {	// We can't fix metadata without validation
					continue;
				}
				config.setProcessingType(type);
				config.setFixMetadata(fixMetadata);
				try (InputStream toProcess = new FileInputStream(pdf);
					OutputStream mrrReport = new FileOutputStream(xmlReport)) {
					Processor processor = new ProcessorImpl();
					processingResult = processor.validate(toProcess,
							ItemDetails.fromFile(pdf), config, mrrReport);

					ProcessingResult.ValidationSummary expectedValidation = type.isValidating() ?
							ProcessingResult.ValidationSummary.ERROR_IN_VALIDATION :
							ProcessingResult.ValidationSummary.VALIDATION_DISABLED;
					assertEquals(processingResult.getValidationSummary(),
							expectedValidation);

					ProcessingResult.FeaturesSummary expectedFeatures = type.isFeatures() ?
							ProcessingResult.FeaturesSummary.ERROR_IN_FEATURES :
							ProcessingResult.FeaturesSummary.FEATURES_DISABLED;
					assertEquals(processingResult.getFeaturesSummary(),
							expectedFeatures);

					ProcessingResult.MetadataFixingSummary expectedMetadata = fixMetadata ?
							ProcessingResult.MetadataFixingSummary.ERROR_IN_FIXING :
							ProcessingResult.MetadataFixingSummary.FIXING_DISABLED;
					assertEquals(processingResult.getMetadataFixerSummary(),
							expectedMetadata);
				}
			}
		}
	}


	private static String getSystemIndependentPath(String path)
			throws URISyntaxException {
		URL resourceUrl = ClassLoader.class.getResource(path);
		Path resourcePath = Paths.get(resourceUrl.toURI());
		return resourcePath.toString();
	}
}