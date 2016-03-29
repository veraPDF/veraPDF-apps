/**
 *
 */
package org.verapdf.cli;

import org.verapdf.cli.commands.FormatOption;
import org.verapdf.cli.commands.VeraCliArgParser;
import org.verapdf.core.ValidationException;
import org.verapdf.features.pb.PBFeatureParser;
import org.verapdf.features.tools.FeaturesCollection;
import org.verapdf.metadata.fixer.impl.MetadataFixerImpl;
import org.verapdf.metadata.fixer.impl.pb.FixerConfigImpl;
import org.verapdf.metadata.fixer.utils.FileGenerator;
import org.verapdf.metadata.fixer.utils.FixerConfig;
import org.verapdf.model.ModelParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.MetadataFixerResult;
import org.verapdf.pdfa.results.TestAssertion;
import org.verapdf.pdfa.results.TestAssertion.Status;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.pdfa.validation.RuleId;
import org.verapdf.pdfa.validation.ValidationProfile;
import org.verapdf.pdfa.validators.Validators;
import org.verapdf.report.*;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
final class VeraPdfCliProcessor {
    final FormatOption format;
    final boolean extractFeatures;
    final boolean logPassed;
    final boolean recurse;
    final boolean verbose;
    final PDFAValidator validator;

    final int maxFailuresDisplayed;

    final boolean fixMetadata;
    final String prefix;
    final Path saveFolder;

    final String profilesWikiPath;

    final ValidationProfile profile;

    private VeraPdfCliProcessor() throws IOException {
        this(new VeraCliArgParser());
    }

    private VeraPdfCliProcessor(final VeraCliArgParser args)
            throws IOException {
        this.format = args.getFormat();
        this.extractFeatures = args.extractFeatures();
        this.logPassed = args.logPassed();
        this.recurse = args.isRecurse();
        this.verbose = args.isVerbose();
        this.profile = profileFromArgs(args);
        this.validator = (this.profile == Profiles.defaultProfile()) ? null
                : Validators.createValidator(this.profile, logPassed(args), args.maxFailures());

        this.maxFailuresDisplayed = args.maxFailuresDisplayed();

        this.fixMetadata = args.fixMetadata();
        this.prefix = args.prefix();
        this.saveFolder = FileSystems.getDefault().getPath(args.saveFolder());
        this.profilesWikiPath = args.getProfilesWikiPath();
    }

