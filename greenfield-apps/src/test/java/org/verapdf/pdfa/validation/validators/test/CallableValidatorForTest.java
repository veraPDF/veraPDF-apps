package org.verapdf.pdfa.validation.validators.test;

import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.results.ValidationResult;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

public class CallableValidatorForTest implements Callable<ValidationResult> {
	private File fileToValidate;

	public CallableValidatorForTest(File fileToValidate) {
		this.fileToValidate = fileToValidate;
	}

	@Override
	public ValidationResult call() throws Exception {
		ValidationResult result;
		try (FileInputStream fis = new FileInputStream(this.fileToValidate);
				PDFAParser parser = Foundries.defaultInstance().createParser(fis); 
				PDFAValidator validator = Foundries.defaultInstance().createValidator(parser.getFlavour(), false)) {
			result = validator.validate(parser);
		}
		return result;
	}
}