package org.verapdf.gui;

import org.verapdf.gui.config.Config;
import org.verapdf.gui.tools.GUIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Settings Panel
 *
 * @author Maksim Bezrukov
 */
class SettingsPanel extends JPanel {

	private static final long serialVersionUID = -5688021756073449469L;
	private JButton okButton;
	boolean ok;
	JDialog dialog;
	private JTextField numberOfFailed;
	private JTextField numberOfFailedDisplay;
	private JCheckBox hidePassedRules;
	private JCheckBox usePlugins;
	private JTextField fixMetadataPrefix;
	JTextField fixMetadataFolder;
	JFileChooser folderChooser;
	private JTextField profilesWikiPath;

	SettingsPanel() throws IOException {
		setBorder(new EmptyBorder(GUIConstants.EMPTYBORDER_INSETS, GUIConstants.EMPTYBORDER_INSETS, GUIConstants.EMPTYBORDER_INSETS, GUIConstants.EMPTYBORDER_INSETS));
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(7, 2));

		panel.add(new JLabel(GUIConstants.DISPLAY_PASSED_RULES));
		this.hidePassedRules = new JCheckBox();
		panel.add(this.hidePassedRules);

		panel.add(new JLabel(GUIConstants.USE_PLUGINS));
		this.usePlugins = new JCheckBox();
		panel.add(this.usePlugins);

		panel.add(new JLabel(GUIConstants.MAX_NUMBER_FAILED_CHECKS));
		this.numberOfFailed = new JTextField();
		this.numberOfFailed.setTransferHandler(null);
		this.numberOfFailed.addKeyListener(getKeyAdapter(this.numberOfFailed, false));
		this.numberOfFailed.setToolTipText(GUIConstants.MAX_FAILED_CHECKS_SETTING_TIP);
		JPanel panel1 = new JPanel();
		panel1.setLayout(null);
		this.numberOfFailed.setBounds(0, 0, 65, 23);
		panel1.add(this.numberOfFailed);
		panel.add(panel1);

		panel.add(new JLabel(GUIConstants.MAX_NUMBER_FAILED_DISPLAYED_CHECKS));

		this.numberOfFailedDisplay = new JTextField();
		this.numberOfFailedDisplay.setTransferHandler(null);
		this.numberOfFailedDisplay.addKeyListener(getKeyAdapter(this.numberOfFailedDisplay, true));
		this.numberOfFailedDisplay.setToolTipText(GUIConstants.MAX_FAILED_CHECKS_DISP_SETTING_TIP);
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		this.numberOfFailedDisplay.setBounds(0, 0, 65, 23);
		panel2.add(this.numberOfFailedDisplay);
		panel.add(panel2);

		panel.add(new JLabel(GUIConstants.FIX_METADATA_PREFIX_LABEL_TEXT));
		this.fixMetadataPrefix = new JTextField(19); 
		this.fixMetadataPrefix.setTransferHandler(null);
		panel.add(this.fixMetadataPrefix);

		panel.add(new JLabel(GUIConstants.SELECTED_PATH_FOR_FIXER_LABEL_TEXT));

		File currentDir = new File(
				new File(GUIConstants.DOT).getCanonicalPath());

