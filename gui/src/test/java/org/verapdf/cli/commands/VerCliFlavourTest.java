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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.Test;
import org.verapdf.pdfa.flavours.PDFAFlavour;

import static org.junit.Assert.assertSame;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
@SuppressWarnings("static-method")
public class VerCliFlavourTest {


    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourDefault() {
        // Test default is 1b
        assertSame(PDFAFlavour.NO_FLAVOUR, VeraCliArgParser.DEFAULT_ARGS.getFlavour());

        // Test empty String[] args doesn't change that
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse();
        assertSame(VeraCliArgParser.DEFAULT_ARGS.getFlavour(), parser.getFlavour());

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse("-l", "--version", "-h");
        assertSame(VeraCliArgParser.DEFAULT_ARGS.getFlavour(), parser.getFlavour());
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFlavourNoFlag() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-f");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFlavourNoFlagMultiParam() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-f", "-h");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFlavourNoOption() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("--flavour");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFlavourNoOptionMultiPararm() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("--flavour", "--version", "-h");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFlavourFlagInvalid() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("-f", "5t");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=ParameterException.class)
    public final void testGetFlavourOptionInvalid() {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse("--flavour", "9u");
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlagOff() {
        testFlavour("-f", "0", PDFAFlavour.NO_FLAVOUR);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOptionOff() {
        testFlavour("--flavour", "0", PDFAFlavour.NO_FLAVOUR);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag1a() {
        testFlavour("-f", "1a", PDFAFlavour.PDFA_1_A);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption1a() {
        testFlavour("--flavour", "1a", PDFAFlavour.PDFA_1_A);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag1b() {
        testFlavour("-f", "1b", PDFAFlavour.PDFA_1_B);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption1b() {
        testFlavour("--flavour", "1b", PDFAFlavour.PDFA_1_B);
    }


    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag2a() {
        testFlavour("-f", "2a", PDFAFlavour.PDFA_2_A);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption2a() {
        testFlavour("--flavour", "2a", PDFAFlavour.PDFA_2_A);
    }


    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag2b() {
        testFlavour("-f", "2b", PDFAFlavour.PDFA_2_B);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption2b() {
        testFlavour("--flavour", "2b", PDFAFlavour.PDFA_2_B);
    }


    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag3a() {
        testFlavour("-f", "3a", PDFAFlavour.PDFA_3_A);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption3a() {
        testFlavour("--flavour", "3a", PDFAFlavour.PDFA_3_A);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag3b() {
        testFlavour("-f", "3b", PDFAFlavour.PDFA_3_B);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption3b() {
        testFlavour("--flavour", "3b", PDFAFlavour.PDFA_3_B);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag3u() {
        testFlavour("-f", "3u", PDFAFlavour.PDFA_3_U);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption3u() {
        testFlavour("--flavour", "3u", PDFAFlavour.PDFA_3_U);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag4() {
        testFlavour("-f", "4", PDFAFlavour.PDFA_4);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption4() {
        testFlavour("--flavour", "4", PDFAFlavour.PDFA_4);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag4f() {
        testFlavour("-f", "4f", PDFAFlavour.PDFA_4_F);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption4f() {
        testFlavour("--flavour", "4f", PDFAFlavour.PDFA_4_F);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlag4e() {
        testFlavour("-f", "4e", PDFAFlavour.PDFA_4_E);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOption4e() {
        testFlavour("--flavour", "4e", PDFAFlavour.PDFA_4_E);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourFlagua1() {
        testFlavour("-f", "ua1", PDFAFlavour.PDFUA_1);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test
    public final void testGetFlavourOptionua1() {
        testFlavour("--flavour", "ua1", PDFAFlavour.PDFUA_1);
    }

    private static final void testFlavour(final String flag, final String flavour, final PDFAFlavour expected) {
        VeraCliArgParser parser = new VeraCliArgParser();
        JCommander jCommander = VeraCliArgParserTest.initialiseJCommander(parser);

        // Test flag works
        jCommander.parse(flag, flavour);
        assertSame(expected, parser.getFlavour());

        // Test flag works with other options & flags
        parser = new VeraCliArgParser();
        jCommander = VeraCliArgParserTest.initialiseJCommander(parser);
        jCommander.parse("-l", flag, flavour, "--format", "xml");
        assertSame(expected, parser.getFlavour());
    }

}
