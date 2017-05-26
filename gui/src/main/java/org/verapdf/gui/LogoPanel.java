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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.verapdf.gui.utils.GUIConstants;

/**
 * Panel with veraPDF logo
 *
 * @author Maksim Bezrukov
 */
class LogoPanel extends JPanel {

	/**
	 * ID for serialisation
	 */
	private static final long serialVersionUID = -3623071197419943686L;

	private final transient BufferedImage logo;
	private int borderWidth;
	private Color background;

	LogoPanel(String logoName, Color backgroundColor, int borderWidth) throws IOException {
		this.borderWidth = borderWidth;
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(logoName)) {
			this.logo = ImageIO.read(is);
		}
		this.background = backgroundColor;
		this.setLayout(null);

		setBackground(backgroundColor);

		setPreferredSize(new Dimension(GUIConstants.LOGO_PANEL_PREFERRED_SIZE_WIDTH,
				GUIConstants.LOGO_PANEL_PREFERRED_SIZE_HEIGHT));
	}

	/**
	 * Paints the component
	 *
	 * @param g
	 *            graphics for painting
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int imageHeight = getHeight() - this.borderWidth * 2;
		int imageWidth = this.logo.getWidth() * imageHeight / this.logo.getHeight();
		int imageStartX = (getWidth() - imageWidth) / 2;

		g.setColor(this.background);

		g.drawImage(this.logo, imageStartX, this.borderWidth, imageStartX + imageWidth, this.borderWidth + imageHeight,
				0, 0, this.logo.getWidth(), this.logo.getHeight(), this);

	}

}
