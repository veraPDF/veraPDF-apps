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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.verapdf.apps.ConfigManager;
import org.verapdf.gui.utils.GUIConstants;

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
	private JTextField fixMetadataPrefix;
	JTextField fixMetadataFolder;
	JFileChooser folderChooser;
	private JTextField profilesWikiPath;

	SettingsPanel() throws IOException {
		setBorder(new EmptyBorder(GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS,
				GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS));
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2));

		panel.add(new JLabel(GUIConstants.DISPLAY_PASSED_RULES));
		this.hidePassedRules = new JCheckBox();
		panel.add(this.hidePassedRules);

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
		this.fixMetadataPrefix = new JTextField(GUIConstants.SETTINGS_DIALOG_MAX_CHARS_TEXTFIELD);
		this.fixMetadataPrefix.setTransferHandler(null);
		panel.add(this.fixMetadataPrefix);

		panel.add(new JLabel(GUIConstants.SELECTED_PATH_FOR_FIXER_LABEL_TEXT));

		File currentDir = new File(new File(GUIConstants.DOT).getCanonicalPath());

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
						JOptionPane.showMessageDialog(SettingsPanel.this, "Error. Selected directory doesn't exist.",
								GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
					} else {
						SettingsPanel.this.fixMetadataFolder
								.setText(SettingsPanel.this.folderChooser.getSelectedFile().getAbsolutePath());
					}
				}

			}
		});
		this.fixMetadataFolder = new JTextField(GUIConstants.SETTINGS_DIALOG_MAX_CHARS_TEXTFIELD);
		this.fixMetadataFolder.setToolTipText(GUIConstants.SELECTED_PATH_FOR_FIXER_TOOLTIP);
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));
		panel4.add(this.fixMetadataFolder);
		panel4.add(choose2);
		panel.add(panel4);

		this.fixMetadataPrefix.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!isValidFileNameCharacter(e.getKeyChar())) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
			}
		});

		panel.add(new JLabel(GUIConstants.SELECTED_PROFILES_WIKI_PATH));
		this.profilesWikiPath = new JTextField(GUIConstants.SETTINGS_DIALOG_MAX_CHARS_TEXTFIELD);
		panel.add(this.profilesWikiPath);

		add(panel, BorderLayout.CENTER);

		this.okButton = new JButton(GUIConstants.OK);
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				boolean isEverythingValid = true;
				Path mdPath = FileSystems.getDefault().getPath(SettingsPanel.this.fixMetadataFolder.getText());
				if (mdPath == null || (!mdPath.toString().isEmpty()
						&& !(mdPath.toFile().isDirectory() && mdPath.toFile().canWrite()))) {
					isEverythingValid = false;
					JOptionPane.showMessageDialog(SettingsPanel.this, "Invalid path for saving fixed files.",
							"Invalid data", JOptionPane.INFORMATION_MESSAGE);
				}
				if (isEverythingValid) {
					SettingsPanel.this.ok = true;
					SettingsPanel.this.dialog.setVisible(false);
				}
			}
		});

		JButton cancelButton = new JButton(GUIConstants.CANCEL);
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

	boolean showDialog(Component parent, String title, ConfigManager settings) {

		this.ok = false;

		this.hidePassedRules.setSelected(settings.createProcessorConfig().getValidatorConfig().isRecordPasses());

		int numbOfFail = settings.createProcessorConfig().getValidatorConfig().getMaxFails();
		if (numbOfFail == -1) {
			this.numberOfFailed.setText("");
		} else {
			this.numberOfFailed.setText(String.valueOf(numbOfFail));
		}

		int numbOfFailDisp = settings.getApplicationConfig().getMaxFailsDisplayed();
		if (numbOfFailDisp == -1) {
			this.numberOfFailedDisplay.setText("");
		} else {
			this.numberOfFailedDisplay.setText(String.valueOf(numbOfFailDisp));
		}

		this.fixMetadataPrefix.setText(settings.createProcessorConfig().getFixerConfig().getFixesPrefix());
		this.fixMetadataFolder.setText(settings.getApplicationConfig().getFixesFolder());

		this.profilesWikiPath.setText(settings.getApplicationConfig().getWikiPath());

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

		this.dialog.setLocation(GUIConstants.SETTINGS_DIALOG_COORD_X, GUIConstants.SETTINGS_DIALOG_COORD_Y);
		this.dialog.setVisible(true);

		return this.ok;
	}

	private static KeyAdapter getKeyAdapter(final JTextField field, final boolean fromZero) {
		return new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if ((field.getText().length() == 6)
						&& ((field.getSelectedText() == null) || (field.getSelectedText().length() == 0))
						&& (c != KeyEvent.VK_BACK_SPACE) && (c != KeyEvent.VK_DELETE)) {
					e.consume();
				} else if (c == '0'
						&& ((!fromZero && field.getText().length() == 0) || field.getText().startsWith("0"))) {
					e.consume();
				} else if (!(((c >= '0') && (c <= '9')) || (c == KeyEvent.VK_BACK_SPACE)
						|| (c == KeyEvent.VK_DELETE))) {
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

	private static final char[] FORBIDDEN_SYMBOLS_IN_FILE_NAME = new char[] { '\\', '/', ':', '*', '?', '\"', '<', '>',
			'|', '+', '\0', '%' };

	public static final boolean isValidFileNameCharacter(char c) {
		for (char ch : FORBIDDEN_SYMBOLS_IN_FILE_NAME) {
			if (ch == c) {
				return false;
			}
		}
		return true;
	}

}
