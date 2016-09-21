package org.verapdf.gui;

import org.apache.log4j.Logger;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.processor.ProcessingResult;
import org.verapdf.processor.config.Config;
import org.verapdf.processor.config.ConfigIO;
import org.verapdf.processor.config.ProcessingType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * Panel with functionality for checker.
 *
 * @author Maksim Bezrukov
 */
class CheckerPanel extends JPanel {

	/**
	 * ID for serialisation
	 */
	private static final long serialVersionUID = 1290058869994329766L;

	static final Logger LOGGER = Logger.getLogger(CheckerPanel.class);

	private JFileChooser pdfChooser;
	private JFileChooser xmlChooser;
	private JFileChooser htmlChooser;
	private File pdfFile;
	private JTextField chosenPDF;
	private JTextField chosenProfile;
	private JLabel resultLabel;
	private File xmlReport;
	private File htmlReport;

	private JComboBox<ProcessingType> processingType;
	private JCheckBox fixMetadata;
	private JComboBox<PDFAFlavour> chooseFlavour;

	private boolean isValidationErrorOccurred;

	private JButton validate;
	private JButton saveXML;
	private JButton viewXML;
	private JButton saveHTML;
	private JButton viewHTML;

	private transient Config config;

	JProgressBar progressBar;
	transient ValidateWorker validateWorker;

	CheckerPanel(final Config config) throws IOException {

		this.config = config;
		setPreferredSize(new Dimension(GUIConstants.PREFERRED_SIZE_WIDTH,
				GUIConstants.PREFERRED_SIZE_HEIGHT));

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		this.chosenPDF = new JTextField(GUIConstants.PDF_NOT_CHOSEN_TEXT);
		this.chosenPDF.setEditable(false);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOSEN_PDF_LABEL_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.chosenPDF, gbc);
		this.add(this.chosenPDF);

		JButton choosePDF = new JButton(GUIConstants.CHOOSE_PDF_BUTTON_TEXT);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(choosePDF, gbc);
		this.add(choosePDF);

		final JLabel processType = new JLabel(GUIConstants.PROCESSING_TYPE);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.PROCESS_TYPE_LABEL_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(processType, gbc);
		this.add(processType);

