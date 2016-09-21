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
import java.nio.file.Path;

import javax.swing.BoxLayout;
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

import org.apache.log4j.Logger;
import org.verapdf.ReleaseDetails;
import org.verapdf.features.config.FeaturesConfig;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.processor.config.Config;
import org.verapdf.processor.config.ConfigIO;

/**
 * Main frame of the PDFA Conformance Checker
 *
 * @author Maksim Bezrukov
 */

public class PDFValidationApplication extends JFrame {

	class ExitWindowAdapter extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			PDFValidationApplication.this.config
					.setFixMetadata(PDFValidationApplication.this.checkerPanel.isFixMetadata());
			PDFValidationApplication.this.config
					.setProcessingType(PDFValidationApplication.this.checkerPanel.getProcessingType());
			ConfigIO.writeConfig(PDFValidationApplication.this.config);
		}
	}

	private static final long serialVersionUID = -5569669411392145783L;

	private static final Logger LOGGER = Logger.getLogger(PDFValidationApplication.class);

	private AboutPanel aboutPanel;
	private transient Config config;
	private SettingsPanel settingsPanel;
	private FeaturesConfigPanel featuresPanel;
	private CheckerPanel checkerPanel;

	private PDFValidationApplication() {
		addWindowListener(new ExitWindowAdapter());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(GUIConstants.FRAME_COORD_X, GUIConstants.FRAME_COORD_Y, GUIConstants.FRAME_WIDTH,
				GUIConstants.FRAME_HEIGHT);
		setResizable(false);

		setTitle(GUIConstants.TITLE);

		try {
			config = ConfigIO.readConfig();
		} catch (IOException e) {
			LOGGER.error("Can not read config file", e);
			this.config = new Config();
		} catch (JAXBException e) {
			LOGGER.error("Cannot parse config XML", e);
			this.config = new Config();
		}

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
						PDFValidationApplication.this.config)) {
					PDFValidationApplication.this.config.setShowPassedRules(settingsPanel.isDispPassedRules());
					PDFValidationApplication.this.config
							.setMaxNumberOfFailedChecks(settingsPanel.getFailedChecksNumber());
					PDFValidationApplication.this.config
							.setMaxNumberOfDisplayedFailedChecks(settingsPanel.getFailedChecksDisplayNumber());
					PDFValidationApplication.this.config
							.setFixMetadataPathFolder(settingsPanel.getFixMetadataDirectory());
					PDFValidationApplication.this.config.setMetadataFixerPrefix(settingsPanel.getFixMetadataPrefix());
					PDFValidationApplication.this.config.setProfileWikiPath(settingsPanel.getProfilesWikiPath());
					PDFValidationApplication.this.config
							.setFixMetadata(PDFValidationApplication.this.config.isFixMetadata());
					PDFValidationApplication.this.config
							.setProcessingType(PDFValidationApplication.this.config.getProcessingType());
					ConfigIO.writeConfig(PDFValidationApplication.this.config);
				}
			}
		});

		file.add(sett);

		featuresPanel = new FeaturesConfigPanel();

		final JMenuItem features = new JMenuItem("Features Config");
		features.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path featuresPath = PDFValidationApplication.this.config.getFeaturesConfigFilePath();
				if (featuresPanel != null
						&& featuresPanel.showDialog(PDFValidationApplication.this, "Features Config", featuresPath)) {
					FeaturesConfig featuresConfig = featuresPanel.getFeaturesConfig();
					File featuresFile = featuresPath.toFile();
					try (FileOutputStream outputStream = new FileOutputStream(featuresFile)) {
						FeaturesConfig.toXml(featuresConfig, outputStream, Boolean.TRUE);
					} catch (JAXBException | IOException exp) {
						LOGGER.error("Exception in saving features config", exp);
					}
				}
			}
		});

		file.add(features);

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
			checkerPanel = new CheckerPanel(config);
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