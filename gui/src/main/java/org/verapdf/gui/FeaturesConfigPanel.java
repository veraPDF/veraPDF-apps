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
import java.util.EnumMap;
import java.util.EnumSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.features.FeatureObjectType;
import org.verapdf.gui.utils.GUIConstants;

/**
 * @author Maksim Bezrukov
 */
public class FeaturesConfigPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -6602264333993164990L;

	private JButton okButton;
	boolean ok;
	JDialog dialog;
	private EnumMap<FeatureObjectType, JCheckBox> featureGrid = new EnumMap<>(FeatureObjectType.class);

	FeaturesConfigPanel() {
		setBorder(new EmptyBorder(GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS,
				GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS));
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 3));

		for (FeatureObjectType type : FeatureObjectType.values()) {
			if (type != FeatureObjectType.ERROR) {
				this.featureGrid.put(type, new JCheckBox(type.getFullName()));
				panel.add(this.featureGrid.get(type));
			}
		}

		add(panel, BorderLayout.CENTER);

		this.okButton = new JButton(GUIConstants.OK);
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				org.verapdf.gui.FeaturesConfigPanel.this.ok = true;
				org.verapdf.gui.FeaturesConfigPanel.this.dialog.setVisible(false);
			}
		});

		JButton cancelButton = new JButton(GUIConstants.CANCEL);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				org.verapdf.gui.FeaturesConfigPanel.this.dialog.setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	boolean showDialog(Component parent, String title, FeatureExtractorConfig featureExtractorConfig) {

		this.ok = false;

		for (FeatureObjectType type : FeatureObjectType.values()) {
			if (type != FeatureObjectType.ERROR) {
				this.featureGrid.get(type).setSelected(true);
				this.featureGrid.get(type).setSelected(featureExtractorConfig.isFeatureEnabled(type));
			}
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

	FeatureExtractorConfig getFeaturesConfig() {
		EnumSet<FeatureObjectType> enabledFeatures = EnumSet.noneOf(FeatureObjectType.class);
		for (FeatureObjectType type : this.featureGrid.keySet()) {
			if (this.featureGrid.get(type).isSelected()) {
				enabledFeatures.add(type);
			}
		}
		return FeatureFactory.configFromValues(enabledFeatures);
	}

}
