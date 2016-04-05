package org.verapdf.gui;

import org.verapdf.gui.tools.GUIConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	private JLabel version;

	PartnersPanel(String logoName, Color backgroundColor) throws IOException {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(logoName)) {
			this.partnersLogo = ImageIO.read(is);
		}
		this.background = backgroundColor;
		this.setLayout(null);

		this.consortium = new JLabel(GUIConstants.CONSORTIUM_TEXT);

		this.consortium.setHorizontalTextPosition(SwingConstants.CENTER);
		this.consortium.setFont(new Font(this.consortium.getFont().getName(), this.consortium.getFont().getStyle(), (int) (this.consortium.getFont().getSize() * GUIConstants.CONSORTIUM_FONT_SCALE)));
		Rectangle2D rec = new TextLayout(GUIConstants.CONSORTIUM_TEXT, this.consortium.getFont(), new FontRenderContext(null, true, true)).getBounds();
		this.consortium.setSize((int) (rec.getWidth()) + GUIConstants.BORDER_WIDTH * 2, (int) (rec.getHeight() + GUIConstants.BORDER_WIDTH));

		add(this.consortium);

		Properties properties = new Properties();
		properties.load(getClass().getClassLoader().getResourceAsStream(GUIConstants.PROPERTIES_NAME));

		String versionText = "Version: " + properties.getProperty("application.version");

		this.version = new JLabel(versionText);

		this.version.setHorizontalTextPosition(SwingConstants.CENTER);
		Rectangle2D recVer = new TextLayout(versionText, this.version.getFont(), new FontRenderContext(null, true, true)).getBounds();
		this.version.setSize((int) (recVer.getWidth() + GUIConstants.BORDER_WIDTH * 2), (int) (recVer.getHeight() + GUIConstants.BORDER_WIDTH));
		add(this.version);


		setBackground(backgroundColor);

		int height = (int) (this.partnersLogo.getHeight() * GUIConstants.SCALE + this.consortium.getHeight() * 2 + this.version.getHeight() * 2);
		setPreferredSize(new Dimension(GUIConstants.PREFERRED_WIDTH, height + GUIConstants.BORDER_WIDTH * 2));
	}

	/**
	 * Paints the component
	 *
	 * @param g graphics for painting
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int imageHeight = (int) (this.partnersLogo.getHeight() * GUIConstants.SCALE);
		int imageWidth = (int) (this.partnersLogo.getWidth() * GUIConstants.SCALE);
		int imageStartY = GUIConstants.BORDER_WIDTH * 2 + this.consortium.getHeight();
		int imageStartX = (getWidth() - imageWidth) / 2;

		g.setColor(this.background);

		this.consortium.setLocation((getWidth() - this.consortium.getWidth()) / 2, GUIConstants.BORDER_WIDTH);

		g.drawImage(this.partnersLogo, imageStartX, imageStartY, imageStartX + imageWidth, imageStartY + imageHeight, 0, 0, this.partnersLogo.getWidth(), this.partnersLogo.getHeight(), this);

		this.version.setLocation((getWidth() - this.version.getWidth()) / 2, getHeight() - this.version.getHeight() - GUIConstants.BORDER_WIDTH);
	}

}
