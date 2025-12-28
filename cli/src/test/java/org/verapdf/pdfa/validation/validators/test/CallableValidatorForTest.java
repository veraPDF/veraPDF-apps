/**
 * This file is part of veraPDF Greenfield Applications, a module of the veraPDF project.
 * Copyright (c) 2015-2025, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Greenfield Applications is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Greenfield Applications as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Greenfield Applications as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
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