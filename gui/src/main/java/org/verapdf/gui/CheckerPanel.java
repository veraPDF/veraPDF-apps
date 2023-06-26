/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org> All rights
 * reserved. VeraPDF Library GUI is free software: you can redistribute it
 * and/or modify it under the terms of either: The GNU General public license
 * GPLv3+. You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the
 * source tree. If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html. The Mozilla Public License
 * MPLv2+. You should have received a copy of the Mozilla Public License along
 * with VeraPDF Library GUI as the LICENSE.MPL file in the root of the source
 * tree. If a copy of the MPL was not distributed with this file, you can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.gui;

import org.verapdf.apps.Applications;
import org.verapdf.apps.utils.ApplicationUtils;
import org.verapdf.core.utils.FileUtils;
import org.verapdf.gui.utils.*;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.flavours.PDFAFlavours;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.TaskType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.verapdf.processor.app.AppConfigBuilder;
import org.verapdf.processor.app.ConfigManager;
import org.verapdf.processor.app.ProcessType;
import org.verapdf.processor.app.VeraAppConfig;

/**
 * Panel with functionality for checker.
 *
 * @author Maksim Bezrukov
 */
@SuppressWarnings("synthetic-access")
class CheckerPanel extends JPanel {
	private static transient ConfigManager config;
	/**
	 * ID for serialisation
	 */
	private static final long serialVersionUID = 1290058869994329766L;

	private static final Logger logger = Logger.getLogger(CheckerPanel.class.getCanonicalName());

	private static final Map<String, PDFAFlavour> FLAVOURS_MAP = new HashMap<>();
	private static final String emptyString = ""; //$NON-NLS-1$

	private JFileChooser pdfChooser;
	private JFileChooser xmlChooser;
	private JFileChooser htmlChooser;
	private JFileChooser policyChooser;
	private List<File> pdfsToProcess;
	private JTextField chosenPDF;
	private JTextField chosenProfile;
	private JTextField chosenPolicy;
	private JLabel resultLabel;
	private File xmlReport;
	private File htmlReport;
	private File policy;

	private JComboBox<ProcessType> ProcessTypes;
	private JCheckBox fixMetadata;
	private JComboBox<String> chooseFlavour;

	private boolean isValidationErrorOccurred;

	private JButton execute;
	private JButton saveXML;
	private JButton viewXML;
	private JButton saveHTML;
	private JButton viewHTML;

	private DropTarget targetPDF;
	private DropTarget targetPolicy;
	private DropTarget targetProfile;

	private transient Path profilePath;

	private JProgressBar progressBar;
	transient ValidateWorker validateWorker;

	private boolean validationInProgress = false;

	CheckerPanel(final ConfigManager config) throws IOException {
		CheckerPanel.config = config;
		this.profilePath = FileSystems.getDefault().getPath(emptyString);

		this.initGui();

		this.pdfChooser = getChooser(true, GUIConstants.PDF, GUIConstants.ZIP);
		this.pdfChooser.setMultiSelectionEnabled(true);
		this.pdfChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		this.xmlChooser = getChooser(false, GUIConstants.XML);
		this.htmlChooser = getChooser(false, GUIConstants.HTML);
		this.policyChooser = getChooser(false, GUIConstants.SCH, GUIConstants.XSL, GUIConstants.XSLT);

		this.addActionListeners();
	}

