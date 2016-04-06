package org.verapdf.gui;

import org.apache.log4j.Logger;
import org.verapdf.gui.config.Config;
import org.verapdf.gui.tools.ConfigIO;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.gui.tools.ProcessingType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

/**
 * Main frame of the PDFA Conformance Checker
 *
 * @author Maksim Bezrukov
 */

public class PDFValidationApplication extends JFrame {

    class ExitWindowAdapter extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            PDFValidationApplication app = (PDFValidationApplication) e.getSource();
			app.config.setFixMetadata(app.checkerPanel.isFixMetadata());
			app.config.setProcessingType(app.checkerPanel.getProcessingType());
            app.configIO.writeConfig(app.config);
		}
    }

	private static final long serialVersionUID = -5569669411392145783L;

	private static final Logger LOGGER = Logger.getLogger(PDFValidationApplication.class);

	private AboutPanel aboutPanel;
	private transient Config config;
	private SettingsPanel settingsPanel;
	private CheckerPanel checkerPanel;
	private ConfigIO configIO;

	Config getConfig () {
		return this.config;
	}

	private PDFValidationApplication() {
        addWindowListener(new ExitWindowAdapter());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(GUIConstants.FRAME_COORD_X, GUIConstants.FRAME_COORD_Y, GUIConstants.FRAME_WIDTH, GUIConstants.FRAME_HEIGHT);
		setResizable(false);

		setTitle(GUIConstants.TITLE);

		configIO = new ConfigIO();
		try {
			config = configIO.readConfig();
		}
		catch (IOException e) {
			LOGGER.error("Can not read config file", e);
			this.config =  new Config();
		}
		catch (JAXBException e) {
			LOGGER.error("Cannot parse config XML", e);
			this.config =  new Config();
		}

		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setJMenuBar(menuBar);

		aboutPanel = null;
		try {
			aboutPanel = new AboutPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error in reading logo image.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in reading logo image", e);
		}

		try {
			settingsPanel = new SettingsPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error initialising settings panel.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in initialising settings panel", e);
		}

		final JMenuItem sett = new JMenuItem("Settings");
		sett.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (settingsPanel != null && settingsPanel.showDialog(PDFValidationApplication.this, "Settings", config)) {
					Config config = new Config();
					config.setShowPassedRules(settingsPanel.isDispPassedRules());
					config.setMaxNumberOfFailedChecks(settingsPanel.getFailedChecksNumber());
					config.setMaxNumberOfDisplayedFailedChecks(settingsPanel.getFailedChecksDisplayNumber());
					config.setFixMetadataPathFolder(settingsPanel.getFixMetadataDirectory());
					config.setMetadataFixerPrefix(settingsPanel.getFixMetadataPrefix());
					config.setProfileWikiPath(settingsPanel.getProfilesWikiPath());
					config.setFixMetadata(PDFValidationApplication.this.config.isFixMetadata());
					config.setProcessingType(PDFValidationApplication.this.config.getProcessingType());

                    if(!PDFValidationApplication.this.config.equals(config)) {
                        PDFValidationApplication.this.config = config;
						configIO.writeConfig(PDFValidationApplication.this.config);
                    }
				}
			}
		});

		menuBar.add(sett);

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
		contentPane.setBorder(new EmptyBorder(GUIConstants.EMPTYBORDER_INSETS, GUIConstants.EMPTYBORDER_INSETS, GUIConstants.EMPTYBORDER_INSETS, GUIConstants.EMPTYBORDER_INSETS));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);

		MiniLogoPanel logoPanel = null;
		try {
			logoPanel = new MiniLogoPanel(GUIConstants.LOGO_NAME);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in creating mini logo.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in creating mini logo", e);
		}

		contentPane.add(logoPanel);

		checkerPanel = null;
		try {
			checkerPanel = new CheckerPanel(config, configIO);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in loading xml or html image.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in loading xml or html image", e);
		}
		contentPane.add(checkerPanel);

	}

	/**
	 * Starting point of the gui
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(
							UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
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