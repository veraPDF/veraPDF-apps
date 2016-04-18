/**
 *
 */
package org.verapdf.cli;

import org.apache.log4j.Logger;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.processor.Processor;
import org.verapdf.processor.ProcessorImpl;
import org.verapdf.processor.config.ConfigIO;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.processor.config.Config;
import org.verapdf.processor.config.ProcessingType;
import org.verapdf.report.*;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.List;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
final class VeraPdfCliProcessor {

	private static final Logger LOGGER = Logger.getLogger(VeraPdfCliProcessor.class);

    final boolean recurse;

    private Config config;

    private VeraPdfCliProcessor() throws IOException {
        this(new VeraCliArgParser());
    }

	public Config getConfig() {
		return this.config;
	}

    private VeraPdfCliProcessor(final VeraCliArgParser args)
            throws IOException {
		this.recurse = args.isRecurse();


		if (args.isLoadingConfig()) {
			try {
				config = ConfigIO.readConfig();
			} catch (IOException e) {
				LOGGER.error("Can not read config file", e);
				this.config = new Config();
			} catch (JAXBException e) {
				LOGGER.error("Cannot parse config XML", e);
				this.config = new Config();
			}
		} else {
			config = new Config();
			config.setShowPassedRules(args.logPassed());
			config.setMaxNumberOfFailedChecks(args.maxFailures());
			config.setMaxNumberOfDisplayedFailedChecks(args.maxFailuresDisplayed());
			config.setMetadataFixerPrefix(args.prefix());
			config.setFixMetadataPathFolder(FileSystems.getDefault().getPath(args.saveFolder()));
			config.setProfileWikiPath(args.getProfilesWikiPath());
			config.setFixMetadata(args.fixMetadata());
			config.setProcessingType(processingTypeFromArgs(args));
			config.setReportType(args.getFormat());
            config.setValidationProfilePath(
					args.getProfileFile() == null ? null : args.getProfileFile().toPath());
            config.setFlavour(args.getFlavour());
            config.setVerboseCli(args.isVerbose());
		}
    }

	static ProcessingType processingTypeFromArgs(final VeraCliArgParser args) {
		if(args.getFlavour() != PDFAFlavour.NO_FLAVOUR &&
				args.extractFeatures()) {
			return ProcessingType.VALIDATING_AND_FEATURES;
		} else if(args.getFlavour() == PDFAFlavour.NO_FLAVOUR
				&& args.extractFeatures()) {
			return ProcessingType.FEATURES;
		} else if(args.getFlavour() != PDFAFlavour.NO_FLAVOUR &&
				!args.extractFeatures()) {
			return ProcessingType.VALIDATING;
		} else throw new IllegalArgumentException("Processing type is not chosen");
	}

    void processPaths(final List<String> pdfPaths) {
        // If the path list is empty then
        if (pdfPaths.isEmpty()) {
            ItemDetails item = ItemDetails.fromValues("STDIN");
            processStream(item, System.in);
        }

        for (String pdfPath : pdfPaths) {
            File file = new File(pdfPath);
            if (file.isDirectory()) {
                processDir(file);
            } else {
                processFile(file);
            }
        }
    }

    static VeraPdfCliProcessor createProcessorFromArgs(
            final VeraCliArgParser args) throws FileNotFoundException,
            IOException {
        return new VeraPdfCliProcessor(args);
    }

    private void processDir(final File dir) {
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                int extIndex = file.getName().lastIndexOf(".");
                String ext = file.getName().substring(extIndex + 1);
                if ("pdf".equalsIgnoreCase(ext)) {
                    processFile(file);
                }
            } else if (file.isDirectory()) {
                if (this.recurse) {
                    processDir(file);
                }
            }
        }
    }

    private void processFile(final File pdfFile) {
        if (checkFileCanBeProcessed(pdfFile)) {
            try (InputStream toProcess = new FileInputStream(pdfFile)) {
                processStream(ItemDetails.fromFile(pdfFile), toProcess);
            } catch (IOException e) {
                System.err.println("Exception raised while processing "
                        + pdfFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

	private static boolean checkFileCanBeProcessed(final File file) {
		if (!file.isFile()) {
			System.err.println("Path " + file.getAbsolutePath()
					+ " is not an existing file.");
			return false;
		} else if (!file.canRead()) {
			System.err.println("Path " + file.getAbsolutePath()
					+ " is not a readable file.");
			return false;
		}
		return true;
	}

    private void processStream(final ItemDetails item,
            final InputStream toProcess) {
        Processor processor = new ProcessorImpl();
		processor.validate(toProcess, item, this.config, System.out);
    }

}
