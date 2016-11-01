/**
 * 
 */
package org.verapdf.apps;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.verapdf.processor.FormatOption;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 30 Oct 2016:20:58:16
 */
@XmlJavaTypeAdapter(VeraAppConfigImpl.Adapter.class)
public interface VeraAppConfig {
	public boolean isOverwriteReport();
	public String getFixesFolder();
	public String getPluginsFolder();
	public String getReportFile();
	public String getReportFolder();
	public String getPolicyFile();
	public String getWikiPath();
	public FormatOption getFormat();
	public ProcessType getProcessType();
	public int getMaxFailsDisplayed();
}
