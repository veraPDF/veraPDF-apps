/**
 * 
 */
package org.verapdf.cli.commands;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.verapdf.apps.Applications;
import org.verapdf.apps.ProcessType;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 11 Jan 2017:02:13:28
 */

public class VeraConfigGeneratorImpl implements VeraConfigGenerator {

	/**
	 * @see org.verapdf.cli.commands.VeraConfigGenerator#appConfigFromArgs(org.verapdf.cli.commands.VeraCliArgs)
	 */
	@Override
	public VeraAppConfig appConfigFromArgs(VeraCliArgs cliArgs, VeraAppConfig base) {
		Applications.Builder configBuilder = Applications.Builder.fromConfig(base);
		configBuilder.format(cliArgs.getFormat()).isVerbose(cliArgs.isVerbose()).fixerFolder(cliArgs.saveFolder());
		configBuilder.type(typeFromArgs(cliArgs));
		return configBuilder.build();
	}

	/**
	 * @see org.verapdf.cli.commands.VeraConfigGenerator#validatorConfigFromArgs(org.verapdf.cli.commands.VeraCliArgs)
	 */
	@Override
	public ValidatorConfig validatorConfigFromArgs(VeraCliArgs cliArgs) {
		return ValidatorFactory.createConfig(cliArgs.getFlavour(), cliArgs.logPassed(), cliArgs.maxFailures());
	}

	/**
	 * @see org.verapdf.cli.commands.VeraConfigGenerator#fixerConfigFromArgs(org.verapdf.cli.commands.VeraCliArgs)
	 */
	@Override
	public MetadataFixerConfig fixerConfigFromArgs(VeraCliArgs cliArgs) {
		return FixerFactory.configFromValues(cliArgs.prefix(), true);
	}

	/**
	 * @see org.verapdf.cli.commands.VeraConfigGenerator#processorConfigFromArgs(org.verapdf.cli.commands.VeraCliArgs)
	 */
	@Override
	public ProcessorConfig processorConfigFromArgs(VeraCliArgs cliArgs, ProcessType procType,
			FeatureExtractorConfig featConfig) throws VeraPDFException {
		if (cliArgs.getProfileFile() == null) {
			return ProcessorFactory.fromValues(this.validatorConfigFromArgs(cliArgs), featConfig,
					this.fixerConfigFromArgs(cliArgs), procType.getTasks(), cliArgs.saveFolder());
		}
		try (InputStream fis = new FileInputStream(cliArgs.getProfileFile())) {
			ValidationProfile customProfile = Profiles.profileFromXml(fis);
			return ProcessorFactory.fromValues(this.validatorConfigFromArgs(cliArgs), featConfig,
					this.fixerConfigFromArgs(cliArgs), procType.getTasks(), customProfile, cliArgs.saveFolder());
		} catch (IOException | JAXBException excep) {
			throw new VeraPDFException("Problem loading custom profile", excep);
		}
	}

	private static ProcessType typeFromArgs(VeraCliArgs parser) {
		ProcessType retVal = (parser.isValidationOff() && !parser.isPolicy()) ? ProcessType.NO_PROCESS
				: ProcessType.VALIDATE;
		if (parser.extractFeatures() || parser.isPolicy())
			retVal = ProcessType.addProcess(retVal, ProcessType.EXTRACT);
		if (parser.fixMetadata())
			retVal = ProcessType.addProcess(retVal, ProcessType.FIX);
		return retVal;
	}

}
