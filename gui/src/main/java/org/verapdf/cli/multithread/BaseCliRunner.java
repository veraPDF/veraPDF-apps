/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2025, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * VeraPDF Library GUI is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * VeraPDF Library GUI as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.cli.multithread;

import org.verapdf.cli.CliConstants;
import org.verapdf.cli.VeraPdfCli;
import org.verapdf.processor.reports.ResultStructure;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

public class BaseCliRunner implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(BaseCliRunner.class.getCanonicalName());

    private final String veraPDFStarterPath;

    private final Queue<File> filesToProcess;
    private final List<String> veraPDFParameters;

    private Process process;

    private OutputStream out;

    private Scanner reportScanner;

    private final MultiThreadProcessor multiThreadProcessor;

    BaseCliRunner(MultiThreadProcessor multiThreadProcessor, String veraPDFStarterPath, List<String> veraPDFParameters, Queue<File> filesToProcess) {
        this.multiThreadProcessor = multiThreadProcessor;
        this.filesToProcess = filesToProcess;
        this.veraPDFStarterPath = veraPDFStarterPath;
        this.veraPDFParameters = veraPDFParameters;
    }

    @Override
    public void run() {
        List<String> command = new LinkedList<>();

        command.add(veraPDFStarterPath);
        command.addAll(veraPDFParameters);
        command.add(filesToProcess.poll().getAbsolutePath());

        command = command.stream().map(parameter -> {
            if (parameter.isEmpty()) {
                return "\"\"";
            }
            return parameter;
        }).collect(toList());

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            this.process = pb.start();

            this.out = process.getOutputStream();
            reportScanner = new Scanner(process.getInputStream());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in process", e);
        }
        while (reportScanner.hasNextLine()) {
            multiThreadProcessor.write(getData());

            File file = filesToProcess.poll();

            if (file != null) {
                validateFile(file);
            } else {
                closeProcess();
            }
        }
    }

    private boolean closeProcess() {
        boolean isClosed = false;
        try {
            this.out.write(VeraPdfCli.EXIT.getBytes());
            this.out.write("\n".getBytes());
            this.out.flush();

            process.waitFor();

            isClosed = true;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't close process", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Process interrupted exception", e);
        }
        this.multiThreadProcessor.countDown(CliConstants.ExitCodes.fromValue(process.exitValue()));
        return isClosed;
    }

    private void validateFile(File file) {
        try {
            this.out.write(file.getAbsolutePath().getBytes());
            this.out.write("\n".getBytes());
            this.out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't pass new file to validate", e);
        }
    }

    private ResultStructure getData() {
        return new ResultStructure(new File(reportScanner.nextLine()));
    }
}
