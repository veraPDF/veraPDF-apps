package org.verapdf.pdfa.validation.validators.test;

import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.ValidationResult;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableValidatorForTest implements Callable<List<ValidationResult>> {
	private final File fileToValidate;

	public CallableValidatorForTest(File fileToValidate) {
		this.fileToValidate = fileToValidate;
	}

	@Override
	public List<ValidationResult> call() throws Exception {
		List<ValidationResult> result;
		try (FileInputStream fis = new FileInputStream(this.fileToValidate);
				PDFAParser parser = Foundries.defaultInstance().createParser(fis); 
				PDFAValidator validator = Foundries.defaultInstance().createValidator(PDFAFlavour.ARLINGTON1_4, false)) {
			result = validator.validateAll(parser);
		}
		return result;
	}
}