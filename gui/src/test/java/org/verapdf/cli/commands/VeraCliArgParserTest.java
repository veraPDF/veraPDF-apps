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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.verapdf.apps.Applications;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.cli.FormatterHelper;
import org.verapdf.processor.TaskType;

import com.beust.jcommander.JCommander;

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
        jCommander.parse(new String[] {});
        assertTrue(parser.showVersion() == VeraCliArgParser.DEFAULT_ARGS
                .showVersion());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "-h", "--success" });
        assertTrue(parser.showVersion() == VeraCliArgParser.DEFAULT_ARGS
                .showVersion());
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
        jCommander.parse(new String[] { "--version" });
        assertFalse(parser.showVersion() == VeraCliArgParser.DEFAULT_ARGS
                .showVersion());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "--version", "-l", "--passed" });
        assertFalse(parser.showVersion() == VeraCliArgParser.DEFAULT_ARGS
                .showVersion());
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
        jCommander.parse(new String[] {});
        assertTrue(parser.listProfiles() == VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "--version", "-h", "--success" });
        assertTrue(parser.listProfiles() == VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());
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
        jCommander.parse(new String[] { "-l" });
        assertFalse(parser.listProfiles() == VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "--version", "-l", "--passed", "-h" });
        assertFalse(parser.listProfiles() == VeraCliArgParser.DEFAULT_ARGS
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
        jCommander.parse(new String[] { "--list" });
        assertFalse(parser.listProfiles() == VeraCliArgParser.DEFAULT_ARGS
                .listProfiles());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-h", "--list", "--passed" });
        assertFalse(parser.listProfiles() == VeraCliArgParser.DEFAULT_ARGS
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
        jCommander.parse(new String[] {});
        assertTrue(parser.isHelp() == VeraCliArgParser.DEFAULT_ARGS.isHelp());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--version", "--success" });
        assertTrue(parser.isHelp() == VeraCliArgParser.DEFAULT_ARGS.isHelp());
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
        jCommander.parse(new String[] { "-h" });
        assertFalse(parser.isHelp() == VeraCliArgParser.DEFAULT_ARGS.isHelp());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--format", "mrr", "-h" });
        assertFalse(parser.isHelp() == VeraCliArgParser.DEFAULT_ARGS.isHelp());
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
        jCommander.parse(new String[] { "--help" });
        assertFalse(parser.isHelp() == VeraCliArgParser.DEFAULT_ARGS.isHelp());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--format", "xml", "--help" });
        assertFalse(parser.isHelp() == VeraCliArgParser.DEFAULT_ARGS.isHelp());
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
        jCommander.parse(new String[] {});
        assertTrue(parser.logPassed() == VeraCliArgParser.DEFAULT_ARGS
                .logPassed());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--flavour", "1a", "--version",
                "-h" });
        assertTrue(parser.logPassed() == VeraCliArgParser.DEFAULT_ARGS
                .logPassed());
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
        jCommander.parse(new String[] { "--passed" });
        assertFalse(parser.logPassed() == VeraCliArgParser.DEFAULT_ARGS
                .logPassed());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--passed", "--flavour", "3b" });
        assertFalse(parser.logPassed() == VeraCliArgParser.DEFAULT_ARGS
                .logPassed());
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
        jCommander.parse(new String[] { "--success" });
        assertFalse(parser.logPassed() == VeraCliArgParser.DEFAULT_ARGS
                .logPassed());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--success", "--flavour", "3b" });
        assertFalse(parser.logPassed() == VeraCliArgParser.DEFAULT_ARGS
                .logPassed());
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
        jCommander.parse(new String[] {});
        assertTrue(parser.extractFeatures() == VeraCliArgParser.DEFAULT_ARGS
                .extractFeatures());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--flavour", "1a", "-h" });
        assertTrue(parser.extractFeatures() == VeraCliArgParser.DEFAULT_ARGS
                .extractFeatures());
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
        jCommander.parse(new String[] { "-x" });
        assertFalse(parser.extractFeatures() == VeraCliArgParser.DEFAULT_ARGS
                .extractFeatures());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-x", "--format", "xml", "-h" });
        assertFalse(parser.extractFeatures() == VeraCliArgParser.DEFAULT_ARGS
                .extractFeatures());
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
        jCommander.parse(new String[] { "--extract" });
        assertFalse(parser.extractFeatures() == VeraCliArgParser.DEFAULT_ARGS
                .extractFeatures());

        // Test option works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--extract", "--flavour", "3b" });
        assertFalse(parser.extractFeatures() == VeraCliArgParser.DEFAULT_ARGS
                .extractFeatures());
    }

    @Test
    public final void testProcessTypeParsing() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = initialiseJCommander(parser);

        // Test flag works
        jCommander.parse(new String[] {});
        VeraAppConfig config = parser.appConfig(Applications.defaultConfig());
        assertFalse(parser.isValidationOff());
        assertTrue(config.getProcessType().getTasks().contains(TaskType.VALIDATE));

        // Test flag works
        jCommander.parse(new String[] { "-o" });
        config = parser.appConfig(Applications.defaultConfig());
        assertTrue(parser.isValidationOff());
        assertFalse(config.getProcessType().getTasks().contains(TaskType.VALIDATE));

        // Test flag works
        jCommander.parse(new String[] { "--off" });
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