		this.processingType = new JComboBox<>(ProcessingType.values());
		processingType.setSelectedItem(config.getProcessingType());
		ProcessingTypeRenderer processingTypeRenderer = new ProcessingTypeRenderer();
		processingType.setRenderer(processingTypeRenderer);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_X,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_Y,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_WEIGHT_X,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_WEIGHT_Y,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_WIDTH,
				GUIConstants.PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.processingType, gbc);
		this.add(this.processingType);

		this.fixMetadata = new JCheckBox(GUIConstants.FIX_METADATA_LABEL_TEXT);
		this.fixMetadata.setSelected(config.isFixMetadata());
		setGridBagConstraintsParameters(gbc,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_X,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_Y,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_WEIGHT_X,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_WEIGHT_Y,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_WIDTH,
				GUIConstants.FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.fixMetadata, gbc);
		this.add(this.fixMetadata);
		if (config.getProcessingType() == ProcessingType.FEATURES) {
			this.fixMetadata.setEnabled(false);
		}

		Vector<PDFAFlavour> availableFlavours = new Vector<>();
		availableFlavours.add(PDFAFlavour.NO_FLAVOUR);
		availableFlavours.add(PDFAFlavour.AUTO);
		for (PDFAFlavour flavour : PDFAFlavour.values()) {
			Set<PDFAFlavour> currentFlavours
					= Profiles.getVeraProfileDirectory().getPDFAFlavours();
			if (currentFlavours.contains(flavour)) {
				availableFlavours.add(flavour);
			}
		}
		chooseFlavour = new JComboBox<>(availableFlavours);
		ChooseFlavourRenderer renderer = new ChooseFlavourRenderer();
		chooseFlavour.setRenderer(renderer);
		PDFAFlavour fromConfig = config.getFlavour();
		if (availableFlavours.contains(fromConfig)) {
			chooseFlavour.setSelectedItem(fromConfig);
		} else {
			chooseFlavour.setSelectedItem(PDFAFlavour.PDFA_1_B);
		}
		setGridBagConstraintsParameters(gbc,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(chooseFlavour, gbc);
		this.add(chooseFlavour);

		this.chosenProfile = new JTextField(
				GUIConstants.VALIDATION_PROFILE_NOT_CHOSEN);
		this.chosenProfile.setEditable(false);
		chosenProfile.setEnabled(false);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.chosenProfile, gbc);
		this.add(this.chosenProfile);
		if (!this.config.getValidationProfile().toString().equals("")) {
			this.chosenProfile.setText(
					this.config.getValidationProfile().toAbsolutePath().toString());
		} else {
			this.chosenProfile.setText(GUIConstants.CHOOSEN_PROFILE_TEXTFIELD_DEFAULT_TEXT);
		}

		final JButton chooseProfile = new JButton(
				GUIConstants.CHOOSE_PROFILE_BUTTON_TEXT);
		chooseProfile.setEnabled(false);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_Y,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_WEIGHT_Y,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(chooseProfile, gbc);
		this.add(chooseProfile);

		this.resultLabel = new JLabel();
		this.resultLabel.setForeground(GUIConstants.BEFORE_VALIDATION_COLOR);
		this.resultLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_X,
				GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_Y,
				GUIConstants.RESULT_LABEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.RESULT_LABEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.RESULT_LABEL_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.CENTER);
		gbl.setConstraints(this.resultLabel, gbc);
		this.add(this.resultLabel);

		this.progressBar = new JProgressBar();
		this.progressBar.setIndeterminate(true);
		this.progressBar.setVisible(false);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_X,
				GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_Y,
				GUIConstants.PROGRESSBAR_CONSTRAINT_WEIGHT_X,
				GUIConstants.PROGRESSBAR_CONSTRAINT_WEIGHT_Y,
				GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_WIDTH,
				GUIConstants.PROGRESSBAR_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.progressBar, gbc);
		this.add(this.progressBar);

		this.validate = new JButton(GUIConstants.VALIDATE_BUTTON_TEXT);
		this.validate.setEnabled(false);
		setGridBagConstraintsParameters(gbc,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_X,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_Y,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_WEIGHT_X,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_WEIGHT_Y,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_WIDTH,
				GUIConstants.VALIDATE_BUTTON_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(this.validate, gbc);
		this.add(this.validate);

		JPanel reports = new JPanel();
		reports.setBorder(BorderFactory.createTitledBorder(GUIConstants.REPORT));
		reports.setLayout(new GridLayout(
				GUIConstants.REPORT_PANEL_LINES_NUMBER,
				GUIConstants.REPORT_PANEL_COLUMNS_NUMBER));
		setGridBagConstraintsParameters(gbc,
				GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_X,
				GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_Y,
				GUIConstants.REPORT_PANEL_CONSTRAINT_WEIGHT_X,
				GUIConstants.REPORT_PANEL_CONSTRAINT_WEIGHT_Y,
				GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_WIDTH,
				GUIConstants.REPORT_PANEL_CONSTRAINT_GRID_HEIGHT,
				GridBagConstraints.HORIZONTAL);
		gbl.setConstraints(reports, gbc);
		this.add(reports);

		LogoPanel xmlLogo = new LogoPanel(GUIConstants.XML_LOGO_NAME,
				reports.getBackground(), GUIConstants.XML_LOGO_BORDER_WIDTH);
		reports.add(xmlLogo);

		this.saveXML = new JButton(GUIConstants.SAVE_REPORT_BUTTON_TEXT);
		this.saveXML.setEnabled(false);
		reports.add(this.saveXML);

		this.viewXML = new JButton(GUIConstants.VIEW_REPORT_BUTTON_TEXT);
		this.viewXML.setEnabled(false);
		reports.add(this.viewXML);

		LogoPanel htmlLogo = new LogoPanel(GUIConstants.HTML_LOGO_NAME,
				reports.getBackground(), GUIConstants.HTML_LOGO_BORDER_WIDTH);
		reports.add(htmlLogo);

		this.saveHTML = new JButton(GUIConstants.SAVE_HTML_REPORT_BUTTON_TEXT);
		this.saveHTML.setEnabled(false);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		reports.add(this.saveHTML);

		this.viewHTML = new JButton(GUIConstants.VIEW_HTML_REPORT_BUTTON_TEXT);
		this.viewHTML.setEnabled(false);
		reports.add(this.viewHTML);

		this.pdfChooser = getChooser(GUIConstants.PDF);
		this.xmlChooser = getChooser(GUIConstants.XML);
		this.htmlChooser = getChooser(GUIConstants.HTML);

		choosePDF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CheckerPanel.this.chooseFile(CheckerPanel.this.pdfChooser, GUIConstants.PDF);
			}
		});

		this.processingType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProcessingType item =
						(ProcessingType) CheckerPanel.this.processingType.getSelectedItem();
				switch (item) {
					case VALIDATION:
						CheckerPanel.this.fixMetadata.setEnabled(true);
						break;
					case VALIDATION_AND_FEATURES:
						CheckerPanel.this.fixMetadata.setEnabled(true);
						break;
					case FEATURES:
						CheckerPanel.this.fixMetadata.setEnabled(false);
						break;
					default:
						break;
				}
			}
		});

		chooseFlavour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				CheckerPanel.this.config.setFlavour(
						(PDFAFlavour) chooseFlavour.getSelectedItem());
				if (chooseFlavour.getSelectedItem() == PDFAFlavour.NO_FLAVOUR) {
					chooseProfile.setEnabled(true);
					chosenProfile.setEnabled(true);
				} else if (chooseFlavour.getSelectedItem() != PDFAFlavour.NO_FLAVOUR) {
					chooseProfile.setEnabled(false);
					chosenProfile.setEnabled(false);
				}
				setValidationButtonEnability();
			}
		});

		chooseProfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CheckerPanel.this.chooseFile(CheckerPanel.this.xmlChooser, GUIConstants.XML);
			}
		});

		this.validate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					changeConfig();
					CheckerPanel.this.validateWorker = new ValidateWorker(
							CheckerPanel.this, CheckerPanel.this.pdfFile,
							CheckerPanel.this.config);
					CheckerPanel.this.progressBar.setVisible(true);
					CheckerPanel.this.resultLabel.setVisible(false);
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					CheckerPanel.this.validate.setEnabled(false);
					CheckerPanel.this.isValidationErrorOccurred = false;
					CheckerPanel.this.viewXML.setEnabled(false);
					CheckerPanel.this.saveXML.setEnabled(false);
					CheckerPanel.this.viewHTML.setEnabled(false);
					CheckerPanel.this.saveHTML.setEnabled(false);
					CheckerPanel.this.validateWorker.execute();
				} catch (IllegalArgumentException exep) {
					JOptionPane.showMessageDialog(CheckerPanel.this, exep.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					LOGGER.error(exep);
				}
			}
		});

		this.saveXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveReport(CheckerPanel.this.xmlChooser, GUIConstants.XML, CheckerPanel.this.xmlReport);
			}
		});

		this.saveHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveReport(CheckerPanel.this.htmlChooser, GUIConstants.HTML, CheckerPanel.this.htmlReport);
			}
		});

		this.viewXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CheckerPanel.this.xmlReport == null) {
					JOptionPane.showMessageDialog(CheckerPanel.this,
							"XML report hasn't been saved.",
							GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
				} else {
					this.openXMLReport();
				}
			}

			private void openXMLReport() {
				try {
					Desktop.getDesktop().open(CheckerPanel.this.xmlReport);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(CheckerPanel.this,
							"Some error in opening the XML report.",
							GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
					LOGGER.error("Exception in opening the XML report", e1);
				}
			}
		});

		this.viewHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CheckerPanel.this.htmlReport == null) {
					JOptionPane.showMessageDialog(CheckerPanel.this,
							"HTML report hasn't been saved.",
							GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						Desktop.getDesktop().open(CheckerPanel.this.htmlReport);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(CheckerPanel.this,
								"Some error in opening the HTML report.",
								GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
						LOGGER.error("Exception in opening the HTML report", e1);
					}
				}
			}
		});

	}

	void validationEnded(File xmlReport, File htmlReport) {

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.progressBar.setVisible(false);
		this.validate.setEnabled(true);

		if (!this.isValidationErrorOccurred) {
			try {
				ProcessingResult result = this.validateWorker.get();
				ProcessingResult.ValidationSummary validationSummary = result.getValidationSummary();
				if (validationSummary == ProcessingResult.ValidationSummary.ERROR_IN_VALIDATION) {
					this.resultLabel.setForeground(GUIConstants.VALIDATION_FAILED_COLOR);
					this.resultLabel.setText(GUIConstants.ERROR_IN_VALIDATING);
				} else if (validationSummary == ProcessingResult.ValidationSummary.FILE_VALID) {
					this.resultLabel.setForeground(GUIConstants.VALIDATION_SUCCESS_COLOR);
					this.resultLabel.setText(GUIConstants.VALIDATION_OK);
				} else if (validationSummary == ProcessingResult.ValidationSummary.FILE_NOT_VALID) {
					this.resultLabel.setForeground(GUIConstants.VALIDATION_FAILED_COLOR);
					this.resultLabel.setText(GUIConstants.VALIDATION_FALSE);
				} else if (result.getFeaturesSummary() == ProcessingResult.FeaturesSummary.FEATURES_SUCCEED) {
					// If we did not obtain validation result, then it was features process
					this.resultLabel.setForeground(GUIConstants.BEFORE_VALIDATION_COLOR);
					this.resultLabel.setText(GUIConstants.FEATURES_GENERATED_CORRECT);
				} else {
					// If it was features process and it has not finished with succeed result,
					// then it was features process with exception result
					this.resultLabel.setForeground(GUIConstants.VALIDATION_FAILED_COLOR);
					this.resultLabel.setText(GUIConstants.ERROR_IN_FEATURES);
				}

				this.resultLabel.setVisible(true);

				this.xmlReport = xmlReport;
				this.htmlReport = htmlReport;

				if (xmlReport != null) {
					this.saveXML.setEnabled(true);
					this.viewXML.setEnabled(true);
				}

				if (htmlReport != null) {
					this.saveHTML.setEnabled(true);
					this.viewHTML.setEnabled(true);
				}
			} catch (InterruptedException e) {
				errorInValidatingOccur("Process has been interrupted: ", e);
			} catch (ExecutionException e) {
				errorInValidatingOccur("Execution exception in processing: ", e);
			}
		}

	}

	void errorInValidatingOccur(String message, Throwable e) {
		LOGGER.error(e);
		e.printStackTrace();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.progressBar.setVisible(false);
		this.isValidationErrorOccurred = true;
		JOptionPane.showMessageDialog(CheckerPanel.this, message + e.getMessage(),
				GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);

		this.resultLabel.setForeground(GUIConstants.VALIDATION_FAILED_COLOR);
		this.resultLabel.setText(message + e.getMessage());
		this.resultLabel.setVisible(true);
	}

	private static JFileChooser getChooser(String type) throws IOException {
		JFileChooser res = new JFileChooser();
		File currentDir = new File(
				new File(GUIConstants.DOT).getCanonicalPath());
		res.setCurrentDirectory(currentDir);
		res.setAcceptAllFileFilterUsed(false);
		res.setFileFilter(new FileNameExtensionFilter(type, type));
		return res;
	}

	private static void setGridBagConstraintsParameters(GridBagConstraints gbc,
														int gridx, int gridy, int weightx, int weighty, int gridwidth,
														int gridheight, int fill) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.fill = fill;
	}

	private void chooseFile(JFileChooser chooser, String extension) {
		int resultChoose = chooser.showOpenDialog(CheckerPanel.this);
		if (resultChoose == JFileChooser.APPROVE_OPTION) {

			if (!chooser.getSelectedFile().exists()) {
				JOptionPane.showMessageDialog(CheckerPanel.this,
						"Error. Selected file doesn't exist.",
						GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			} else if (!chooser.getSelectedFile().getName().toLowerCase()
					.endsWith(GUIConstants.DOT + extension.toLowerCase())) {
				JOptionPane.showMessageDialog(
						CheckerPanel.this,
						"Error. Selected file is not in "
								+ extension.toUpperCase() + " format.",
						GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			} else {

				this.resultLabel.setForeground(GUIConstants.BEFORE_VALIDATION_COLOR);
				this.resultLabel.setText("");
				this.xmlReport = null;
				this.htmlReport = null;
				this.saveXML.setEnabled(false);
				this.viewXML.setEnabled(false);
				this.saveHTML.setEnabled(false);
				this.viewHTML.setEnabled(false);

				switch (extension) {
					case GUIConstants.PDF:
						this.pdfFile = chooser.getSelectedFile();
						this.chosenPDF.setText(this.pdfFile.getAbsolutePath());
						break;
					case GUIConstants.XML:
						this.config.setValidationProfilePath(
								chooser.getSelectedFile().toPath().toAbsolutePath());
						this.chosenProfile.setText(
								this.config.getValidationProfile().toString());
						break;
					default:
						// This method used only for previous two cases.
						// So do nothing.
				}
				setValidationButtonEnability();
			}
		}
	}

	private void saveReport(JFileChooser chooser, String extension, File report) {
		if (report == null) {
			JOptionPane.showMessageDialog(CheckerPanel.this,
					"Validation hasn't been run.", GUIConstants.ERROR,
					JOptionPane.ERROR_MESSAGE);
		} else {
			chooser.setSelectedFile(new File(extension.toLowerCase()
					+ "Report." + extension.toLowerCase()));
			int resultChoose = chooser.showSaveDialog(CheckerPanel.this);
			if (resultChoose == JFileChooser.APPROVE_OPTION) {
				File temp = chooser.getSelectedFile();

				if (!(temp.getName().toLowerCase().endsWith(GUIConstants.DOT
						+ extension.toLowerCase()))) {
					temp = new File(temp.getPath() + GUIConstants.DOT
							+ extension.toLowerCase());
				}

				try {
					try {
						Files.copy(report.toPath(), temp.toPath());
					} catch (FileAlreadyExistsException e1) {
						LOGGER.debug(
								"File already exists, conform overwrite with user",
								e1);
						int resultOption = JOptionPane
								.showConfirmDialog(
										CheckerPanel.this,
										extension.toUpperCase()
												+ " file with the same name already exists. Do you want to overwrite it?",
										"", JOptionPane.YES_NO_OPTION);
						if (resultOption == JOptionPane.YES_OPTION) {
							Files.copy(report.toPath(), temp.toPath(),
									StandardCopyOption.REPLACE_EXISTING);
						}
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(CheckerPanel.this,
							GUIConstants.ERROR_IN_SAVING_HTML_REPORT + e.getMessage(),
							GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
					LOGGER.error("Exception saving " + extension.toUpperCase()
							+ " report", e);
				}
			}
		}
	}

	private void changeConfig() {
		if (this.chooseFlavour.getSelectedItem() != PDFAFlavour.NO_FLAVOUR) {
			this.config.setValidationProfilePath(FileSystems.getDefault().getPath(""));
		}
		this.config.setProcessingType(
				(ProcessingType) processingType.getSelectedItem());
		this.config.setFixMetadata(fixMetadata.isSelected());
		this.config.setFlavour(
				(PDFAFlavour) this.chooseFlavour.getSelectedItem());
		ConfigIO.writeConfig(this.config);
	}

	private void setValidationButtonEnability() {
		if (this.pdfFile != null &&
				(!this.config.getValidationProfile().toString().equals("") ||
						this.chooseFlavour.getSelectedItem() != PDFAFlavour.NO_FLAVOUR)) {
			validate.setEnabled(true);
		} else {
			validate.setEnabled(false);
		}
	}

	boolean isFixMetadata() {
		return fixMetadata.isSelected();
	}

	ProcessingType getProcessingType() {
		return (ProcessingType) processingType.getSelectedItem();
	}


	private class ChooseFlavourRenderer extends JLabel implements ListCellRenderer<PDFAFlavour> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3740801661593829099L;

		public ChooseFlavourRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends PDFAFlavour> list, PDFAFlavour value,
													  int index, boolean isSelected, boolean cellHasFocus) {
			if (value == PDFAFlavour.NO_FLAVOUR) {
				this.setText(GUIConstants.CUSTOM_PROFILE_COMBOBOX_TEXT);
				return this;
			} else if (value == PDFAFlavour.AUTO) {
				this.setText(GUIConstants.AUTO_FLAVOUR_COMBOBOX_TEXT);
				return this;
			} else if (value.toString().matches("\\d\\w")) {
				String valueString = value.toString();
				String parsedFlavour = "PDF/A-";
				parsedFlavour += valueString.charAt(0);
				parsedFlavour += valueString.substring(1, 2).toUpperCase();
				this.setText(parsedFlavour);
				return this;
			} else {
				this.setText("Error in parsing flavour");
				return this;
			}
		}
	}

	private class ProcessingTypeRenderer extends JLabel implements ListCellRenderer<ProcessingType> {

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
		public Component getListCellRendererComponent(JList<? extends ProcessingType> list,
													  ProcessingType value,
													  int index, boolean isSelected,
													  boolean cellHasFocus) {
			this.setText(value.toText());
			return this;
		}
	}
}