		JButton choose2 = new JButton(GUIConstants.FIX_METADATA_FOLDER_CHOOSE_BUTTON);
		this.folderChooser = new JFileChooser();
		this.folderChooser.setCurrentDirectory(currentDir);
		this.folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		choose2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int resultChoose = SettingsPanel.this.folderChooser.showOpenDialog(SettingsPanel.this);
				if (resultChoose == JFileChooser.APPROVE_OPTION) {
					if (!SettingsPanel.this.folderChooser.getSelectedFile().isDirectory()) {
						JOptionPane.showMessageDialog(SettingsPanel.this,
								"Error. Selected directory doesn't exist.",
								GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
					} else {
						SettingsPanel.this.fixMetadataFolder.setText(SettingsPanel.this.folderChooser.getSelectedFile().getAbsolutePath());
					}
				}

			}
		});
		this.fixMetadataFolder = new JTextField(19); 
		this.fixMetadataFolder.setToolTipText(GUIConstants.SELECTED_PATH_FOR_FIXER_TOOLTIP);
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));
		panel4.add(this.fixMetadataFolder);
		panel4.add(choose2);
		panel.add(panel4);

		this.fixMetadataPrefix.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!Config.isValidFileNameCharacter(e.getKeyChar())) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
			}
		});

		panel.add(new JLabel(GUIConstants.SELECTED_PROFILES_WIKI_PATH));
		this.profilesWikiPath = new JTextField(19);	
		panel.add(this.profilesWikiPath);


		add(panel, BorderLayout.CENTER);

		this.okButton = new JButton("Ok");
		this.okButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent event) {
				boolean isEverythingValid = true;
				if (!Config.isValidFolderPath(FileSystems.getDefault().getPath(SettingsPanel.this.fixMetadataFolder.getText()))) {
					isEverythingValid = false;
					JOptionPane.showMessageDialog(SettingsPanel.this, "Invalid path for saving fixed files.", "Invalid data", JOptionPane.INFORMATION_MESSAGE);
				}
				if (isEverythingValid) {
					SettingsPanel.this.ok = true;
					SettingsPanel.this.dialog.setVisible(false);
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent event) {
				SettingsPanel.this.dialog.setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	boolean showDialog(Component parent, String title, Config settings) {

		this.ok = false;

		this.hidePassedRules.setSelected(settings.isShowPassedRules());
		this.usePlugins.setSelected(settings.isUsePlugins());

		int numbOfFail = settings.getMaxNumberOfFailedChecks();
		if (numbOfFail == -1) {
			this.numberOfFailed.setText("");
		} else {
			this.numberOfFailed.setText(String.valueOf(numbOfFail));
		}

		int numbOfFailDisp = settings.getMaxNumberOfDisplayedFailedChecks();
		if (numbOfFailDisp == -1) {
			this.numberOfFailedDisplay.setText("");
		} else {
			this.numberOfFailedDisplay.setText(String.valueOf(numbOfFailDisp));
		}

		this.fixMetadataPrefix.setText(settings.getMetadataFixerPrefix());
		this.fixMetadataFolder.setText(settings.getFixMetadataFolder().toString());

		this.profilesWikiPath.setText(settings.getProfileWikiPath());

		Frame owner;
		if (parent instanceof Frame) {
			owner = (Frame) parent;
		} else {
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
		}

		if (this.dialog == null || this.dialog.getOwner() != owner) {
			this.dialog = new JDialog(owner, true);
			this.dialog.setResizable(false);
			this.dialog.add(this);
			this.dialog.getRootPane().setDefaultButton(this.okButton);
			this.dialog.pack();
			this.dialog.setTitle(title);
		}

		this.dialog.setLocation(GUIConstants.SETTINGSDIALOG_COORD_X, GUIConstants.SETTINGSDIALOG_COORD_Y);
		//this.dialog.setSize(650, 234);
		this.dialog.setVisible(true);

		return this.ok;
	}

	private static KeyAdapter getKeyAdapter(final JTextField field, final boolean fromZero) {
		return new KeyAdapter() {
			@Override
            public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if ((field.getText().length() == 6) && ((field.getSelectedText() == null) || (field.getSelectedText().length() == 0)) &&
						(c != KeyEvent.VK_BACK_SPACE) &&
						(c != KeyEvent.VK_DELETE)) {
					e.consume();
				} else if (c == '0' && ((!fromZero && field.getText().length() == 0) || field.getText().startsWith("0"))) {
					e.consume();
				} else if (!(((c >= '0') && (c <= '9')) ||
						(c == KeyEvent.VK_BACK_SPACE) ||
						(c == KeyEvent.VK_DELETE))) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (field.getText().startsWith("0")) {
					field.setText(field.getText().replaceFirst("0*", ""));
					if (field.getText().length() == 0) {
						if (fromZero) {
							field.setText("0");
						} else {
							field.setText("");
						}
					}
				}
				super.keyReleased(e);
			}
		};
	}

	boolean isDispPassedRules() {
		return this.hidePassedRules.isSelected();
	}

	boolean isUsePlugins() { return this.usePlugins.isSelected(); }

	int getFailedChecksNumber() {
		String str = this.numberOfFailed.getText();
		return str.length() > 0 ? Integer.parseInt(str) : -1;
	}

	int getFailedChecksDisplayNumber() {
		String str = this.numberOfFailedDisplay.getText();
		return str.length() > 0 ? Integer.parseInt(str) : -1;
	}

	Path getFixMetadataDirectory() {
		return FileSystems.getDefault().getPath(this.fixMetadataFolder.getText());
	}

	String getFixMetadataPrefix() {
		return this.fixMetadataPrefix.getText();
	}

	String getProfilesWikiPath() {
		return this.profilesWikiPath.getText();
	}
}
