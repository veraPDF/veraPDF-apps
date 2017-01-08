package org.verapdf.gui;

import org.verapdf.gui.tools.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

		LogoPanel logo = new LogoPanel(GUIConstants.LOGO_NAME, GUIConstants.LOGO_BACKGROUND, GUIConstants.LOGO_PANEL_BORDER_WIDTH);

		mainPanel.add(logo);

		PartnersPanel partners = new PartnersPanel(GUIConstants.PARTNERS_NAME, GUIConstants.PARTNERS_BACKGROUND);

		mainPanel.add(partners);

		this.okButton = new JButton("Ok");
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
					JOptionPane.showMessageDialog(AboutPanel.this, GUIConstants.ERROR, GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
					logger.log(Level.SEVERE, "Exception in opening link " + GUIConstants.LOGO_LINK_URL, excep);
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
