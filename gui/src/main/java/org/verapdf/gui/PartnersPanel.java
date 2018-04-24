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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.verapdf.ReleaseDetails;
import org.verapdf.apps.Applications;
import org.verapdf.gui.utils.GUIConstants;
import org.verapdf.pdfa.Foundries;

/**
 * Panel with partners logo
 *
 * @author Maksim Bezrukov
 */
class PartnersPanel extends JPanel {

	/**
	 * ID for serialisation
	 */
	private static final long serialVersionUID = -5926089530817358566L;
	private final transient BufferedImage partnersLogo;
	private Color background;
	private JLabel consortium;
	private JLabel preforma;
	private JLabel version;

	PartnersPanel(String logoName, Color backgroundColor) throws IOException {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(logoName)) {
			this.partnersLogo = ImageIO.read(is);
		}
		this.background = backgroundColor;
		this.setLayout(null);

		this.consortium = new JLabel(GUIConstants.CONSORTIUM_TEXT);

		this.consortium.setHorizontalTextPosition(SwingConstants.CENTER);
		this.consortium.setFont(new Font(this.consortium.getFont().getName(), this.consortium.getFont().getStyle(),
				(int) (this.consortium.getFont().getSize() * GUIConstants.CONSORTIUM_FONT_SCALE)));
		Rectangle2D rec = new TextLayout(GUIConstants.CONSORTIUM_TEXT, this.consortium.getFont(),
				new FontRenderContext(null, true, true)).getBounds();
		this.consortium.setSize((int) (rec.getWidth()) + GUIConstants.BORDER_WIDTH * 4,
				(int) (rec.getHeight() + GUIConstants.BORDER_WIDTH));

		add(this.consortium);

		this.preforma = new JLabel(GUIConstants.PREFORMA_FUNDED_TEXT);

		this.preforma.setHorizontalTextPosition(SwingConstants.CENTER);
		this.preforma.setFont(new Font(this.preforma.getFont().getName(), this.preforma.getFont().getStyle(),
				(int) (this.preforma.getFont().getSize() * GUIConstants.PREFORMA_FUNDED_FONT_SCALE)));
		Rectangle2D rec2 = new TextLayout(GUIConstants.PREFORMA_FUNDED_TEXT, this.preforma.getFont(),
				new FontRenderContext(null, true, true)).getBounds();
		this.preforma.setSize((int) (rec2.getWidth()) + GUIConstants.BORDER_WIDTH * 2,
				(int) (rec2.getHeight() + GUIConstants.BORDER_WIDTH));

		add(this.preforma);

		ReleaseDetails appDetails= Applications.getAppDetails();
		String versionText = "Version: " + appDetails.getVersion()
				+ ", Parser: " + Foundries.defaultInstance().getParserId();

		this.version = new JLabel(versionText);

		this.version.setHorizontalTextPosition(SwingConstants.CENTER);
		Rectangle2D recVer = new TextLayout(versionText, this.version.getFont(),
				new FontRenderContext(null, true, true)).getBounds();
		this.version.setSize((int) (recVer.getWidth() + GUIConstants.BORDER_WIDTH * 2),
				(int) (recVer.getHeight() + GUIConstants.BORDER_WIDTH));
		add(this.version);

		setBackground(backgroundColor);

		int height = (int) (this.partnersLogo.getHeight() * GUIConstants.SCALE + this.consortium.getHeight() * 2
				+ this.version.getHeight() * 2 + this.preforma.getHeight());
		setPreferredSize(new Dimension(GUIConstants.PREFERRED_WIDTH, height + GUIConstants.BORDER_WIDTH * 2));
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

		int imageHeight = (int) (this.partnersLogo.getHeight() * GUIConstants.SCALE);
		int imageWidth = (int) (this.partnersLogo.getWidth() * GUIConstants.SCALE);
		int imageStartY = GUIConstants.BORDER_WIDTH * 2 + this.consortium.getHeight() + this.preforma.getHeight();
		int imageStartX = (getWidth() - imageWidth) / 2;

		g.setColor(this.background);

		this.consortium.setLocation((getWidth() - this.consortium.getWidth()) / 2, GUIConstants.BORDER_WIDTH);
		this.preforma.setLocation((getWidth() - this.preforma.getWidth()) / 2,
				GUIConstants.BORDER_WIDTH + this.consortium.getHeight());

		g.drawImage(this.partnersLogo, imageStartX, imageStartY, imageStartX + imageWidth, imageStartY + imageHeight, 0,
				0, this.partnersLogo.getWidth(), this.partnersLogo.getHeight(), this);

		this.version.setLocation((getWidth() - this.version.getWidth()) / 2,
				getHeight() - this.version.getHeight() - GUIConstants.BORDER_WIDTH);
	}

}
