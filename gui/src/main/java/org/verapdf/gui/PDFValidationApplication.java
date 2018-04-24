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

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.verapdf.ReleaseDetails;
import org.verapdf.apps.Applications;
import org.verapdf.apps.Applications.Builder;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.apps.ConfigManager;
import org.verapdf.apps.SoftwareUpdater;
import org.verapdf.apps.VeraAppConfig;
import org.verapdf.metadata.fixer.FixerFactory;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
import org.verapdf.processor.FeaturesPluginsLoader;

/**
 * Main frame of the PDFA Conformance Checker
 *
 * @author Maksim Bezrukov
 */

@SuppressWarnings("synthetic-access")
public class PDFValidationApplication extends JFrame {
	private static final ConfigManager configManager = Applications.createAppConfigManager();
	private static final long serialVersionUID = -5569669411392145783L;
	private static final Logger logger = Logger.getLogger(PDFValidationApplication.class.getCanonicalName());

	private AboutPanel aboutPanel;
	private SettingsPanel settingsPanel;
	private FeaturesConfigPanel featuresPanel;
	private CheckerPanel checkerPanel;
	private VeraAppConfig config;
	private PolicyPanel policyConfig;

	private PDFValidationApplication(double frameScale) {
		addWindowListener(new ExitWindowAdapter());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(GUIConstants.FRAME_COORD_X, GUIConstants.FRAME_COORD_Y, (int) (GUIConstants.FRAME_WIDTH*frameScale),
				(int) (GUIConstants.FRAME_HEIGHT*frameScale));
		setResizable(false);

		setTitle(GUIConstants.TITLE);

		this.config = configManager.getApplicationConfig();

		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setJMenuBar(menuBar);

		this.aboutPanel = null;
		try {
			this.aboutPanel = new AboutPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error in reading logo image.", GUIConstants.ERROR,
					JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "Exception in reading logo image", e);
		}

		final JMenu file = new JMenu("File");
		menuBar.add(file);

		try {
			this.settingsPanel = new SettingsPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error initialising settings panel.",
					GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			logger.log(Level.SEVERE, "Exception in initialising settings panel", e);
		}

