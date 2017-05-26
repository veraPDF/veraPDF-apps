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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.verapdf.gui.utils.GUIConstants;

/**
 * Mini logo panel. Represents mini logo and link to site.
 *
 * @author Maksim Bezrukov
 */
class MiniLogoPanel extends JPanel {

	/**
	 * ID for serialisation
	 */
	private static final long serialVersionUID = -199053265127458738L;

	MiniLogoPanel(String logoPath) throws IOException {

		setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel(GUIConstants.LABEL_TEXT);
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(logoPath)) {
			final BufferedImage image = ImageIO.read(is);
			Icon icon = new Icon() {

				private static final double SCALE = 0.3;

				@Override
				public void paintIcon(Component c, Graphics g, int x, int y) {
					g.drawImage(image, 0, 0, getIconWidth(), getIconHeight(), 0, 0, image.getWidth(), image.getHeight(),
							null);
				}

				@Override
				public int getIconWidth() {
					return (int) (image.getWidth() * SCALE);
				}

				@Override
				public int getIconHeight() {
					return (int) (image.getHeight() * SCALE);
				}
			};
			label.setIcon(icon);
		}

		add(label);

	}
}
