package org.verapdf.apps.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.verapdf.apps.utils.ApplicationUtils;

public class AppUtilsTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	
	@Test
	public void testFilterPdfFiles() throws IOException {
		File ext1 = tempFolder.newFile("test1.ext");
		File ext2 = tempFolder.newFile("test2.SCH");
		File ext3 = tempFolder.newFile("test3.xsl");
		File pdf1 = tempFolder.newFile("test1.pdf");
		File pdf2 = tempFolder.newFile("test2.PDF");
		File pdf3 = tempFolder.newFile("test3.pdf");
		File[] pdfs = {pdf1, pdf2, pdf3};
		File non_pdf1 = new File("test1.pdf");
		File non_pdf2 = new File("test2.PDF");
		File non_pdf3 = new File("test3.pdf");
		File[] non_pdfs = {non_pdf1, non_pdf2, non_pdf3};
		File xml1 = tempFolder.newFile("test1.xml");
		File xml2 = tempFolder.newFile("test2.xml");
		File xml3 = tempFolder.newFile("test3.xml");
		File[] xml_exts = {xml1, xml2, xml3, ext1, ext2, ext3};
		File[] all = {pdf1, pdf2, pdf3, xml1, xml2, xml3, ext1, ext2, ext3};
		assertEquals(pdfs.length, ApplicationUtils.filterPdfFiles(pdfs).size());
		assertTrue(ApplicationUtils.filterPdfFiles(xml_exts).size() == 0);
		assertTrue(ApplicationUtils.filterPdfFiles(non_pdfs).size() == 0);
		assertEquals(pdfs.length, ApplicationUtils.filterPdfFiles(all).size());
	}

	@Test
	public void testAreAllExists() throws IOException {
		File non_ext1 = new File("test1.ext");
		File non_ext2 = new File("test2.SCH");
		File non_ext3 = new File("test3.xsl");
		File[] non_exts = {non_ext1, non_ext2, non_ext3};
		assertFalse(ApplicationUtils.areAllExists(non_exts));
		File pdf1 = tempFolder.newFile("test1.pdf");
		File pdf2 = tempFolder.newFile("test2.PDF");
		File pdf3 = tempFolder.newFile("test3.pdf");
		File[] pdfs = {pdf1, pdf2, pdf3};
		File[] pdfs_non_exts = {pdf1, pdf2, pdf3, non_ext1, non_ext2, non_ext3};
		assertFalse(ApplicationUtils.areAllExists(pdfs_non_exts));
		assertTrue(ApplicationUtils.areAllExists(pdfs));
		File non_pdf1 = new File("test1.pdf");
		File non_pdf2 = new File("test2.PDF");
		File non_pdf3 = new File("test3.pdf");
		File[] non_pdfs = {non_pdf1, non_pdf2, non_pdf3};
		assertFalse(ApplicationUtils.areAllExists(non_pdfs));
	}

	@Test
	public void testIsLegalExtension() throws IOException {
		File non_ext1 = new File("test1.ext");
		File non_ext2 = new File("test2.SCH");
		File non_ext3 = new File("test3.xsl");
		File[] non_exts = {non_ext1, non_ext2, non_ext3};
		assertTrue(ApplicationUtils.isLegalExtension(non_exts, new String[] { "pdf" }));
		File pdf1 = tempFolder.newFile("test1.pdf");
		File pdf2 = tempFolder.newFile("test2.PDF");
		File pdf3 = tempFolder.newFile("test3.pdf");
		File[] pdfs = {pdf1, pdf2, pdf3};
		assertTrue(ApplicationUtils.isLegalExtension(pdfs, new String[] { "pdf" }));
		assertTrue(ApplicationUtils.isLegalExtension(pdfs, new String[] { "PDF" }));
		assertTrue(ApplicationUtils.isLegalExtension(pdfs, new String[] { ".PDF" }));
		File xml1 = tempFolder.newFile("test1.xml");
		File xml2 = tempFolder.newFile("test2.xml");
		File xml3 = tempFolder.newFile("test3.xml");
		File[] xmls = {xml1, xml2, xml3};
		assertTrue(ApplicationUtils.isLegalExtension(xmls, new String[] { "XML" }));
		assertFalse(ApplicationUtils.isLegalExtension(xmls, new String[] { ".pdf" }));
		assertTrue(ApplicationUtils.isLegalExtension(xmls, new String[] { ".xml" }));
		File[] xml_pdfs = {xml1, xml2, xml3, pdf1, pdf2, pdf3};
		assertFalse(ApplicationUtils.isLegalExtension(xml_pdfs, new String[] { "pdf" }));
		assertFalse(ApplicationUtils.isLegalExtension(xml_pdfs, new String[] { "XML" }));
		assertTrue(ApplicationUtils.isLegalExtension(xml_pdfs, new String[] { "XML", "pdf" }));
	}
}