		final JMenuItem sett = new JMenuItem("Settings");
		sett.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PDFValidationApplication.this.settingsPanel != null && PDFValidationApplication.this.settingsPanel
						.showDialog(PDFValidationApplication.this, "Settings", configManager)) {
					Builder confBuilder = Builder.fromConfig(configManager.getApplicationConfig());
					confBuilder.wikiPath(PDFValidationApplication.this.settingsPanel.getProfilesWikiPath());
					confBuilder.maxFails(PDFValidationApplication.this.settingsPanel.getFailedChecksDisplayNumber());
					confBuilder.fixerFolder(
							PDFValidationApplication.this.settingsPanel.getFixMetadataDirectory().toString());
					try {
						configManager.updateAppConfig(confBuilder.build());
					} catch (JAXBException | IOException excep) {
						// TODO Auto-generated catch block
						excep.printStackTrace();
					}

					ValidatorConfig validConf = ValidatorFactory.createConfig(
							configManager.getValidatorConfig().getFlavour(),
							PDFValidationApplication.this.settingsPanel.isDispPassedRules(),
							PDFValidationApplication.this.settingsPanel.getFailedChecksNumber());
					try {
						configManager.updateValidatorConfig(validConf);
					} catch (JAXBException | IOException excep) {
						// TODO Auto-generated catch block
						excep.printStackTrace();
					}

					MetadataFixerConfig fixConf = FixerFactory
							.configFromValues(PDFValidationApplication.this.settingsPanel.getFixMetadataPrefix(), true);
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

		final JMenu policy = new JMenu("Configs");

		this.featuresPanel = new FeaturesConfigPanel();

		final JMenuItem features = new JMenuItem("Features Config");
		features.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PDFValidationApplication.this.featuresPanel != null
						&& PDFValidationApplication.this.featuresPanel.showDialog(PDFValidationApplication.this,
								"Features Config", configManager.getFeaturesConfig())) {
					try {
						configManager
								.updateFeaturesConfig(PDFValidationApplication.this.featuresPanel.getFeaturesConfig());
					} catch (JAXBException | IOException exp) {
						logger.log(Level.SEVERE, "Exception in updating features config", exp); //$NON-NLS-1$
					}
				}
			}
		});

		menuBar.add(policy);
		policy.add(features);

		this.policyConfig = new PolicyPanel();
		final JMenuItem policyPanel = new JMenuItem("Policy Config");
		policyPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PDFValidationApplication.this.policyConfig != null
						&& PDFValidationApplication.this.policyConfig.showDialog(PDFValidationApplication.this)) {
					try {
						File jfcStartingPath = PDFValidationApplication.this.policyConfig.getPolicyFile();
						if (jfcStartingPath == null) {
							jfcStartingPath = new File(GUIConstants.DOT);
						}
						JFileChooser jfc = new JFileChooser(jfcStartingPath.getCanonicalPath());
						int dialogRes = jfc.showDialog(PDFValidationApplication.this, "Save policy config file");
						if (dialogRes == JFileChooser.APPROVE_OPTION) {
							PDFValidationApplication.this.policyConfig.setPolicyFile(jfc.getSelectedFile());
							PDFValidationApplication.this.policyConfig.writeSchematronFile();
							PDFValidationApplication.this.checkerPanel
									.setPolicyFile(PDFValidationApplication.this.policyConfig.getPolicyFile());
						}
					} catch (IOException | XMLStreamException ex) {
						JOptionPane.showMessageDialog(PDFValidationApplication.this,
								"Error in saving policy config file.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
						logger.log(Level.SEVERE, "Error in saving policy config file.", ex);
					}
				}
			}
		});

		policy.add(policyPanel);

		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PDFValidationApplication.this.aboutPanel != null) {
					PDFValidationApplication.this.aboutPanel.showDialog(PDFValidationApplication.this, "About veraPDF");
				}
			}
		});

		JMenuItem checkForUpdates = new JMenuItem(GUIConstants.CHECK_FOR_UPDATES_TEXT);
		checkForUpdates.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SoftwareUpdater updater = Applications.softwareUpdater();
				if (!updater.isOnline()) {
					JOptionPane.showMessageDialog(
							PDFValidationApplication.this,
							Applications.UPDATE_SERVICE_NOT_AVAILABLE,
							GUIConstants.CHECK_FOR_UPDATES_TEXT,
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				ReleaseDetails details = ReleaseDetails.byId("gui");
				if (updater.isUpdateAvailable(details)) {
					int res = JOptionPane.showConfirmDialog(
							PDFValidationApplication.this,
							String.format(
									Applications.UPDATE_OLD_VERSION,
									details.getVersion(), updater.getLatestVersion(details))
							+ String.format("Do you want to download the latest version from:\n%s?",
							Applications.UPDATE_URI),
							GUIConstants.CHECK_FOR_UPDATES_TEXT,
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (res == JOptionPane.YES_OPTION) {
						attemptURIOpen(Applications.UPDATE_URI);
					}
				} else {
					JOptionPane.showMessageDialog(
							PDFValidationApplication.this,
							String.format(Applications.UPDATE_LATEST_VERSION, "\n", details.getVersion()),
							GUIConstants.CHECK_FOR_UPDATES_TEXT,
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		JMenuItem guiHelp = new JMenuItem("GUI");
		guiHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PDFValidationApplication.this.attemptURIOpen(GUIConstants.DOCS_GUI_LINK_URL);
			}
		});

		JMenuItem validationHelp = new JMenuItem("Validation");
		validationHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PDFValidationApplication.this.attemptURIOpen(GUIConstants.DOCS_VALIDATION_LINK_URL);
			}
		});

		JMenuItem policyHelp = new JMenuItem("Policy");
		policyHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PDFValidationApplication.this.attemptURIOpen(GUIConstants.DOCS_POLICY_LINK_URL);
			}
		});

		JMenu help = new JMenu("Help");
		help.add(guiHelp);
		help.add(validationHelp);
		help.add(policyHelp);
		help.addSeparator();
		help.add(checkForUpdates);
		help.add(about);

		menuBar.add(help);

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
			logger.log(Level.WARNING, "Exception in creating mini logo", e);
		}

		contentPane.add(logoPanel);

		this.checkerPanel = null;
		try {
			this.checkerPanel = new CheckerPanel(configManager);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in loading xml or html image.",
					GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			logger.log(Level.WARNING, "Exception in loading xml or html image", e);
		}
		contentPane.add(this.checkerPanel);

	}

	private void attemptURIOpen(String uri) {
		try {
			Desktop.getDesktop().browse(new URI(uri));
		} catch (IOException | URISyntaxException excep) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, GUIConstants.ERROR, GUIConstants.ERROR,
					JOptionPane.ERROR_MESSAGE);
			logger.log(Level.SEVERE, "Exception in opening link " + uri, excep); //$NON-NLS-1$
		}
	}

	/**
	 * Starting point of the gui
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		FeaturesPluginsLoader.setBaseFolderPath(System.getProperty(Applications.APP_HOME_PROPERTY));
		double frameScale = 1;
		if (args.length > 1 && "--frameScale".equals(args[0]) && args[1] != null) {
			try {
				frameScale = Double.valueOf(args[1]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		ReleaseDetails.addDetailsFromResource(
				ReleaseDetails.APPLICATION_PROPERTIES_ROOT + "app." + ReleaseDetails.PROPERTIES_EXT); //$NON-NLS-1$
		final double finalFrameScale = frameScale;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException
						| InstantiationException e) {
					logger.log(Level.SEVERE, "Exception in configuring UI manager", e); //$NON-NLS-1$
				}
				try {
					PDFValidationApplication frame = new PDFValidationApplication(finalFrameScale);
					URL url = ClassLoader.getSystemResource("org/verapdf/gui/images/icon.png");
					Toolkit kit = Toolkit.getDefaultToolkit();
					frame.setIconImage(kit.createImage(url));
					frame.setVisible(true);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Exception", e); //$NON-NLS-1$
				}
			}
		});
	}

	class ExitWindowAdapter extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				configManager.updateAppConfig(PDFValidationApplication.this.config);
			} catch (JAXBException | IOException excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			}
		}
	}

}