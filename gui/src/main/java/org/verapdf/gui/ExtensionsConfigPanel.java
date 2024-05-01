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

import org.verapdf.extensions.ExtensionObjectType;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.processor.app.ConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * @author Maxim Plushchov
 */
public class ExtensionsConfigPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -6602264333993164990L;

	private final JButton okButton;
	boolean ok;
	JDialog dialog;
	private final EnumMap<ExtensionObjectType, JCheckBox> extensionGrid = new EnumMap<>(ExtensionObjectType.class);

	ExtensionsConfigPanel() {
		setBorder(new EmptyBorder(GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS,
				GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS));
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 3));

		for (ExtensionObjectType type : ExtensionObjectType.values()) {
			this.extensionGrid.put(type, new JCheckBox(type.toString()));
			panel.add(this.extensionGrid.get(type));
		}

		add(panel, BorderLayout.CENTER);

		this.okButton = new JButton(GUIConstants.OK);
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				ExtensionsConfigPanel.this.ok = true;
				ExtensionsConfigPanel.this.dialog.setVisible(false);
			}
		});

		JButton cancelButton = new JButton(GUIConstants.CANCEL);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				ExtensionsConfigPanel.this.dialog.setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	boolean showDialog(Component parent, String title, EnumSet<ExtensionObjectType> enabledExtensions) {

		this.ok = false;

		for (ExtensionObjectType type : enabledExtensions) {
			this.extensionGrid.get(type).setSelected(true);
		}

		Frame owner = parent instanceof Frame ? (Frame) parent
				: (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

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

	void updateEnabledExtensions(ConfigManager configManager) throws JAXBException, IOException {
		ValidatorConfig validatorConfig = configManager.getValidatorConfig();
		validatorConfig.getEnabledExtensions().clear();
		for (Map.Entry<ExtensionObjectType, JCheckBox> entry : this.extensionGrid.entrySet()) {
			if (entry.getValue().isSelected()) {
				validatorConfig.getEnabledExtensions().add(entry.getKey());
			}
		}
		configManager.updateValidatorConfig(validatorConfig);
	}
}
