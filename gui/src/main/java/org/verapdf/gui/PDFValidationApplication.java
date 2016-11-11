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

		featuresPanel = new FeaturesConfigPanel();

		final JMenuItem features = new JMenuItem("Features Config");
		features.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File featuresFile = new File(PDFValidationApplication.this.config.getReportFile());
				if (featuresPanel != null && featuresPanel.showDialog(PDFValidationApplication.this, "Features Config",
						featuresFile.toPath())) {
					FeatureExtractorConfig featuresConfig = featuresPanel.getFeaturesConfig();
					try (FileOutputStream outputStream = new FileOutputStream(featuresFile)) {
						FeatureFactory.configToXml(featuresConfig, outputStream);
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