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
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.verapdf.gui.utils.GUIConstants;

/**
 * About Panel
 *
 * @author Maksim Bezrukov
 */
class AboutPanel extends JPanel {

	/**
	 * ID for serialisation
	 */
	private static final long serialVersionUID = -4011118192914036216L;

	static final Logger logger = Logger.getLogger(AboutPanel.class.getCanonicalName());

	private JButton okButton;
	JDialog dialog;

	AboutPanel() throws IOException {
		setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		add(mainPanel, BorderLayout.CENTER);

		LogoPanel logo = new LogoPanel(GUIConstants.LOGO_NAME, GUIConstants.LOGO_BACKGROUND,
				GUIConstants.LOGO_PANEL_BORDER_WIDTH);

		mainPanel.add(logo);

		PartnersPanel partners = new PartnersPanel(GUIConstants.PARTNERS_NAME, GUIConstants.PARTNERS_BACKGROUND);

		mainPanel.add(partners);

		this.okButton = new JButton(GUIConstants.OK);
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				AboutPanel.this.dialog.setVisible(false);
			}
		});

		JButton urlLabel = new JButton(GUIConstants.LOGO_LINK_TEXT);
		urlLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					Desktop.getDesktop().browse(new URI(GUIConstants.LOGO_LINK_URL));
				} catch (IOException | URISyntaxException excep) {
					JOptionPane.showMessageDialog(AboutPanel.this, GUIConstants.ERROR, GUIConstants.ERROR,
							JOptionPane.ERROR_MESSAGE);
					logger.log(Level.SEVERE, "Exception in opening link " + GUIConstants.LOGO_LINK_URL, excep); //$NON-NLS-1$
				}

			}
		});

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2));

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(urlLabel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(this.okButton);

		bottomPanel.add(labelPanel);
		bottomPanel.add(buttonPanel);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	void showDialog(Component parent, String title) {

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

		this.dialog.setLocation(GUIConstants.ABOUT_DIALOG_COORD_X, GUIConstants.ABOUT_DIALOG_COORD_Y);
		this.dialog.setVisible(true);
	}

}
