package org.verapdf.apps.test;

import org.junit.Before;
import org.junit.Test;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.validation.validators.test.CallableValidatorForTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;

public class MultithreadingTest {
	private static final String TEST_FILE = "src/test/resources/veraPDFtest-pass-a.pdf";

	@Before
	public void init() {
		VeraGreenfieldFoundryProvider.initialise();
	}

	@Test
	public void shouldCreateXmlReportsInGivenDirectory() throws Exception {
		File fileToValidate = new File(TEST_FILE);

		int numberOfThreads = 4;

		List<Future<List<ValidationResult>>> futureResult = startValidation(fileToValidate, numberOfThreads);
		List<ValidationResult> validationResults = getValidationResult(futureResult);

		boolean isExpectedResults = compareResultsFromDifferentThreads(validationResults);

		assertTrue(isExpectedResults);
	}

	private boolean compareResultsFromDifferentThreads(List<ValidationResult> validationResults) {
		boolean isExpectedResults = true;
		for (ValidationResult result : validationResults) {
			for (ValidationResult comparingResult : validationResults) {
				boolean isCompliantResultsEquals = result.isCompliant() == comparingResult.isCompliant();
				boolean isTestAssertionsEquals = result.getTestAssertions().equals(comparingResult.getTestAssertions());
				boolean isTotalAssertionsEquals = result.getTotalAssertions() == comparingResult.getTotalAssertions();
				if (!isCompliantResultsEquals || !isTestAssertionsEquals || !isTotalAssertionsEquals) {
					isExpectedResults = false;
				}
			}
		}
		return isExpectedResults;
	}

	private List<ValidationResult> getValidationResult(List<Future<List<ValidationResult>>> results) throws InterruptedException, java.util.concurrent.ExecutionException {
		List<ValidationResult> validationResults = new ArrayList<>();
		for (Future<List<ValidationResult>> result : results) {
			List<ValidationResult> validationResult = result.get();
			validationResults.addAll(validationResult);
		}
		return validationResults;
	}

	private List<Future<List<ValidationResult>>> startValidation(File fileToValidate, int numberOfThreads) {
		List<Future<List<ValidationResult>>> futureResult = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		for (int i = 0; i < numberOfThreads; i++) {
			CallableValidatorForTest validator = new CallableValidatorForTest(fileToValidate);
			Future<List<ValidationResult>> submit = executor.submit(validator);
			futureResult.add(submit);
		}
		return futureResult;
	}
}
