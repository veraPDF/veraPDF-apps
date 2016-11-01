/**
 * 
 */
package org.verapdf.apps;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.xml.bind.JAXBException;

import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.TaskType;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 31 Oct 2016:09:12:43
 */

public interface ConfigManager {
	public ValidatorConfig getValidatorConfig();
	public FeatureExtractorConfig getFeaturesConfig();
	public MetadataFixerConfig getFixerConfig();
	public ProcessorConfig createProcessorConfig();
	public ProcessorConfig createProcessorConfig(EnumSet<TaskType> tasks);
	public VeraAppConfig getApplicationConfig();
	public File getConfigDir();
	public void updateValidatorConfig(ValidatorConfig config) throws JAXBException, IOException;
	public void updateFeaturesConfig(FeatureExtractorConfig config) throws JAXBException, IOException;
	public void updateFixerConfig(MetadataFixerConfig config) throws JAXBException, IOException;
	public void updateAppConfig(VeraAppConfig config) throws JAXBException, IOException;
}
