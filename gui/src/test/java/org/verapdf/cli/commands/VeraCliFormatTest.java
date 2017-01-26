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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.verapdf.processor.FormatOption;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@SuppressWarnings("static-method")
public class VeraCliFormatTest {

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFormat()}.
     */
    @Test
    public final void testGetFormatDefault() {
        // Test default is XML
        assertTrue(VeraCliArgParser.DEFAULT_ARGS.getFormat() == FormatOption.MRR);

        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest
                .initialiseJCommander(parser);

        // Test empty String[] args doesn't change that
        jCommander.parse(new String[] {});
        assertTrue(parser.getFormat() == VeraCliArgParser.DEFAULT_ARGS
                .getFormat());

        // Test other flags & options don't change that
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--version", "--success", "-f",
                "1b" });
        assertTrue(parser.getFormat() == VeraCliArgParser.DEFAULT_ARGS
                .getFormat());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFormat()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFormatGarbage() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest
                .initialiseJCommander(parser);

        // Test that "rub" value throws exception
        jCommander.parse(new String[] { "--format", "somerubbish" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFormat()}.
     */
    @Test
    public final void testGetFormatXml() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest
                .initialiseJCommander(parser);

        // Test that "xml" value selects XML
        jCommander.parse(new String[] { "--format", "xml" });
        assertTrue(parser.getFormat() == FormatOption.XML);

        // Test that "XML" value selects XML
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "--format", "XML" });
        assertTrue(parser.getFormat() == FormatOption.XML);

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--format", "xml", "--success",
                "-f", "1b" });
        assertTrue(parser.getFormat() == FormatOption.XML);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFormat()}.
     */
    @Test
    public final void testGetFormatMrr() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest
                .initialiseJCommander(parser);

        // Test that "mrr" value selects MRR
        jCommander.parse(new String[] { "--format", "mrr" });
        assertTrue(parser.getFormat() == FormatOption.MRR);

        // Test that "MRR" value selects MRR
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "--format", "MRR" });
        assertTrue(parser.getFormat() == FormatOption.MRR);

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse(new String[] { "-l", "--format", "mrr", "--passed",
                "-f", "1a" });
        assertTrue(parser.getFormat() == FormatOption.MRR);
    }
}
