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
package org.verapdf.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.verapdf.ReleaseDetails;
import org.verapdf.apps.Applications;
import org.verapdf.apps.Applications.Builder;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.PdfBoxFoundryProvider;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;

/**
 * Main frame of the PDFA Conformance Checker
 *
 * @author Maksim Bezrukov
 */

public class PDFValidationApplication extends JFrame {
	static final ConfigManager configManager = Applications.createAppConfigManager();

	class ExitWindowAdapter extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				configManager.updateAppConfig(config);
			} catch (JAXBException | IOException excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			}
		}
	}

	private static final long serialVersionUID = -5569669411392145783L;

	private static final Logger LOGGER = Logger.getLogger(PDFValidationApplication.class);

	private AboutPanel aboutPanel;
	private SettingsPanel settingsPanel;
	private FeaturesConfigPanel featuresPanel;
	private CheckerPanel checkerPanel;
	private VeraAppConfig config;
	private PolicyPanel policyConfig;

	private PDFValidationApplication() {
		addWindowListener(new ExitWindowAdapter());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(GUIConstants.FRAME_COORD_X, GUIConstants.FRAME_COORD_Y, GUIConstants.FRAME_WIDTH,
				GUIConstants.FRAME_HEIGHT);
		setResizable(false);

		setTitle(GUIConstants.TITLE);

		config = configManager.getApplicationConfig();

		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setJMenuBar(menuBar);

		aboutPanel = null;
		try {
			aboutPanel = new AboutPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error in reading logo image.", GUIConstants.ERROR,
					JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in reading logo image", e);
		}

		final JMenu file = new JMenu("File");
		menuBar.add(file);

		try {
			settingsPanel = new SettingsPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error initialising settings panel.",
					GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in initialising settings panel", e);
		}

		final JMenuItem sett = new JMenuItem("Settings");
		sett.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (settingsPanel != null && settingsPanel.showDialog(PDFValidationApplication.this, "Settings",
						configManager)) {
					Builder confBuilder = Builder
							.fromConfig(configManager.getApplicationConfig());
					confBuilder.wikiPath(settingsPanel.getProfilesWikiPath());
					confBuilder.maxFails(settingsPanel.getFailedChecksDisplayNumber());
					confBuilder.fixerFolder(settingsPanel.getFixMetadataDirectory().toString());
					try {
						configManager.updateAppConfig(confBuilder.build());
					} catch (JAXBException | IOException excep) {
						// TODO Auto-generated catch block
						excep.printStackTrace();
					}

					ValidatorConfig validConf = ValidatorFactory.createConfig(
							configManager.getValidatorConfig().getFlavour(), settingsPanel.isDispPassedRules(),
							settingsPanel.getFailedChecksNumber());
					try {
						configManager.updateValidatorConfig(validConf);
					} catch (JAXBException | IOException excep) {
						// TODO Auto-generated catch block
						excep.printStackTrace();
					}

					MetadataFixerConfig fixConf = FixerFactory.configFromValues(settingsPanel.getFixMetadataPrefix(), true);
					try {
						configManager.updateFixerConfig(fixConf);
					} catch (JAXBException | IOException excep) {
						// TODO Auto-generated catch block
						excep.printStackTrace();
					}

				}
			}
		});

		file.add(sett);

		file.addSeparator();

		final JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				PDFValidationApplication.this
						.processWindowEvent(new WindowEvent(PDFValidationApplication.this, WindowEvent.WINDOW_CLOSING));
			}
		});

		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		file.add(quit);

		final JMenu policy = new JMenu("Configurations");

		featuresPanel = new FeaturesConfigPanel();

		final JMenuItem features = new JMenuItem("Features Config");
		features.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (featuresPanel != null && featuresPanel.showDialog(PDFValidationApplication.this, "Features Config",
						configManager.getFeaturesConfig())) {
					try {
						configManager.updateFeaturesConfig(featuresPanel.getFeaturesConfig());
					} catch (JAXBException | IOException exp) {
						LOGGER.error("Exception in updating features config", exp);
					}
				}
			}
		});

		menuBar.add(policy);
		policy.add(features);

		policyConfig = new PolicyPanel();
		final JMenuItem policyPanel = new JMenuItem("Policy Config");
		policyPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (policyConfig != null && policyConfig.showDialog(PDFValidationApplication.this)) {
					try {
						JFileChooser jfc = new JFileChooser(new File(GUIConstants.DOT).getCanonicalPath());
						jfc.showDialog(PDFValidationApplication.this, "Save policy config file");
						PDFValidationApplication.this.policyConfig.setPoilcyFile(
								jfc.getSelectedFile());
						policyConfig.writeSchematronFile();
						PDFValidationApplication.this.checkerPanel.setPolicyFile(
								policyConfig.getPolicyFile());
					} catch (IOException | XMLStreamException ex) {
						JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in saving policy config file.",
								GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
						LOGGER.error("Error in saving policy config file.", ex);
					}
				}
			}
		});

		policy.add(policyPanel);

		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (aboutPanel != null) {
					aboutPanel.showDialog(PDFValidationApplication.this, "About veraPDF");
				}
			}
		});

		menuBar.add(about);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS,
				GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);

		MiniLogoPanel logoPanel = null;
		try {
			logoPanel = new MiniLogoPanel(GUIConstants.LOGO_NAME);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in creating mini logo.",
					GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in creating mini logo", e);
		}

		contentPane.add(logoPanel);

		checkerPanel = null;
		try {
			checkerPanel = new CheckerPanel(configManager);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in loading xml or html image.",
					GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in loading xml or html image", e);
		}
		contentPane.add(checkerPanel);

	}

	/**
	 * Starting point of the gui
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		PdfBoxFoundryProvider.initialise();
		ReleaseDetails.addDetailsFromResource(
				ReleaseDetails.APPLICATION_PROPERTIES_ROOT + "app." + ReleaseDetails.PROPERTIES_EXT);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException
						| InstantiationException e) {
					LOGGER.error("Exception in configuring UI manager", e);
				}
				try {
					PDFValidationApplication frame = new PDFValidationApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					LOGGER.error("Exception", e);
				}
			}
		});
	}

}