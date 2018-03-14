package org.verapdf.cli.multithread.reports;

import org.verapdf.cli.multithread.BaseCliRunner;

public interface MultiThreadProcessingHandler {
	void startReport();

	void fillReport(BaseCliRunner.ResultStructure result);

	void endReport();
}