    private static boolean logPassed(final VeraCliArgParser args) {
        return (args.getFormat() != FormatOption.XML) || args.logPassed();
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

    private void processStream(final ItemDetails item,
            final InputStream toProcess) {
        ValidationResult validationResult = null;
        MetadataFixerResult fixerResult = null;
        FeaturesCollection featuresCollection = null;

        long start = System.currentTimeMillis();
        try (ModelParser toValidate = new ModelParser(toProcess, this.profile.getPDFAFlavour())) {
            if (this.validator != null) {
                validationResult = this.validator.validate(toValidate);
                if (this.fixMetadata) {
                    fixerResult = this.fixMetadata(validationResult, toValidate,
                                                   item.getName());
                }
            }
            if (this.extractFeatures) {
                featuresCollection = PBFeatureParser
                        .getFeaturesCollection(toValidate.getPDDocument());
            }
        } catch (IOException e) {
            System.err.println("Error: " + item.getName() + " is not a PDF format file.");
            // TODO : do we need stacktrace in cli application?
            // e.printStackTrace();
        } catch (ValidationException e) {
            System.err.println("Exception raised while validating "
                    + item.getName());
            e.printStackTrace();
        }
        if (this.format == FormatOption.XML) {
            CliReport report = CliReport.fromValues(item, validationResult,
                    FeaturesReport.fromValues(featuresCollection));
            try {
                CliReport.toXml(report, System.out, Boolean.TRUE);
            } catch (JAXBException excep) {
                // TODO Auto-generated catch block
                excep.printStackTrace();
            }
        } else if ((this.format == FormatOption.TEXT) && (validationResult != null)) {
            System.out.println((validationResult.isCompliant() ? "PASS " : "FAIL ") + item.getName());
            if (this.verbose) {
                Set<RuleId> ruleIds = new HashSet<>();
                for (TestAssertion assertion : validationResult.getTestAssertions()) {
                    if (assertion.getStatus() == Status.FAILED) {
                        ruleIds.add(assertion.getRuleId());
                    }
                }
                for (RuleId id : ruleIds) {
                    System.out.println(id.getClause() + "-" + id.getTestNumber());
                }
            }
        } else {
            MachineReadableReport report = MachineReadableReport.fromValues(
                    item.getName(),
                    this.validator == null ? Profiles.defaultProfile()
                            : this.validator.getProfile(), validationResult,
                    this.logPassed, fixerResult, featuresCollection,
                    System.currentTimeMillis() - start, this.maxFailuresDisplayed);
            outputMrr(report, this.format == FormatOption.HTML);
        }
    }

    private void outputMrr(final MachineReadableReport report,
            final boolean toHtml) {
        try {
            if (toHtml) {
                outputMrrAsHtml(report);
            } else {
                MachineReadableReport.toXml(report, System.out, Boolean.TRUE);
            }
        } catch (JAXBException | IOException | TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void outputMrrAsHtml(final MachineReadableReport report)
            throws IOException, JAXBException, TransformerException {
        File tmp = File.createTempFile("verpdf", "xml");
        try (OutputStream os = new FileOutputStream(tmp)) {
            MachineReadableReport.toXml(report, os, Boolean.FALSE);
        }
        try (InputStream is = new FileInputStream(tmp)) {
            HTMLReport.writeHTMLReport(is, System.out, this.profilesWikiPath);
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

    private static ValidationProfile profileFromArgs(final VeraCliArgParser args)
            throws FileNotFoundException, IOException {
        if (args.getProfileFile() == null) {
            return (args.getFlavour() == PDFAFlavour.NO_FLAVOUR) ? Profiles
                    .defaultProfile() : Profiles.getVeraProfileDirectory()
                    .getValidationProfileByFlavour(args.getFlavour());
        }
        ValidationProfile profile = profileFromFile(args.getProfileFile());

        return profile;
    }

    private static ValidationProfile profileFromFile(final File profileFile)
            throws IOException {
        ValidationProfile profile = Profiles.defaultProfile();
        try (InputStream is = new FileInputStream(profileFile)) {
            profile = Profiles.profileFromXml(is);
            if ("sha-1 hash code".equals(profile.getHexSha1Digest())) {
                return Profiles.defaultProfile();
            }
            return profile;
        } catch (JAXBException e) {
            e.printStackTrace();
            return Profiles.defaultProfile();
        }
    }

    private MetadataFixerResult fixMetadata(ValidationResult info,
                                            ModelParser parser, String fileName) throws IOException {
        FixerConfig fixerConfig = FixerConfigImpl.getFixerConfig(
                parser.getPDDocument(), info);
        Path path = this.saveFolder;
        File tempFile = File.createTempFile("fixedTempFile", ".pdf");
        tempFile.deleteOnExit();
        try (OutputStream tempOutput = new BufferedOutputStream(
                new FileOutputStream(tempFile))) {
            MetadataFixerResult fixerResult = MetadataFixerImpl.fixMetadata(
                    tempOutput, fixerConfig);
            MetadataFixerResult.RepairStatus repairStatus = fixerResult
                    .getRepairStatus();
            if (repairStatus == MetadataFixerResult.RepairStatus.SUCCESS || repairStatus == MetadataFixerResult.RepairStatus.ID_REMOVED) {
                File resFile;
                boolean flag = true;
                while (flag) {
                    if (!path.toString().trim().isEmpty()) {
                        resFile = FileGenerator.createOutputFile(this.saveFolder.toFile(),
                                fileName, prefix);
                    } else {
                        resFile = FileGenerator.createOutputFile(new File(fileName),
                                prefix);
                    }

                    try {
                        Files.copy(tempFile.toPath(), resFile.toPath());
                        flag = false;
                    } catch (FileAlreadyExistsException e) {
                        System.err.println(e);
                    }
                }
            }
            return fixerResult;
        }
    }

    /**
     * Checks is the parameter path a valid for saving fixed file
     *
     * @param path path for check
     * @return true if it is valid
     */
    private static boolean isValidFolderPath(Path path) {
        if (path == null) {
            return false;
        }
        File f = path.toFile();
        return path.toString().isEmpty() || (f.isDirectory() && f.canWrite());
    }
}
