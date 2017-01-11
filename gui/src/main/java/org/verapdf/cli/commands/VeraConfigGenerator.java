/**
 * 
 */
package org.verapdf.cli.commands;

import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.processor.ProcessorConfig;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1
 * 
 * Created 11 Jan 2017:02:07:59
 */

public interface VeraConfigGenerator {
	public VeraAppConfig appConfigFromArgs(VeraCliArgs cliArgs, VeraAppConfig base);
	public ValidatorConfig validatorConfigFromArgs(VeraCliArgs cliArgs);
	public MetadataFixerConfig fixerConfigFromArgs(VeraCliArgs cliArgs);
	public ProcessorConfig processorConfigFromArgs(VeraCliArgs cliArgsfinal, ProcessType procType, FeatureExtractorConfig featConfig) throws VeraPDFException;
}
