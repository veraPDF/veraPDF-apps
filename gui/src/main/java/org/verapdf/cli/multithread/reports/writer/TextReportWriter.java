package org.verapdf.cli.multithread.reports.writer;

import org.verapdf.cli.multithread.BaseCliRunner;
import java.io.*;

public class TextReportWriter extends ReportWriter {

	protected TextReportWriter(OutputStream os, OutputStream errorStream) {
		super(os, errorStream);
	}

	@Override
	public void write(BaseCliRunner.ResultStructure result) {
		merge(result.getReportFile(), os);
		deleteTemp(result);
	}

	@Override
	public void startDocument() {
		//NOP
	}

	@Override
	public void endDocument() {
		//NOP
	}
}
