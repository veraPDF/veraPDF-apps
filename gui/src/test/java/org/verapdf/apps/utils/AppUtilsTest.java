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
package org.verapdf.apps.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AppUtilsTest {
	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	
	@Test
	public void testFilterPdfFiles() throws IOException {
		File ext1 = tempFolder.newFile("test1.ext");
		File ext2 = tempFolder.newFile("test2.SCH");
		File ext3 = tempFolder.newFile("test3.xsl");
		File pdf1 = tempFolder.newFile("test1.pdf");
		File pdf2 = tempFolder.newFile("test2.PDF");
		File pdf3 = tempFolder.newFile("test3.pdf");
		List<File> pdfs = Arrays.asList(pdf1, pdf2, pdf3);
		File non_pdf1 = new File("test1.pdf");
		File non_pdf2 = new File("test2.PDF");
		File non_pdf3 = new File("test3.pdf");
		List<File> non_pdfs = Arrays.asList(non_pdf1, non_pdf2, non_pdf3);
		File xml1 = tempFolder.newFile("test1.xml");
		File xml2 = tempFolder.newFile("test2.xml");
		File xml3 = tempFolder.newFile("test3.xml");
		List<File> xml_exts = Arrays.asList(xml1, xml2, xml3, ext1, ext2, ext3);
		List<File> all = Arrays.asList(pdf1, pdf2, pdf3, xml1, xml2, xml3, ext1, ext2, ext3);
		assertEquals(pdfs.size(), ApplicationUtils.filterPdfFiles(pdfs, true, false).size());
		assertEquals(0, ApplicationUtils.filterPdfFiles(xml_exts, true, false).size());
		assertEquals(0, ApplicationUtils.filterPdfFiles(non_pdfs, true, false).size());
		assertEquals(pdfs.size(), ApplicationUtils.filterPdfFiles(all, true, false).size());
		assertEquals(pdfs.size(), ApplicationUtils.filterPdfFiles(pdfs, true, true).size());
		assertEquals(6, ApplicationUtils.filterPdfFiles(xml_exts, true, true).size());
		assertEquals(0, ApplicationUtils.filterPdfFiles(non_pdfs, true, true).size());
		assertEquals(all.size(), ApplicationUtils.filterPdfFiles(all, true, true).size());
	}

	@Test
	public void testAreAllExists() throws IOException {
		File non_ext1 = new File("test1.ext");
		File non_ext2 = new File("test2.SCH");
		File non_ext3 = new File("test3.xsl");
		List<File> non_exts = Arrays.asList(non_ext1, non_ext2, non_ext3);
		assertFalse(ApplicationUtils.doAllFilesExist(non_exts));
		File pdf1 = tempFolder.newFile("test1.pdf");
		File pdf2 = tempFolder.newFile("test2.PDF");
		File pdf3 = tempFolder.newFile("test3.pdf");
		List<File> pdfs = Arrays.asList(pdf1, pdf2, pdf3);
		List<File> pdfs_non_exts = new ArrayList<>(pdfs);
		pdfs_non_exts.addAll(non_exts);
		assertFalse(ApplicationUtils.doAllFilesExist(pdfs_non_exts));
		assertTrue(ApplicationUtils.doAllFilesExist(pdfs));
		File non_pdf1 = new File("test1.pdf");
		File non_pdf2 = new File("test2.PDF");
		File non_pdf3 = new File("test3.pdf");
		List<File> non_pdfs = Arrays.asList(non_pdf1, non_pdf2, non_pdf3);
		assertFalse(ApplicationUtils.doAllFilesExist(non_pdfs));
	}

	@Test
	public void testIsLegalExtension() throws IOException {
		File non_ext1 = new File("test1.ext");
		File non_ext2 = new File("test2.SCH");
		File non_ext3 = new File("test3.xsl");
		List<File> non_exts = Arrays.asList(non_ext1, non_ext2, non_ext3);
		assertTrue(ApplicationUtils.isLegalExtension(non_exts, new String[] { "pdf" }));
		File pdf1 = tempFolder.newFile("test1.pdf");
		File pdf2 = tempFolder.newFile("test2.PDF");
		File pdf3 = tempFolder.newFile("test3.pdf");
		List<File> pdfs = Arrays.asList(pdf1, pdf2, pdf3);
		assertTrue(ApplicationUtils.isLegalExtension(pdfs, new String[] { "pdf" }));
		assertTrue(ApplicationUtils.isLegalExtension(pdfs, new String[] { "PDF" }));
		assertTrue(ApplicationUtils.isLegalExtension(pdfs, new String[] { ".PDF" }));
		File xml1 = tempFolder.newFile("test1.xml");
		File xml2 = tempFolder.newFile("test2.xml");
		File xml3 = tempFolder.newFile("test3.xml");
		List<File> xmls = Arrays.asList(xml1, xml2, xml3);
		assertTrue(ApplicationUtils.isLegalExtension(xmls, new String[] { "XML" }));
		assertFalse(ApplicationUtils.isLegalExtension(xmls, new String[] { ".pdf" }));
		assertTrue(ApplicationUtils.isLegalExtension(xmls, new String[] { ".xml" }));
		List<File> xml_pdfs = Arrays.asList(xml1, xml2, xml3, pdf1, pdf2, pdf3);
		assertFalse(ApplicationUtils.isLegalExtension(xml_pdfs, new String[] { "pdf" }));
		assertFalse(ApplicationUtils.isLegalExtension(xml_pdfs, new String[] { "XML" }));
		assertTrue(ApplicationUtils.isLegalExtension(xml_pdfs, new String[] { "XML", "pdf" }));
	}
}
