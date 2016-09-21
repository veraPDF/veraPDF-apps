package org.verapdf.gui;

import org.apache.log4j.Logger;
import org.verapdf.features.config.FeaturesConfig;
import org.verapdf.gui.tools.GUIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

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
	private JCheckBox infoDict;
	private JCheckBox metadata;
	private JCheckBox documentSecurity;
	private JCheckBox signatures;
	private JCheckBox lowLevelInfo;
	private JCheckBox embeddedFiles;
	private JCheckBox iccProfiles;
	private JCheckBox outputIntents;
	private JCheckBox outlines;
	private JCheckBox annotations;
	private JCheckBox pages;
	private JCheckBox graphicsStates;
	private JCheckBox colorSpaces;
	private JCheckBox patterns;
	private JCheckBox shadings;
	private JCheckBox xobjects;
	private JCheckBox fonts;
	private JCheckBox propertiesDicts;

	FeaturesConfigPanel() {
		setBorder(new EmptyBorder(GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS, GUIConstants.EMPTY_BORDER_INSETS));
		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 3));

		infoDict = new JCheckBox("Information Dictionary");
		panel.add(infoDict);
		iccProfiles = new JCheckBox("ICC Profiles");
		panel.add(iccProfiles);
		colorSpaces = new JCheckBox("Color Spaces");
		panel.add(colorSpaces);

		metadata = new JCheckBox("Metadata");
		panel.add(metadata);
		outputIntents = new JCheckBox("Output Intents");
		panel.add(outputIntents);
		patterns = new JCheckBox("Patterns");
		panel.add(patterns);

		documentSecurity = new JCheckBox("Document Security");
		panel.add(documentSecurity);
		outlines = new JCheckBox("Outlines");
		panel.add(outlines);
		shadings = new JCheckBox("Shadings");
		panel.add(shadings);

		signatures = new JCheckBox("Signatures");
		panel.add(signatures);
		annotations = new JCheckBox("Annotations");
		panel.add(annotations);
		xobjects = new JCheckBox("XObjects");
		panel.add(xobjects);

		lowLevelInfo = new JCheckBox("Low Level Info");
		panel.add(lowLevelInfo);
		pages = new JCheckBox("Pages");
		panel.add(pages);
		fonts = new JCheckBox("Fonts");
		panel.add(fonts);

		embeddedFiles = new JCheckBox("Embedded Files");
		panel.add(embeddedFiles);
		graphicsStates = new JCheckBox("Graphics States");
		panel.add(graphicsStates);
		propertiesDicts = new JCheckBox("Properties Dictionaries");
		panel.add(propertiesDicts);

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

		FeaturesConfig config = null;

		if (featuresConfigPath != null && !featuresConfigPath.toString().isEmpty()) {
			try (FileInputStream fis = new FileInputStream(featuresConfigPath.toFile())) {
				config = FeaturesConfig.fromXml(fis);
			} catch (JAXBException e) {
				LOGGER.error("Error during loading features config", e);
			} catch (FileNotFoundException e) {
				LOGGER.error("Features config file not found", e);
			} catch (IOException excep) {
				LOGGER.info("IOException caught when closing config file: " + featuresConfigPath, excep);
			}
		}

		if (config == null) {
			config = new FeaturesConfig.Builder().build();
		}

		this.infoDict.setSelected(config.isInformationDictEnabled());
		this.metadata.setSelected(config.isMetadataEnabled());
		this.documentSecurity.setSelected(config.isDocumentSecurityEnabled());
		this.signatures.setSelected(config.isSignaturesEnabled());
		this.lowLevelInfo.setSelected(config.isLowLevelInfoEnabled());
		this.embeddedFiles.setSelected(config.isEmbeddedFilesEnabled());
		this.iccProfiles.setSelected(config.isIccProfilesEnabled());
		this.outputIntents.setSelected(config.isOutputIntentsEnabled());
		this.outlines.setSelected(config.isOutlinesEnabled());
		this.annotations.setSelected(config.isAnnotationsEnabled());
		this.pages.setSelected(config.isPagesEnabled());
		this.graphicsStates.setSelected(config.isGraphicsStatesEnabled());
		this.colorSpaces.setSelected(config.isColorSpacesEnabled());
		this.patterns.setSelected(config.isPatternsEnabled());
		this.shadings.setSelected(config.isShadingsEnabled());
		this.xobjects.setSelected(config.isXobjectsEnabled());
		this.fonts.setSelected(config.isFontsEnabled());
		this.propertiesDicts.setSelected(config.isPropertiesDictsEnabled());

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

	FeaturesConfig getFeaturesConfig() {
		FeaturesConfig.Builder builder = new FeaturesConfig.Builder();
		builder.informationDict(Boolean.valueOf(this.infoDict.isSelected()))
				.metadata(Boolean.valueOf(this.metadata.isSelected()))
				.documentSecurity(Boolean.valueOf(this.documentSecurity.isSelected()))
				.signatures(Boolean.valueOf(this.signatures.isSelected()))
				.lowLevelInfo(Boolean.valueOf(this.lowLevelInfo.isSelected()))
				.embeddedFiles(Boolean.valueOf(this.embeddedFiles.isSelected()))
				.iccProfiles(Boolean.valueOf(this.iccProfiles.isSelected()))
				.outputIntents(Boolean.valueOf(this.outputIntents.isSelected()))
				.outlines(Boolean.valueOf(this.outlines.isSelected()))
				.annotations(Boolean.valueOf(this.annotations.isSelected()))
				.pages(Boolean.valueOf(this.pages.isSelected()))
				.graphicsStates(Boolean.valueOf(this.graphicsStates.isSelected()))
				.colorSpaces(Boolean.valueOf(this.colorSpaces.isSelected()))
				.patterns(Boolean.valueOf(this.patterns.isSelected()))
				.shadings(Boolean.valueOf(this.shadings.isSelected()))
				.xobjects(Boolean.valueOf(this.xobjects.isSelected()))
				.fonts(Boolean.valueOf(this.fonts.isSelected()))
				.propertiesDicts(Boolean.valueOf(this.propertiesDicts.isSelected()));
		return builder.build();
	}
}
