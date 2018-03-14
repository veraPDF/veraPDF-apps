package org.verapdf.cli.multithread.reports;

import org.verapdf.cli.multithread.BaseCliRunner;
import org.verapdf.cli.multithread.reports.writer.ReportWriter;

public class MultiThreadProcessingHandlerImpl implements MultiThreadProcessingHandler {
	private ReportWriter reportWriter;

	public MultiThreadProcessingHandlerImpl(ReportWriter reportWriter) {
		this.reportWriter = reportWriter;
	}

	@Override
	public void startReport() {
		reportWriter.startDocument();
	}

	@Override
	public void fillReport(BaseCliRunner.ResultStructure result) {
		reportWriter.write(result);
	}

	@Override
	public void endReport() {
		reportWriter.endDocument();
		reportWriter.closeOutputStream();
	}
}
