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

import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.cli.FormatterHelper;
import org.verapdf.processor.TaskType;

import com.beust.jcommander.JCommander;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@SuppressWarnings("static-method")
public class VeraCliArgParserTest {
    private final static String APP_NAME = VeraCliArgParserTest.class.getName();

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#showVersion()}.
     */
    @Test
    public final void testVersionDefault() {
        // Test default is false
        assertFalse(VeraCliArgParser.DEFAULT_ARGS.showVersion());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);
        jCommander.parse();
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.showVersion(), parser.showVersion());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "-h", "--success");
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.showVersion(), parser.showVersion());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#showVersion()}.
     */
    @Test
    public final void testVersionOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--version");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.showVersion(), parser.showVersion());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("--version", "-l", "--passed");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.showVersion(), parser.showVersion());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#listProfiles()}.
     */
    @Test
    public final void testListDefault() {
        // Test default is false
        assertFalse(VeraCliArgParser.DEFAULT_ARGS.listProfiles());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);
        jCommander.parse();
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.listProfiles(), parser.listProfiles());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("--version", "-h", "--success");
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.listProfiles(), parser.listProfiles());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#listProfiles()}.
     */
    @Test
    public final void testListFlag() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-l");
        assertNotEquals(parser.listProfiles(), VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("--version", "-l", "--passed", "-h");
        assertNotEquals(parser.listProfiles(), VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#listProfiles()}.
     */
    @Test
    public final void testListOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--list");
        assertNotEquals(parser.listProfiles(), VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-h", "--list", "--passed");
        assertNotEquals(parser.listProfiles(), VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#isHelp()}.
     */
    @Test
    public final void testHelpDefault() {
        // Test default is false
        assertFalse(VeraCliArgParser.DEFAULT_ARGS.isHelp());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);
        jCommander.parse();
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.isHelp(), parser.isHelp());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--version", "--success");
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.isHelp(), parser.isHelp());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#isHelp()}.
     */
    @Test
    public final void testHelpFlag() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-h");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.isHelp(), parser.isHelp());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--format", "mrr", "-h");
        assertNotEquals(parser.isHelp(), VeraCliArgParser.DEFAULT_ARGS.isHelp());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#isHelp()}.
     */
    @Test
    public final void testHelpOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--help");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.isHelp(), parser.isHelp());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--format", "xml", "--help");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.isHelp(), parser.isHelp());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#logPassed()}.
     */
    @Test
    public final void testLogPassedDefault() {
        // Test default is false
        assertFalse(VeraCliArgParser.DEFAULT_ARGS.logPassed());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);
        jCommander.parse();
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.logPassed(), parser.logPassed());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--flavour", "1a", "--version",
                "-h");
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.logPassed(), parser.logPassed());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#logPassed()}.
     */
    @Test
    public final void testLogPassedOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--passed");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS
                .logPassed(), parser.logPassed());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--passed", "--flavour", "3b");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.logPassed(), parser.logPassed());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#logPassed()}.
     */
    @Test
    public final void testLogPassedSuccessOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--success");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.logPassed(), parser.logPassed());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--success", "--flavour", "3b");
        assertNotEquals(VeraCliArgParser.DEFAULT_ARGS.logPassed(), parser.logPassed());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
     */
    @Test
    public final void testExtractDefault() {
        // Test default is false
        assertFalse(VeraCliArgParser.DEFAULT_ARGS.extractFeatures());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);
        jCommander.parse();
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.extractFeatures(), parser.extractFeatures());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--flavour", "1a", "-h");
        assertEquals(VeraCliArgParser.DEFAULT_ARGS.extractFeatures(), parser.extractFeatures());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
     */
    @Test
    public final void testExtractFlag() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-x");
        assertNotEquals(parser.extractFeatures(), VeraCliArgParser.DEFAULT_ARGS.extractFeatures());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-x", "--format", "xml", "-h");
        assertNotEquals(parser.extractFeatures(), VeraCliArgParser.DEFAULT_ARGS.extractFeatures());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#extractFeatures()}.
     */
    @Test
    public final void testExtractOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test option works
        jCommander.parse("--extract");
        assertNotEquals(parser.extractFeatures(), VeraCliArgParser.DEFAULT_ARGS.extractFeatures());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse("-l", "--extract", "--flavour", "3b");
        assertNotEquals(parser.extractFeatures(), VeraCliArgParser.DEFAULT_ARGS.extractFeatures());
    }

    @Test
    public final void testProcessTypeParsing() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test flag works
        jCommander.parse();
        VeraAppConfig config = parser.appConfig(Applications.defaultConfig());
        assertFalse(parser.isValidationOff());
        assertTrue(config.getProcessType().getTasks().contains(TaskType.VALIDATE));

        // Test flag works
        jCommander.parse("-o");
        config = parser.appConfig(Applications.defaultConfig());
        assertTrue(parser.isValidationOff());
        assertFalse(config.getProcessType().getTasks().contains(TaskType.VALIDATE));

        // Test flag works
        jCommander.parse("--off");
        config = parser.appConfig(Applications.defaultConfig());
        assertFalse(parser.isValidationOff());
        assertTrue(config.getProcessType().getTasks().contains(TaskType.VALIDATE));
    }

    static final JCommander initialiseJCommander(final VeraCliArgParser parser) {
        JCommander jCommander = new JCommander(parser);
        jCommander.setUsageFormatter(new FormatterHelper(jCommander));
        jCommander.setProgramName(APP_NAME);
        jCommander.setAllowParameterOverwriting(true);
        return jCommander;
    }

}