	private void addActionListeners() {
		this.execute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (validationInProgress) {
					execute.setEnabled(false);
					validationInProgress = false;
					validateWorker.cancel(true);
					return;
				}
				try {
					changeConfig();
					ValidationProfile customProfile = null;
					if (GUIConstants.CUSTOM_PROFILE_COMBOBOX_TEXT.equals(CheckerPanel.this.chooseFlavour.getSelectedItem())) {
						try (InputStream is = new FileInputStream(CheckerPanel.this.profilePath.toFile())) {
							customProfile = Profiles.profileFromXml(is);
						}
					}
					CheckerPanel.this.validateWorker = new ValidateWorker(CheckerPanel.this,
							CheckerPanel.this.pdfsToProcess, CheckerPanel.config, customProfile,
							CheckerPanel.this.policy);
					CheckerPanel.this.progressBar.setVisible(true);
					CheckerPanel.this.resultLabel.setVisible(false);
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					CheckerPanel.this.execute.setText(GUIConstants.CANCEL_BUTTON_TEXT);
					CheckerPanel.this.isValidationErrorOccurred = false;
					CheckerPanel.this.viewXML.setEnabled(false);
					CheckerPanel.this.saveXML.setEnabled(false);
					CheckerPanel.this.viewHTML.setEnabled(false);
					CheckerPanel.this.saveHTML.setEnabled(false);
					validationInProgress = true;
					CheckerPanel.this.validateWorker.execute();
				} catch (IllegalArgumentException | JAXBException | IOException excep) {
					DialogUtils.errorDialog(CheckerPanel.this, excep.getMessage(), logger, excep);
				}
			}
		});

		this.saveXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				saveReport(CheckerPanel.this.xmlChooser, GUIConstants.XML, CheckerPanel.this.xmlReport);
			}
		});

		this.saveHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				saveReport(CheckerPanel.this.htmlChooser, GUIConstants.HTML, CheckerPanel.this.htmlReport);
			}
		});

		this.viewXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (CheckerPanel.this.xmlReport == null) {
					String message = String.format(GUIConstants.ERROR_SAVING_REPORT, GUIConstants.XML);
					DialogUtils.errorDialog(CheckerPanel.this, message, logger, new IOException(message));
				} else {
					this.openXMLReport();
				}
			}

			private void openXMLReport() {
				try {
					Desktop.getDesktop().open(CheckerPanel.this.xmlReport);
				} catch (IOException excep) {
					String message = String.format(GUIConstants.IOEXCEP_OPENING_REPORT,
					                               CheckerPanel.this.xmlReport.getAbsolutePath(), GUIConstants.XML);
					DialogUtils.errorDialog(CheckerPanel.this, message, logger, excep);
				}
			}
		});

		this.viewHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (CheckerPanel.this.htmlReport == null) {
					String message = String.format(GUIConstants.ERROR_SAVING_REPORT, GUIConstants.HTML);
					DialogUtils.errorDialog(CheckerPanel.this, message, logger, new IOException(message));
				} else {
					try {
						Desktop.getDesktop().open(CheckerPanel.this.htmlReport);
					} catch (IOException excep) {
						String message = String.format(GUIConstants.IOEXCEP_OPENING_REPORT,
						                               CheckerPanel.this.htmlReport.getAbsolutePath(), GUIConstants.HTML);
						DialogUtils.errorDialog(CheckerPanel.this, message, logger, excep);
					}
				}
			}
		});

		this.fixMetadata.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					config.updateAppConfig(appConfigFromState());
				} catch (JAXBException | IOException exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	private void initGui() throws IOException {
		setPreferredSize(new Dimension(GUIConstants.PREFERRED_SIZE_WIDTH, GUIConstants.PREFERRED_SIZE_HEIGHT));
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		this.chosenPDF = new JTextField(GUIConstants.PDF_NOT_CHOSEN_TEXT);
		this.chosenPDF.setEditable(false);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_Y, GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_WEIGHT_Y, GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.chosenPDF, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.chosenPDF);

		PanelDropTargetListener dtdPDFListener = new PanelDropTargetListener(-1,
				GUIConstants.PDF, GUIConstants.ZIP);
		targetPDF = new DropTarget(this.chosenPDF, DnDConstants.ACTION_COPY_OR_MOVE,
				dtdPDFListener, true, null);

		JButton choosePDF = new JButton(GUIConstants.CHOOSE_PDF_BUTTON_TEXT);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_Y, GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(choosePDF, gbc);
		this.add(choosePDF);

		choosePDF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				CheckerPanel.this.chooseFile(CheckerPanel.this.pdfChooser, new String[] { GUIConstants.PDF });
			}
		});

		final JLabel processType = new JLabel(GUIConstants.PROCESSING_TYPE);
		setGridBagConstraintsParameters(gbc, GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_Y, GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(processType, gbc);
		processType.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(processType);

		this.ProcessTypes = new JComboBox<>(ProcessType.getOptionValues());
		this.ProcessTypes.setSelectedItem(config.getApplicationConfig().getProcessType());
		ProcessingTypeRenderer processingTypeRenderer = new ProcessingTypeRenderer();
		this.ProcessTypes.setRenderer(processingTypeRenderer);
		setGridBagConstraintsParameters(gbc, GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_X,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_Y,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_WEIGHT_X,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_WEIGHT_Y,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_WIDTH,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.ProcessTypes, gbc);
		this.add(this.ProcessTypes);

		this.fixMetadata = new JCheckBox(GUIConstants.FIX_METADATA_LABEL_TEXT);
		this.fixMetadata.setHorizontalTextPosition(SwingConstants.LEFT);
		if (Foundries.defaultParserIsPDFBox()) {
			this.fixMetadata.setSelected(false);
			this.fixMetadata.setEnabled(false);
		} else {
			this.fixMetadata.setSelected(false);
		}
		setGridBagConstraintsParameters(gbc, GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_X,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_Y,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_WEIGHT_X,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_WEIGHT_Y,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_WIDTH,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.fixMetadata, gbc);
//		this.add(this.fixMetadata);
		this.fixMetadata.setHorizontalAlignment(SwingConstants.CENTER);
		if (config.getApplicationConfig().getProcessType() == ProcessType.EXTRACT) {
			this.fixMetadata.setEnabled(false);
		}

		final JLabel chooseFlavourLabel = new JLabel(GUIConstants.CHOOSE_FLAVOUR);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_FLAVOUR_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_FLAVOUR_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		chooseFlavourLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		gbl.setConstraints(chooseFlavourLabel, gbc);
		this.add(chooseFlavourLabel);

		Vector<String> availableFlavours = new Vector<>();
		availableFlavours.add(GUIConstants.CUSTOM_PROFILE_COMBOBOX_TEXT);
//		availableFlavours.add(GUIConstants.AUTO_FLAVOUR_COMBOBOX_TEXT);
		availableFlavours.add(GUIConstants.AUTO_ARLINGTON_FLAVOUR_COMBOBOX_TEXT);
		SortedSet<String> sortedFlavours = new TreeSet<>();
		for (PDFAFlavour flavour : Profiles.getVeraProfileDirectory().getPDFAFlavours()) {
			String flavourReadableText = getFlavourReadableText(flavour);
			sortedFlavours.add(flavourReadableText);
			FLAVOURS_MAP.put(flavourReadableText, flavour);
		}
		availableFlavours.addAll(sortedFlavours);

		this.chooseFlavour = new JComboBox<>(availableFlavours);
		this.chooseFlavour.setOpaque(true);
		ChooseFlavourRenderer renderer = new ChooseFlavourRenderer();
		this.chooseFlavour.setRenderer(renderer);
		PDFAFlavour fromConfig = config.createProcessorConfig().getValidatorConfig().getFlavour();
		String fromConfigFlavourText = getFlavourReadableText(fromConfig);
		if (availableFlavours.contains(fromConfigFlavourText)) {
			this.chooseFlavour.setSelectedItem(fromConfigFlavourText);
		} else {
			this.chooseFlavour.setSelectedItem(GUIConstants.AUTO_ARLINGTON_FLAVOUR_COMBOBOX_TEXT);
		}
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.chooseFlavour, gbc);
		this.add(this.chooseFlavour);

		this.chosenProfile = new JTextField(GUIConstants.VALIDATION_PROFILE_NOT_CHOSEN);
		this.chosenProfile.setEditable(false);
		this.chosenProfile.setEnabled(false);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.chosenProfile, gbc);
		this.add(this.chosenProfile);

		PanelDropTargetListener dtdProfileListener = new PanelDropTargetListener(1,
				GUIConstants.XML);
		targetProfile = new DropTarget(this.chosenProfile, DnDConstants.ACTION_COPY_OR_MOVE,
				dtdProfileListener, true, null);
		targetProfile.setActive(false);

		if (!this.profilePath.toString().isEmpty()) {
			this.chosenProfile.setText(this.profilePath.toString());
		} else {
			this.chosenProfile.setText(GUIConstants.CHOOSEN_PROFILE_TEXTFIELD_DEFAULT_TEXT);
		}

		this.setupProfileButton(gbl, gbc);

		String policyPath = config.getApplicationConfig().getPolicyFile();
		if (policyPath != null && !policyPath.isEmpty()) {
			this.policy = new File(policyPath);
		} else {
			policyPath = GUIConstants.POLICY_PROFILE_NOT_CHOSEN;
		}
		this.chosenPolicy = new JTextField(policyPath);
		this.chosenPolicy.setEditable(false);
		this.chosenPolicy.setEnabled(this.ProcessTypes.getSelectedItem() == ProcessType.POLICY);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.CHOSEN_POLICY_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOSEN_POLICY_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.chosenPolicy, gbc);
		this.add(this.chosenPolicy);

		PanelDropTargetListener dtdPolicyListener = new PanelDropTargetListener(1,
				GUIConstants.SCH, GUIConstants.XSL, GUIConstants.XSLT);
		targetPolicy = new DropTarget(this.chosenPolicy, DnDConstants.ACTION_COPY_OR_MOVE,
				dtdPolicyListener, true, null);
		targetPolicy.setActive(this.ProcessTypes.getSelectedItem() == ProcessType.POLICY);

		this.setupPolicyButton(gbl, gbc);

		this.resultLabel = new JLabel();
		this.resultLabel.setForeground(GUIConstants.BEFORE_VALIDATION_COLOR);
		this.resultLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		setGridBagConstraintsParameters(gbc, GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_Y, GUIConstants.RESULT_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.RESULT_LABEL_CONSTRAINT_WEIGHT_Y, GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.CENTER);
		gbl.setConstraints(this.resultLabel, gbc);
		this.add(this.resultLabel);

		this.progressBar = new JProgressBar();
		this.progressBar.setIndeterminate(true);
		this.progressBar.setVisible(false);
		setGridBagConstraintsParameters(gbc, GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_X,
				GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_Y, GUIConstants.PROGRESSBAR_CONSTRAINT_WEIGHT_X,
				GUIConstants.PROGRESSBAR_CONSTRAINT_WEIGHT_Y, GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_WIDTH,
				GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.progressBar, gbc);
		this.add(this.progressBar);

		this.execute = new JButton(GUIConstants.VALIDATE_BUTTON_TEXT);
		this.execute.setEnabled(false);
		setGridBagConstraintsParameters(gbc, GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_Y, GUIConstants.VALIDATE_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_WEIGHT_Y, GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.execute, gbc);
		this.add(this.execute);

		JPanel reports = createReportPanel(gbl, gbc);
		this.add(reports);
		this.setupReportPanel(reports);
	}

	private static JPanel createReportPanel(final GridBagLayout gbl, final GridBagConstraints gbc) {
		JPanel reports = new JPanel();
		reports.setBorder(BorderFactory.createTitledBorder(GUIConstants.REPORT));
		reports.setLayout(
				new GridLayout(GUIConstants.REPORT_PANEL_LINES_NUMBER, GUIConstants.REPORT_PANEL_COLUMNS_NUMBER));
		setGridBagConstraintsParameters(gbc, GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_X,
				GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_Y, GUIConstants.REPORT_PANEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.REPORT_PANEL_CONSTRAINT_WEIGHT_Y, GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(reports, gbc);
		return reports;
	}

	private void setupReportPanel(final JPanel reports) throws IOException {
		LogoPanel xmlLogo = new LogoPanel(GUIConstants.XML_LOGO_NAME, reports.getBackground(),
				GUIConstants.XML_LOGO_BORDER_WIDTH);
		reports.add(xmlLogo);

		this.saveXML = new JButton(GUIConstants.SAVE_REPORT_BUTTON_TEXT);
		this.saveXML.setEnabled(false);
		reports.add(this.saveXML);

		this.viewXML = new JButton(GUIConstants.VIEW_REPORT_BUTTON_TEXT);
		this.viewXML.setEnabled(false);
		reports.add(this.viewXML);

		LogoPanel htmlLogo = new LogoPanel(GUIConstants.HTML_LOGO_NAME, reports.getBackground(),
				GUIConstants.HTML_LOGO_BORDER_WIDTH);
		reports.add(htmlLogo);

		this.saveHTML = new JButton(GUIConstants.SAVE_HTML_REPORT_BUTTON_TEXT);
		this.saveHTML.setEnabled(false);
		reports.add(this.saveHTML);

		this.viewHTML = new JButton(GUIConstants.VIEW_HTML_REPORT_BUTTON_TEXT);
		this.viewHTML.setEnabled(false);
		reports.add(this.viewHTML);
	}

	private void setupProfileButton(final GridBagLayout gbl, final GridBagConstraints gbc) {

		final JButton chooseProfile = new JButton(GUIConstants.CHOOSE_PROFILE_BUTTON_TEXT);
		chooseProfile.setEnabled(false);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(chooseProfile, gbc);
		this.add(chooseProfile);
		this.chooseFlavour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				if (CheckerPanel.this.chooseFlavour.getSelectedItem()
						.equals(GUIConstants.CUSTOM_PROFILE_COMBOBOX_TEXT)) {
					chooseProfile.setEnabled(true);
					CheckerPanel.this.chosenProfile.setEnabled(true);
					CheckerPanel.this.targetProfile.setActive(true);
				} else {
					chooseProfile.setEnabled(false);
					CheckerPanel.this.chosenProfile.setEnabled(false);
					CheckerPanel.this.targetProfile.setActive(false);
				}
				CheckerPanel.this.execute.setEnabled(isExecute());
				try {
					config.updateValidatorConfig(validatorConfigFromState());
				} catch (JAXBException | IOException e) {
					e.printStackTrace();
				}
			}
		});

		chooseProfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				CheckerPanel.this.chooseFile(CheckerPanel.this.xmlChooser, new String[] { GUIConstants.XML });
			}
		});

	}

	private void setupPolicyButton(final GridBagLayout gbl, final GridBagConstraints gbc) {

		final JButton choosePolicy = new JButton(GUIConstants.CHOOSE_POLICY_BUTTON_TEXT);
		choosePolicy.setEnabled(this.ProcessTypes.getSelectedItem() == ProcessType.POLICY);
		setGridBagConstraintsParameters(gbc, GUIConstants.CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_POLICY_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_POLICY_BUTTON_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_HEIGHT, GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(choosePolicy, gbc);
		this.add(choosePolicy);

		choosePolicy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				CheckerPanel.this.chooseFile(CheckerPanel.this.policyChooser,
						new String[] { GUIConstants.SCH, GUIConstants.XSL, GUIConstants.XSLT });
			}
		});

		this.chosenPolicy.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					config.updateAppConfig(appConfigFromState());
				} catch (JAXBException | IOException exception) {
					exception.printStackTrace();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		this.ProcessTypes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ProcessType item = (ProcessType) CheckerPanel.this.ProcessTypes.getSelectedItem();
				switch (item) {
					case VALIDATE:
						updateEnabling(!Foundries.defaultParserIsPDFBox(), false);
						break;
					case EXTRACT:
						CheckerPanel.this.fixMetadata.setSelected(false);
						updateEnabling(false, false);
						break;
					case VALIDATE_EXTRACT:
						updateEnabling(!Foundries.defaultParserIsPDFBox(), false);
						break;
					case POLICY:
						updateEnabling(!Foundries.defaultParserIsPDFBox(), true);
						break;
					default:
						break;
				}
				try {
					config.updateAppConfig(appConfigFromState());
				} catch (JAXBException | IOException exception) {
					exception.printStackTrace();
				}
			}

			private void updateEnabling(boolean enableFixMetadata, boolean enablePolicy) {
				CheckerPanel.this.fixMetadata.setEnabled(enableFixMetadata);
				CheckerPanel.this.chosenPolicy.setEnabled(enablePolicy);
				choosePolicy.setEnabled(enablePolicy);
				targetPolicy.setActive(enablePolicy);
				CheckerPanel.this.execute.setEnabled(isExecute());
			}
		});

	}

	void validationEnded(File xmlReportFile, File htmlReportFile) {

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.progressBar.setVisible(false);
		this.execute.setEnabled(true);
		this.execute.setText(GUIConstants.VALIDATE_BUTTON_TEXT);
		if (!validationInProgress) {
			return;
		}
		this.validationInProgress = false;
		if (!this.isValidationErrorOccurred) {
			try {
				ValidateWorker.ValidateWorkerSummary result = this.validateWorker.get();
				if (!result.getBatchSummary().isMultiJob()) {
					if (result.getBatchSummary().getFailedParsingJobs() == 1) {
						setResultMessage(GUIConstants.ERROR_IN_PARSING, GUIConstants.VALIDATION_FAILED_COLOR);
					} else if (result.getBatchSummary().getFailedEncryptedJobs() == 1) {
						setResultMessage(GUIConstants.ENCRYPTED_PDF, GUIConstants.VALIDATION_FAILED_COLOR);
					} else if (result.isPolicyApplied() && result.getPolicyNonCompliantJobCount() > 0) {
						setResultMessage(GUIConstants.POLICY_FALSE, GUIConstants.VALIDATION_FAILED_COLOR);
					} else if (result.getBatchSummary().getValidationSummary().getCompliantPdfaCount() > 0) {
						setResultMessage(GUIConstants.VALIDATION_OK, GUIConstants.VALIDATION_SUCCESS_COLOR);
					} else if (result.getBatchSummary().getValidationSummary().getNonCompliantPdfaCount() > 0) {
						setResultMessage(GUIConstants.VALIDATION_FALSE, GUIConstants.VALIDATION_FAILED_COLOR);
					} else if (result.getBatchSummary().getValidationSummary().getFailedJobCount() == 1) {
						setResultMessage(GUIConstants.ERROR_IN_VALIDATING, GUIConstants.VALIDATION_FAILED_COLOR);
					} else if (result.getBatchSummary().getFeaturesSummary().getTotalJobCount() > 0) {
						setResultMessage(GUIConstants.FEATURES_GENERATED_CORRECT,
								GUIConstants.VALIDATION_SUCCESS_COLOR);
					} else {
						setResultMessage(GUIConstants.ERROR_IN_FEATURES, GUIConstants.VALIDATION_FAILED_COLOR);
					}
				} else {
					setResultMessage(getBatchResultMessage(result), GUIConstants.BEFORE_VALIDATION_COLOR);
				}
				this.resultLabel.setVisible(true);

				this.xmlReport = xmlReportFile;
				this.htmlReport = htmlReportFile;

				if (xmlReportFile != null) {
					this.saveXML.setEnabled(true);
					this.viewXML.setEnabled(true);
				}

				if (htmlReportFile != null
						&& !(result.getBatchSummary().isMultiJob() && this.ProcessTypes.getSelectedItem() == ProcessType.EXTRACT)) {
					this.saveHTML.setEnabled(true);
					this.viewHTML.setEnabled(true);
				}
			} catch (InterruptedException e) {
				handleValidationError(GUIConstants.ERROR_INTERRUPTED, e);
			} catch (ExecutionException e) {
				handleValidationError(GUIConstants.ERROR_EXECUTION, e);
			}
		}

	}

	private static String getBatchResultMessage(ValidateWorker.ValidateWorkerSummary result) {
		String divisor = ", "; //$NON-NLS-1$
		StringBuilder sb = new StringBuilder(
				String.format("Items processed: %d", result.getBatchSummary().getTotalJobs())); //$NON-NLS-1$
		String end = String.format("%s Parsing Error: %d", divisor, result.getBatchSummary().getFailedParsingJobs()); //$NON-NLS-1$
		if (result.getBatchSummary().getValidationSummary().getTotalJobCount() > 0) {
			end = String.format("%sNo deviations: %d%sDeviations: %d%sError: %d", divisor, //$NON-NLS-1$
					result.getBatchSummary().getValidationSummary().getCompliantPdfaCount(), divisor,
					result.getBatchSummary().getValidationSummary().getNonCompliantPdfaCount(), divisor,
					result.getBatchSummary().getValidationSummary().getFailedJobCount());
			sb.append(end);
		}
		if (result.getBatchSummary().getFeaturesSummary().getSuccessfulJobCount() > 0) {
			String old_end = end;
			end = String.format("%sFeatures generated: %d%s", divisor, //$NON-NLS-1$
					result.getBatchSummary().getFeaturesSummary().getSuccessfulJobCount(), old_end);
			sb.append(end);
		}
		if (result.getPolicyNonCompliantJobCount() > 0) {
			end = String.format("%sPolicy invalid: %d", divisor, result.getPolicyNonCompliantJobCount());
			sb.append(end);
		}
		return sb.toString();
	}

	private void setResultMessage(String message, Color color) {
		this.resultLabel.setForeground(color);
		this.resultLabel.setText(message);
	}

	void handleValidationError(String message, Throwable cause) {
		cause.printStackTrace();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.progressBar.setVisible(false);
		this.isValidationErrorOccurred = true;
		DialogUtils.errorDialog(CheckerPanel.this, message + cause.getMessage(), logger, cause);
		this.resultLabel.setForeground(GUIConstants.VALIDATION_FAILED_COLOR);
		this.resultLabel.setText(message + cause.getMessage());
		this.resultLabel.setVisible(true);
	}

	private static JFileChooser getChooser(boolean allFilesAccept, String... types) throws IOException {
		JFileChooser res = new JFileChooser();
		File currentDir = new File(new File(GUIConstants.DOT).getCanonicalPath());
		res.setCurrentDirectory(currentDir);
		res.setAcceptAllFileFilterUsed(allFilesAccept);
		res.setFileFilter(new FileNameExtensionFilter(elementsCommaDelimeted(types), types));
		return res;
	}

	private static String elementsCommaDelimeted(String... elements) {
		StringBuilder description = new StringBuilder(elements[0]);
		for (int i = 1; i < elements.length; ++i) {
			description.append(",").append(elements[i]); //$NON-NLS-1$
		}
		return description.toString();
	}

	private static void setGridBagConstraintsParameters(GridBagConstraints gbc, int gridx, int gridy, int weightx,
	                                                    int weighty, int gridwidth, int gridheight, int fill) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.fill = fill;
	}

	private void chooseFile(JFileChooser chooser, String[] extensions) {
		int resultChoose = chooser.showOpenDialog(CheckerPanel.this);
		if (resultChoose == JFileChooser.APPROVE_OPTION) {
			File[] chosenFiles = chooser.getSelectedFiles();
			if (chosenFiles == null || chosenFiles.length == 0) {
				chosenFiles = new File[] { chooser.getSelectedFile() };
			}
			List<File> selectedFiles = Arrays.asList(chosenFiles);
			insertFilesInfo(selectedFiles, extensions);
		}
	}

	public void insertFilesInfo(List<File> selectedFiles, String[] extensions){
		if (!ApplicationUtils.doAllFilesExist(selectedFiles)) {
			DialogUtils.errorDialog(CheckerPanel.this, GUIConstants.ERROR_FILE_NOT_FOUND, logger,
					new FileNotFoundException(GUIConstants.ERROR_FILE_NOT_FOUND));
		} else {
			this.resultLabel.setForeground(GUIConstants.BEFORE_VALIDATION_COLOR);
			this.resultLabel.setText(""); //$NON-NLS-1$
			this.xmlReport = null;
			this.htmlReport = null;
			this.saveXML.setEnabled(false);
			this.viewXML.setEnabled(false);
			this.saveHTML.setEnabled(false);
			this.viewHTML.setEnabled(false);

			switch (extensions[0]) {
				case GUIConstants.PDF:
					this.pdfsToProcess = ApplicationUtils.filterPdfFiles(selectedFiles, true, true);
					this.chosenPDF.setText(getSelectedPathsMessage(selectedFiles));
					break;
				case GUIConstants.XML:
					if (selectedFiles.size() == 1) {
						this.profilePath = selectedFiles.get(0).toPath().toAbsolutePath();
						this.chosenProfile.setText(this.profilePath.toString());
					} else {
						String message = String.format(GUIConstants.ERROR_SINGLE_FILE, "validation profile"); //$NON-NLS-1$
						DialogUtils.errorDialog(CheckerPanel.this, message, logger,
								new IllegalArgumentException(message));
					}
					break;
				case GUIConstants.SCH:
				case GUIConstants.XSL:
				case GUIConstants.XSLT:
					if (selectedFiles.size() == 1 && !selectedFiles.get(0).isDirectory()) {
						this.policy = selectedFiles.get(0);
						this.chosenPolicy.setText(this.policy.getAbsolutePath());
					} else {
						String message = String.format(GUIConstants.ERROR_SINGLE_FILE, "policy file"); //$NON-NLS-1$
						DialogUtils.errorDialog(CheckerPanel.this, message, logger,
								new IllegalArgumentException(message));
					}
					break;
				default:
					// This method used only for previous two cases.
					// So do nothing.
			}
			this.execute.setEnabled(isExecute());
		}
	}

	private static String getSelectedPathsMessage(List<File> files) {
		if (files != null && files.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (File file : files) {
				builder.append(file.getAbsolutePath()).append(", "); //$NON-NLS-1$
			}
			return builder.substring(0, builder.length() - 2);
		}
		return emptyString;
	}

	private void saveReport(JFileChooser chooser, String extension, File report) {
		if (report == null) {
			String message = GUIConstants.ERROR_NO_VALIDATION;
			DialogUtils.errorDialog(CheckerPanel.this, message, logger, new IllegalArgumentException(message));
		} else {
			chooser.setSelectedFile(new File(extension.toLowerCase() + "Report." + extension.toLowerCase())); //$NON-NLS-1$
			int resultChoose = chooser.showSaveDialog(CheckerPanel.this);
			if (resultChoose == JFileChooser.APPROVE_OPTION) {
				File temp = chooser.getSelectedFile();

				if (!(temp.getName().toLowerCase().endsWith(GUIConstants.DOT + extension.toLowerCase()))) {
					temp = new File(temp.getPath() + GUIConstants.DOT + extension.toLowerCase());
				}

				try {
					try {
						Files.copy(report.toPath(), temp.toPath());
					} catch (FileAlreadyExistsException excep) {
						String message = String.format(GUIConstants.WARN_FILE_EXISTS, extension.toUpperCase());
						logger.log(Level.FINE, message, excep);
						int resultOption = JOptionPane.showConfirmDialog(CheckerPanel.this,
								message, GUIConstants.TITLE_OVERWRITE, JOptionPane.YES_NO_OPTION);
						if (resultOption == JOptionPane.YES_OPTION) {
							Files.copy(report.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
						}
					}
				} catch (IOException excep) {
					String message = String.format(GUIConstants.IOEXCEP_SAVING_REPORT, extension.toUpperCase());
					DialogUtils.errorDialog(CheckerPanel.this, message, logger, excep);
				}
			}
		}
	}

	private void changeConfig() throws JAXBException, IOException {
		if (!GUIConstants.CUSTOM_PROFILE_COMBOBOX_TEXT.equals(this.chooseFlavour.getSelectedItem())) {
			this.profilePath = FileSystems.getDefault().getPath(emptyString);
		}
		config.updateAppConfig(appConfigFromState());
		config.updateValidatorConfig(validatorConfigFromState());
	}

	ValidatorConfig validatorConfigFromState() {
		ValidatorConfig validatorConfig = config.getValidatorConfig();
		int maxFails = validatorConfig.getMaxFails();
		if (maxFails > 0 && config.getApplicationConfig().getProcessType().getTasks().contains(TaskType.FIX_METADATA)) {
			logger.log(Level.WARNING, "Option \"Halt validation after " + maxFails + " failed checks\" is ignored when fixing metadata is enabled");
			maxFails = -1;
		}
		PDFAFlavour flavour = getCurrentFlavour();
		return ValidatorFactory.createConfig(flavour, validatorConfig.getDefaultFlavour(),
				validatorConfig.isRecordPasses(), maxFails,
				validatorConfig.isDebug(), validatorConfig.isLogsEnabled(),
				validatorConfig.getLoggingLevel(), validatorConfig.getMaxNumberOfDisplayedFailedChecks(),
				validatorConfig.showErrorMessages(), null, validatorConfig.getShowProgress(), false);
	}

	VeraAppConfig appConfigFromState() {
		AppConfigBuilder builder = Applications.createConfigBuilder(CheckerPanel.config.getApplicationConfig());
		ProcessType selectedItem = (ProcessType) this.ProcessTypes.getSelectedItem();
		if (isFixMetadata() && CheckerPanel.config.getApplicationConfig().getFixesFolder().isEmpty() && this.pdfsToProcess != null) {
			for (File pdf : pdfsToProcess) {
				if (FileUtils.hasExtNoCase(pdf.getName(), GUIConstants.ZIP)) {
					logger.log(Level.WARNING, "Fixing metadata are not supported for zip processing, if save folder isn't defined");
					this.fixMetadata.setSelected(false);
					break;
				}
			}
		}
		if (isFixMetadata()) {
			selectedItem = ProcessType.addProcess(selectedItem, ProcessType.FIX);
		}
		builder.type(selectedItem).policyFile(this.getPolicyFile());
		return builder.build();
	}

	private PDFAFlavour getCurrentFlavour() {
		String selectedItem = (String) this.chooseFlavour.getSelectedItem();
		if (GUIConstants.AUTO_ARLINGTON_FLAVOUR_COMBOBOX_TEXT.equals(selectedItem)) {
			return PDFAFlavour.NO_ARLINGTON_FLAVOUR;
		}
		PDFAFlavour flavour = FLAVOURS_MAP.get(selectedItem);
		return flavour == null ? PDFAFlavour.NO_FLAVOUR : flavour;
	}

	private boolean isExecute() {
		return (this.pdfsToProcess != null
				&& (!this.profilePath.toString().isEmpty()
				|| !this.chooseFlavour.getSelectedItem().equals(GUIConstants.CUSTOM_PROFILE_COMBOBOX_TEXT))
				&& (this.ProcessTypes.getSelectedItem() != ProcessType.POLICY || this.policy != null));
	}

	private boolean isFixMetadata() {
		return this.fixMetadata.isSelected();
	}

	protected static String getFlavourReadableText(PDFAFlavour flavour) {
		if (PDFAFlavour.Specification.ISO_32000_1_0.getFamily().equals(flavour.getPart().getFamily()) ||
				PDFAFlavour.Specification.ISO_32000_2_0.getFamily().equals(flavour.getPart().getFamily())) {
			return flavour.getId().substring(PDFAFlavours.ARLINGTON_PREFIX.length());
		}
		return String.format(flavour.getPart().getFamily() + "-%d%S", flavour.getPart().getPartNumber(), //$NON-NLS-1$
				flavour.getLevel().getCode());
	}

	public String getPolicyFile() {
		return this.policy == null ? "" : this.policy.getAbsolutePath();
	}

	void setPolicyFile(File policy) {
		if (policy != null && policy.isFile() && policy.canRead()) {
			this.policy = policy;
			this.policyChooser.setSelectedFile(policy);
			this.chosenPolicy.setText(policy.getAbsolutePath());
			this.execute.setEnabled(isExecute());
		}
	}

	private class ProcessingTypeRenderer extends JLabel implements ListCellRenderer<ProcessType> {

		/**
		 *
		 */
		private static final long serialVersionUID = -2841316639915833315L;

		public ProcessingTypeRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends ProcessType> list, ProcessType value, int index,
													  boolean isSelected, boolean cellHasFocus) {
			this.setText(value.getValue());
			return this;
		}
	}

	public class PanelDropTargetListener extends DropTargetAdapter {
		private final Logger LOGGER = Logger.getLogger(PanelDropTargetListener.class.getCanonicalName());

		private static final String UNSUPPORTED_FLAVOUR = "Unsupported flavour error.";
		private static final String CASTING_ERROR = "Casting transfer to files type error.";

		private int acceptableFilesCount;

		private List<File> selectedFiles = new ArrayList<>();

		private String[] acceptableExtensions;

		public PanelDropTargetListener(int acceptableFilesCount, String...acceptableExtensions) {
			this.acceptableFilesCount = acceptableFilesCount;
			this.acceptableExtensions = acceptableExtensions;
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
			}
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
				try {
					dtde.acceptDrop(dtde.getDropAction());
					selectedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					if (isAcceptableTransferData(selectedFiles)) {
						dropFiles(selectedFiles);
					}
				} catch (UnsupportedFlavorException e) {
					LOGGER.warning(UNSUPPORTED_FLAVOUR);
				} catch (IOException e) {
					LOGGER.warning(CASTING_ERROR);
				}
			} else {
				dtde.rejectDrop();
			}
		}

		private boolean isAcceptableTransferData(List<File> selectedFiles) {
			// "-1" indicates that we can take any non-zero files or dirs
			if (acceptableFilesCount != -1 && acceptableFilesCount != selectedFiles.size()) {
				return false;
			} else if (selectedFiles.size() == 0){
				return false;
			}
			return ApplicationUtils.isLegalExtension(selectedFiles, acceptableExtensions);
		}

		private void dropFiles(List<File> selectedFiles) {
			CheckerPanel.this.insertFilesInfo(selectedFiles, acceptableExtensions);
		}
	}
}
