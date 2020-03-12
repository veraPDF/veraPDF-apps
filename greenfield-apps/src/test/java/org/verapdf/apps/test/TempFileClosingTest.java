package org.verapdf.apps.test;

import org.junit.Assert;
import org.junit.Test;
import org.verapdf.apps.GreenfieldCliWrapper;
import org.verapdf.core.VeraPDFException;

import java.io.File;

/**
 * @author Sergey Shemyakov
 */
public class TempFileClosingTest {

    private static final String TEST_FILE = "src/test/resources/tmpFilesTest.pdf";
    private static final String FORMAT_OPTION = "--format";
    private static final String TEXT_VARIANT = "text";
    private static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";
    private static final String NEW_TEMP_DIR_POSTFIX = "test";

    private File initialTempDir;
    private File currentTempDir;
    private int initialFileAmount;

    public void test() throws VeraPDFException {
        File testFile = new File(TEST_FILE);
        switchTempDir();
        initialFileAmount = getAmountOfFiles(currentTempDir);
        try {
            GreenfieldCliWrapper.main(new String[] {testFile.getAbsolutePath(), FORMAT_OPTION, TEXT_VARIANT});
        } catch (VeraPDFException e) {
            System.setProperty(TEMP_DIR_PROPERTY, initialTempDir.getAbsolutePath());
            throw e;
        }
//        // only directory with configs should be left
//        Assert.assertTrue((getAmountOfFiles(currentTempDir)) - initialFileAmount == 1);
//        System.setProperty(TEMP_DIR_PROPERTY, initialTempDir.getAbsolutePath());
    }

    private void switchTempDir() {
        initialTempDir = new File(System.getProperty(TEMP_DIR_PROPERTY));
        currentTempDir = new File(initialTempDir.getAbsolutePath() +
                File.separator + NEW_TEMP_DIR_POSTFIX + System.currentTimeMillis());
        currentTempDir.deleteOnExit();
        if (!currentTempDir.exists()) {
            currentTempDir.mkdir();
        }
        System.setProperty(TEMP_DIR_PROPERTY, currentTempDir.getAbsolutePath());
    }

    private static int getAmountOfFiles(File dir) {
        File[] files = dir.listFiles();
        return files == null ? 0 : files.length;
    }
}
