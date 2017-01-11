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
import org.verapdf.pdfa.flavours.PDFAFlavour;

import static org.junit.Assert.assertTrue;

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
        // Test empty String[] args doesn't change that
        VeraCliArgs parser = new VeraCliArgParser(new String[] {});
        assertTrue(parser.getFlavour() == PDFAFlavour.NO_FLAVOUR);

        // Test other flags & options doesn't change that
        parser = new VeraCliArgParser(new String[] { "-l", "--version", "-h" });
        assertTrue(parser.getFlavour() == PDFAFlavour.NO_FLAVOUR);
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetFlavourNoFlag() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "-f" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetFlavourNoFlagMultiParam() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "-f", "-h" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetFlavourNoOption() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--flavour" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetFlavourNoOptionMultiPararm() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--flavour", "--version", "-h" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetFlavourFlagInvalid() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "-f", "5t" });
    }

    /**
     * Test method for
     * {@link org.verapdf.cli.commands.VeraCliArgParser#getFlavour()}.
     */
    @Test(expected=joptsimple.OptionException.class)
    public final void testGetFlavourOptionInvalid() {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { "--flavour", "9u" });
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

    private static final void testFlavour(final String flag, final String flavour, final PDFAFlavour expected) {
        VeraCliArgs parser = new VeraCliArgParser(new String[] { flag, flavour });
        assertTrue(parser.getFlavour() == expected);

        // Test flag works with other options & flags
        parser = new VeraCliArgParser(new String[] { "-l", flag, flavour, "--format", "xml"});
        assertTrue(parser.getFlavour() == expected);
    }

}
