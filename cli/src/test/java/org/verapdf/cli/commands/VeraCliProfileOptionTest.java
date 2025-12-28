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
/**
 * 
 */
package org.verapdf.cli.commands;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import static org.junit.Assert.*;

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
        assertNull(VeraCliArgParser.DEFAULT_ARGS.getProfileFile());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse();
        assertSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS.getProfileFile());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse("-l", "--flavour", "1B", "-h");
        assertSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS.getProfileFile());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected = ParameterException.class)
    public final void testGetProfileFileFlagEmpty() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-p");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected = ParameterException.class)
    public final void testGetProfileFileFlagEmptyFollowing() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-p", "-h");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected = ParameterException.class)
    public final void testGetProfileFileOptionEmpty() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("--profile");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected = ParameterException.class)
    public final void testGetProfileFileOptionEmptyFollowing() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("--profile", "-h");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected = ParameterException.class)
    public final void testGetProfileFileFlagNotFile() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-p", "*");
        assertNotSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS.getProfileFile());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     */
    @Test(expected = ParameterException.class)
    public final void testGetProfileFileOptionNotFile() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("--profile", "%$3");
        assertNotSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS.getProfileFile());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     * @throws IOException 
     */
    @Test
    public final void testGetProfileFileFlag() throws IOException {
        File testFile = File.createTempFile("test", "xml");

        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-p", testFile.getAbsolutePath());
        assertNotSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS
                .getProfileFile());
    
        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse("-p", testFile.getAbsolutePath(), "--format", "text", "-h");
        assertNotSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS
                .getProfileFile());
        testFile.delete();
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getProfileFile()}.
     * @throws IOException 
     */
    @Test
    public final void testGetProfileFileOption() throws IOException {
        File testFile = File.createTempFile("test", "xml");
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--profile", testFile.getAbsolutePath());
        assertNotSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS.getProfileFile());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander
                .parse("--profile", testFile.getAbsolutePath(), "--format", "xml", "-h");
        assertNotSame(parser.getProfileFile(), VeraCliArgParser.DEFAULT_ARGS.getProfileFile());
        testFile.delete();
    }

}
