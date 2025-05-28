/**
 * This file is part of veraPDF Greenfield Applications, a module of the veraPDF project.
 * Copyright (c) 2015-2025, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Greenfield Applications is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Greenfield Applications as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Greenfield Applications as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.apps.test;

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
        switchTempDir();
        initialFileAmount = getAmountOfFiles(currentTempDir);
        try {
            File testFile = new File(TEST_FILE);
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
