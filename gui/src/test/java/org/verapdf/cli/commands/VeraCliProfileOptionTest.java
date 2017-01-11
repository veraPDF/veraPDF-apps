/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
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
/**
 * 
 */
package org.verapdf.cli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@SuppressWarnings("static-method")
public class VeraCliProfileOptionTest {

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test
    public final void testGetProfileFileDefault() {
        // Test empty String[] args doesn't change that
        VeraCliArgs parser = new VeraCliArgParser(new String[] {});
        assertNull(parser.getProfileFile());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser(new String[] { "-l", "--flavour", "1B", "-h" });
        assertNull(parser.getProfileFile());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetProfileFileFlagEmpty() {
    	VeraCliArgs parser = new VeraCliArgParser(new String[] { "-p" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test
    public final void testGetProfileFileFlagEmptyFollowing() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "-p", "-h" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetProfileFileOptionEmpty() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--profile" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test
    public final void testGetProfileFileOptionEmptyFollowing() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--profile" , "-h"});
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test
    public final void testGetProfileFileFlagNotFile() {
        VeraCliArgs parser = new VeraCliArgParser((new String[] { "-p", "*"}));
        assertNotNull(parser.getProfileFile());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test
    public final void testGetProfileFileOptionNotFile() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--profile", "%$3"});
        assertNotNull(parser.getProfileFile());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     * @throws IOException 
     */
    @Test
    public final void testGetProfileFileFlag() throws IOException {
        File testFile = File.createTempFile("test", "xml");

        VeraCliArgs parser = new VeraCliArgParser(new String[] { "-p", testFile.getAbsolutePath()});
        assertNotNull(parser.getProfileFile());
    
        // Test flag works with other options & flags
        parser = new VeraCliArgParser(new String[] { "-p", testFile.getAbsolutePath(), "--format", "text", "-h" });
        assertNotNull(parser.getProfileFile());
}

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     * @throws IOException 
     */
    @Test
    public final void testGetProfileFileOption() throws IOException {
        File testFile = File.createTempFile("test", "xml");
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--profile", testFile.getAbsolutePath()});
        assertNotNull(parser.getProfileFile());

        // Test option works with other options & flags
        parser = new VeraCliArgParser(new String[] { "--profile", testFile.getAbsolutePath(), "--format", "xml", "-h" });
        assertNotNull(parser.getProfileFile());
    }

}
