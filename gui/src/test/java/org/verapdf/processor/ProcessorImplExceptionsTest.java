package org.verapdf.processor;

import static org.junit.Assert.assertEquals;

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
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.verapdf.processor.config.Config;
import org.verapdf.processor.config.ProcessingType;
import org.verapdf.report.ItemDetails;

/**
 * @author Sergey Shemyakov
 *
 */
@RunWith(Parameterized.class)
public class ProcessorImplExceptionsTest {

	private static final String BAD_PROFILE_PATH = "/org/verapdf/processor/bad-profile.xml";
	private static final String GOOD_PROFILE_PATH = "/org/verapdf/processor/good-profile.xml";
	private static final String GOOD_PDF_PATH = "/org/verapdf/processor/good.pdf";
	private static final String BAD_PDF_PATH = "/org/verapdf/processor/bad.pdf";
	private static ArrayList<Boolean> fixMetadataValues = new ArrayList<>();

	static {
		fixMetadataValues.add(Boolean.TRUE);
		fixMetadataValues.add(Boolean.FALSE);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{BAD_PROFILE_PATH, GOOD_PDF_PATH, Boolean.FALSE,
						ProcessingResult.ValidationSummary.ERROR_IN_VALIDATION,
						ProcessingResult.FeaturesSummary.FEATURES_SUCCEED,
						ProcessingResult.MetadataFixingSummary.ERROR_IN_FIXING,
						ProcessingResult.ReportSummary.REPORT_SUCCEED},
				{GOOD_PROFILE_PATH, BAD_PDF_PATH, Boolean.FALSE,
						ProcessingResult.ValidationSummary.ERROR_IN_VALIDATION,
						ProcessingResult.FeaturesSummary.ERROR_IN_FEATURES,
						ProcessingResult.MetadataFixingSummary.ERROR_IN_FIXING,
						ProcessingResult.ReportSummary.REPORT_SUCCEED},
				{GOOD_PROFILE_PATH, GOOD_PDF_PATH, Boolean.TRUE,
						ProcessingResult.ValidationSummary.FILE_VALID,
						ProcessingResult.FeaturesSummary.FEATURES_SUCCEED,
						ProcessingResult.MetadataFixingSummary.FIXING_SUCCEED,
						ProcessingResult.ReportSummary.ERROR_IN_REPORT}
		});
	}

	@Parameterized.Parameter
	public String profilePath;

	@Parameterized.Parameter(value = 1)
	public String pdfFilePath;

	@Parameterized.Parameter(value = 2)
	public boolean isCorruptedReportStream;

	@Parameterized.Parameter(value = 3)
	public ProcessingResult.ValidationSummary expectedValidationSummary;

	@Parameterized.Parameter(value = 4)
	public ProcessingResult.FeaturesSummary expectedFeaturesSummary;

	@Parameterized.Parameter(value = 5)
	public ProcessingResult.MetadataFixingSummary expectedFixingSummary;

	@Parameterized.Parameter(value = 6)
	public ProcessingResult.ReportSummary expectedReportSummary;

	/**
	 * Test method for
	 * {@link org.verapdf.processor.ProcessorImpl#validate(InputStream, ItemDetails, Config, OutputStream)}
	 * Uses corrupted validation profile, PDF file and report output stream
	 * to check exception handling in this cases.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public final void testValidate() throws
			URISyntaxException, IOException {
		Config config = new Config();
		config.setValidationProfilePath(
				new File(getSystemIndependentPath(profilePath)).toPath());
		File pdf = new File(getSystemIndependentPath(pdfFilePath));
		File xmlReport = File.createTempFile("veraPDF-tempXMLReport", ".xml");
		xmlReport.deleteOnExit();
		ProcessingResult processingResult;
		for(ProcessingType type : ProcessingType.values()) {
			for (Boolean fixMetadata : ProcessorImplExceptionsTest.fixMetadataValues) {
				if (!type.isValidating() && fixMetadata.booleanValue() == true) {    // We can't fix metadata without validation
					continue;
				}
				config.setProcessingType(type);
				config.setFixMetadata(fixMetadata.booleanValue());
				try (InputStream toProcess = new FileInputStream(pdf);
					OutputStream mrrReport = new FileOutputStream(xmlReport)) {
					if(isCorruptedReportStream) {
						mrrReport.close();
					}
					Processor processor = new ProcessorImpl();
					processingResult = processor.validate(toProcess,
							ItemDetails.fromFile(pdf), config, mrrReport);

					ProcessingResult.ValidationSummary expectedValidation = type.isValidating() ?
							expectedValidationSummary :
							ProcessingResult.ValidationSummary.VALIDATION_DISABLED;
					assertEquals(expectedValidation,
							processingResult.getValidationSummary());

					ProcessingResult.FeaturesSummary expectedFeatures = type.isFeatures() ?
							expectedFeaturesSummary :
							ProcessingResult.FeaturesSummary.FEATURES_DISABLED;
					assertEquals(expectedFeatures,
							processingResult.getFeaturesSummary());

					ProcessingResult.MetadataFixingSummary expectedMetadata = fixMetadata.booleanValue() ?
							expectedFixingSummary :
							ProcessingResult.MetadataFixingSummary.FIXING_DISABLED;
					assertEquals(expectedMetadata,
							processingResult.getMetadataFixerSummary());

					assertEquals(expectedReportSummary,
							processingResult.getReportSummary());
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