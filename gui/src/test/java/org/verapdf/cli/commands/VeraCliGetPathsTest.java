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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.beust.jcommander.JCommander;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@SuppressWarnings("static-method")
public class VeraCliGetPathsTest {

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getPdfPaths()}.
     */
    @Test
    public final void testGetPathsDefault() {
        // Test default is empty list
        assertTrue(VeraCliArgParser.DEFAULT_ARGS.getPdfPaths().isEmpty());

        // Test that empty args don't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest
                .initialiseJCommander(parser);
        jCommander.parse(new String[] {});
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.getPdfPaths(),
                parser.getPdfPaths());

        // Test other flags & options don't change that
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--success", "--format", "xml",
                "-h" });
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.getPdfPaths(),
                parser.getPdfPaths());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getPdfPaths()}.
     */
    @Test
    public final void testGetPaths() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse(new String[] { "path 1", "path 2", "path 3" });
        assertNotEquals(parser.getPdfPaths(), VeraCliArgParser.DEFAULT_ARGS
                .getPdfPaths());
    
        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--success", "--format", "mrr",
                "-h", "path 1", "path 2", "path 3" });
        assertNotEquals(parser.getPdfPaths(), VeraCliArgParser.DEFAULT_ARGS
                .getPdfPaths());
    }

}
