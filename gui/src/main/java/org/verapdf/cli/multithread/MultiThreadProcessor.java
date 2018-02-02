package org.verapdf.cli.multithread;

import javafx.application.Application;
import org.verapdf.apps.Applications;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.cli.multithread.reports.MultiThreadProcessingHandler;
import org.verapdf.cli.multithread.reports.MultiThreadProcessingHandlerImpl;
import org.verapdf.cli.multithread.reports.writer.ReportWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadProcessor {
    private static final Logger LOGGER = Logger.getLogger(MultiThreadProcessor.class.getCanonicalName());

    private final Queue<File> filesToProcess;

    private int filesQuantity;

    private File veraPDFStarterPath;
    private List<String> veraPDFParameters;
    private OutputStream os;
    private OutputStream errorStream;

    private ReportWriter reportWriter;
    private MultiThreadProcessingHandler processingHandler;

    private boolean isFirstReport = true;

    private MultiThreadProcessor(VeraCliArgParser cliArgParser) {
        this.os = System.out;
        this.errorStream = System.err;

        File veraPDFPath = cliArgParser.getVeraCLIPath();
        if (veraPDFPath == null || !veraPDFPath.isFile()) {
            try {
                this.veraPDFStarterPath = Applications.getVeraScriptFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can't obtain veraPDF CLI script path", e);
            }
        } else {
            this.veraPDFStarterPath = veraPDFPath;
        }
        if (this.veraPDFStarterPath == null || !this.veraPDFStarterPath.isFile()) {
            throw new IllegalStateException("Can't obtain executable veraPDF CLI script path");
        }
        this.veraPDFParameters = VeraCliArgParser.getBaseVeraPDFParameters(cliArgParser);
        this.filesToProcess = new ConcurrentLinkedQueue<>();
        this.filesToProcess.addAll(getFiles(cliArgParser.getPdfPaths(), cliArgParser.isRecurse()));
        this.filesQuantity = filesToProcess.size();

        ReportWriter.OutputFormat outputFormat = getOutputFormat(cliArgParser.getFormat().getOption());
        this.reportWriter = ReportWriter.newInstance(os, outputFormat, errorStream);
        this.processingHandler = new MultiThreadProcessingHandlerImpl(reportWriter);
    }

    private ReportWriter.OutputFormat getOutputFormat(String outputFormat) {
        return ReportWriter.OutputFormat.getOutputFormat(outputFormat);
    }

    public static void process(VeraCliArgParser cliArgParser) {
        MultiThreadProcessor processor = new MultiThreadProcessor(cliArgParser);
        processor.startProcesses(cliArgParser.getNumberOfProcesses());
    }

    public synchronized void write(BaseCliRunner.ResultStructure result) {
        if (isFirstReport) {
            processingHandler.startReport();
            processingHandler.fillReport(result);
            isFirstReport = false;
        } else {
            processingHandler.fillReport(result);
        }

        this.filesQuantity--;

        if (filesQuantity == 0) {
            processingHandler.endReport();
        }
    }

    private List<File> getFiles(List<String> pdfPaths, boolean isRecurse) {
        List<File> toFilter = new ArrayList<>(pdfPaths.size());
        pdfPaths.forEach(path -> toFilter.add(new File(path)));

        return ApplicationUtils.filterPdfFiles(toFilter, isRecurse);
    }

    private void startProcesses(int numberOfProcesses) {
        int processesQuantity = Math.min(numberOfProcesses, filesToProcess.size());
        for (int i = 0; i < processesQuantity; i++) {
            BaseCliRunner veraPDFRunner = new BaseCliRunner(this, veraPDFStarterPath.getAbsolutePath(), veraPDFParameters, filesToProcess);
            new Thread(veraPDFRunner).start();
        }
    }
}
