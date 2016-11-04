package org.verapdf.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.EnumSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.features.FeatureFactory;
import org.verapdf.features.FeatureObjectType;
import org.verapdf.gui.tools.GUIConstants;

/**
 * @author Maksim Bezrukov
 */
public class FeaturesConfigPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6602264333993164990L;

	private static final Logger LOGGER = Logger.getLogger(FeaturesConfigPanel.class);

	private JButton okButton;
	boolean ok;
	JDialog dialog;
	private EnumMap<FeatureObjectType, JCheckBox> featureGrid = new EnumMap<>(FeatureObjectType.class);

	FeaturesConfigPanel() {
		setBorder(new EmptyBorder(GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS));
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 3));

		for (FeatureObjectType type : FeatureObjectType.values()) {
			featureGrid.put(type, new JCheckBox(type.getNodeName()));
			panel.add(featureGrid.get(type));
		}

		add(panel, BorderLayout.CENTER);

		this.okButton = new JButton("Ok");
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				org.verapdf.gui.FeaturesConfigPanel.this.ok = true;
				org.verapdf.gui.FeaturesConfigPanel.this.dialog.setVisible(false);
			}
		});

		JButton cancelButton = new JButton("Cancel");
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

	boolean showDialog(Component parent, String title, Path featuresConfigPath) {

		this.ok = false;

		FeatureExtractorConfig config = FeatureFactory.defaultConfig();

		if (featuresConfigPath != null && !featuresConfigPath.toString().isEmpty()) {
			try (FileInputStream fis = new FileInputStream(featuresConfigPath.toFile())) {
				config = FeatureFactory.configFromXml(fis);
			} catch (JAXBException e) {
				LOGGER.error("Error during loading features config", e);
			} catch (FileNotFoundException e) {
				LOGGER.error("Features config file not found", e);
			} catch (IOException excep) {
				LOGGER.info("IOException caught when closing config file: " + featuresConfigPath, excep);
			}
		}
		
		for (FeatureObjectType type : config.getEnabledFeatures()) {
			this.featureGrid.get(type).setSelected(true);
		}

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
