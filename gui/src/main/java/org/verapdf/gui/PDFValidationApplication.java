package org.verapdf.gui;

import org.apache.log4j.Logger;
import org.verapdf.gui.config.Config;
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
import java.nio.file.Path;

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
            changeConfigFromCheckerPanel(app.checkerPanel.getFixMetadataValue(),
                    app.checkerPanel.getProcessingTypeValue());
        }
    }

	private static final long serialVersionUID = -5569669411392145783L;

	static final Logger LOGGER = Logger.getLogger(PDFValidationApplication.class);

	AboutPanel aboutPanel;
	transient Config config;
	SettingsPanel settingsPanel;
	CheckerPanel checkerPanel;
	boolean isSerializedConfig;
	transient Path configPath;

<<<<<<< HEAD
	private PDFValidationApplication() {
        addWindowListener(new ExitWindowAdapter());
=======
	PDFValidationApplication() {
>>>>>>> a9ca1c10313498b0d5fe11f8a0aca24b207a34f9
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(GUIConstants.FRAME_COORD_X, GUIConstants.FRAME_COORD_Y, GUIConstants.FRAME_WIDTH, GUIConstants.FRAME_HEIGHT);
		setResizable(false);

		setTitle(GUIConstants.TITLE);

		String appHome = System.getProperty("app.home");
		if (appHome != null) {
			File user = new File(appHome);
			File f = new File(user, "config");
			if (!f.exists() && !f.mkdir()) {
				this.isSerializedConfig = false;
				this.config = Config.Builder.buildDefaultConfig();
			} else {
				File configFile = new File(f, "config.properties");
				this.isSerializedConfig = true;
				this.configPath = configFile.toPath();
				if (configFile.exists()) {
					try {
						if (!configFile.canRead()) {        //  Should we check that? Or configFile is always accesable?
							throw new IllegalArgumentException("Path should specify read accessible file");
						}
						FileReader reader = new FileReader(configFile);
						StringBuilder stringBuilder = new StringBuilder("");	// Is this way of reading file OK?
						BufferedReader bufferedReader = new BufferedReader(reader);
						String line;
						while ((line = bufferedReader.readLine()) != null)
							stringBuilder.append(line).append('\n');
						bufferedReader.close();
						this.config = Config.fromXml(stringBuilder.toString());

					} catch (IOException e) {
						LOGGER.error("Can not read config file", e);
						this.config = Config.Builder.buildDefaultConfig();
					}
					catch (JAXBException e) {   //  Is that the way we handle this exception?
						LOGGER.error("Cannot parse config XML", e);
						this.config = Config.Builder.buildDefaultConfig();
					}

				} else {
					this.config = Config.Builder.buildDefaultConfig();
				}
			}
		} else {
			this.config = Config.Builder.buildDefaultConfig();
		}

		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setJMenuBar(menuBar);

		this.aboutPanel = null;
		try {
			this.aboutPanel = new AboutPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error in reading logo image.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in reading logo image", e);
		}

		try {
			this.settingsPanel = new SettingsPanel();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error initialising settings panel.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in initialising settings panel", e);
		}

		final JMenuItem sett = new JMenuItem("Settings");
		sett.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PDFValidationApplication.this.settingsPanel != null && PDFValidationApplication.this.settingsPanel.showDialog(PDFValidationApplication.this, "Settings", PDFValidationApplication.this.config)) {
					Config.Builder builder = new Config.Builder();
<<<<<<< HEAD
					builder.showPassedRules(settingsPanel.isDispPassedRules());
					builder.maxNumberOfFailedChecks(settingsPanel.getFailedChecksNumber());
					builder.maxNumberOfDisplayedFailedChecks(settingsPanel.getFailedChecksDisplayNumber());
					builder.fixMetadataPathFolder(settingsPanel.getFixMetadataDirectory());
					builder.metadataFixerPrefix(settingsPanel.getFixMetadataPrefix());
					builder.profilesWikiPath(settingsPanel.getProfilesWikiPath());
                    builder.isFixMetadata(PDFValidationApplication.this.config.isFixMetadata());
                    builder.processingType(PDFValidationApplication.this.config.getProcessingType());
                    Config builtConfig = builder.build();

                    if(!PDFValidationApplication.this.config.equals(builtConfig)) { // TODO: We can check all fields for settings panel
                        PDFValidationApplication.this.config = builtConfig;   //TODO:  before builder, so we don't have to build configs if nothing is changed
                        writeConfigToFile();
                    }
=======
					builder.showPassedRules(PDFValidationApplication.this.settingsPanel.isDispPassedRules());
					builder.maxNumberOfFailedChecks(PDFValidationApplication.this.settingsPanel.getFailedChecksNumber());
					builder.maxNumberOfDisplayedFailedChecks(PDFValidationApplication.this.settingsPanel.getFailedChecksDisplayNumber());
					builder.fixMetadataPathFolder(PDFValidationApplication.this.settingsPanel.getFixMetadataDirectory());
					builder.metadataFixerPrefix(PDFValidationApplication.this.settingsPanel.getFixMetadataPrefix());
					builder.profilesWikiPath(PDFValidationApplication.this.settingsPanel.getProfilesWikiPath());
					PDFValidationApplication.this.config = builder.build();
					PDFValidationApplication.this.checkerPanel.setConfig(PDFValidationApplication.this.config);
					if (PDFValidationApplication.this.isSerializedConfig) {
						try {
							ConfigPropertiesSerializator.saveConfig(PDFValidationApplication.this.config, PDFValidationApplication.this.configPath);
						} catch (IOException e1) {
							LOGGER.error("Can not save config", e1);
						}
					}
>>>>>>> a9ca1c10313498b0d5fe11f8a0aca24b207a34f9
				}
			}
		});

		menuBar.add(sett);

		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PDFValidationApplication.this.aboutPanel != null) {
					PDFValidationApplication.this.aboutPanel.showDialog(PDFValidationApplication.this, "About veraPDF");
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

		this.checkerPanel = null;
		try {
			this.checkerPanel = new CheckerPanel(this.config);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(PDFValidationApplication.this, "Error in loading xml or html image.", GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Exception in loading xml or html image", e);
		}
<<<<<<< HEAD
		contentPane.add(checkerPanel);

=======
		contentPane.add(this.checkerPanel);
>>>>>>> a9ca1c10313498b0d5fe11f8a0aca24b207a34f9
	}

    void changeConfigFromCheckerPanel(boolean isFixMetadata, ProcessingType processingType) {
        if(isFixMetadata == this.config.isFixMetadata() &&
                processingType == this.config.getProcessingType())
            return;
        else {
            Config.Builder builder = new Config.Builder();
            builder.showPassedRules(this.config.isShowPassedRules());
            builder.maxNumberOfFailedChecks(this.config.getMaxNumberOfFailedChecks());
            builder.maxNumberOfDisplayedFailedChecks(this.config.getMaxNumberOfDisplayedFailedChecks());
            builder.fixMetadataPathFolder(this.config.getFixMetadataPathFolder());
            builder.metadataFixerPrefix(this.config.getMetadataFixerPrefix());
            builder.profilesWikiPath(this.config.getProfileWikiPath());
            builder.isFixMetadata(isFixMetadata);
            builder.processingType(processingType);

            PDFValidationApplication.this.config = builder.build();
            writeConfigToFile();
        }
    }

    private void writeConfigToFile() {
        if (PDFValidationApplication.this.isSerializedConfig) {     // What is that for?
            try {
                FileWriter writer = new FileWriter(PDFValidationApplication.this.configPath.toFile());
                writer.write(Config.toXml(PDFValidationApplication.this.config, true));
                writer.close();

            } catch (IOException e1) {
                e1.printStackTrace();
                LOGGER.error("Can not save config", e1);
            }
            catch (JAXBException e1) {
                e1.printStackTrace();
                LOGGER.error("Can not convert config to XML", e1);
            }
        }
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
