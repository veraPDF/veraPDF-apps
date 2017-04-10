package org.verapdf.gui;

import org.verapdf.features.FeatureObjectType;
import org.verapdf.features.objects.Feature;
import org.verapdf.features.objects.FeaturesStructureContainer;
import org.verapdf.gui.tools.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author Sergey Shemyakov
 */
public class PolicyPanel extends JPanel {

    private File policyFile;

    private JButton okButton;
    private JDialog dialog;
    private boolean ok;
    private JPanel mainPanel;
    private GridLayout mainPanelLayout;

    private List<JLabeledComboBox<FeatureObjectType>> featureTypes;
    private List<JTextField> arguments;
    private List<JLabeledComboBox<Feature>> features;
    private List<JLabeledComboBox<Example>> comboBoxes3;   // TODO: remove; just for testing purposes

    private JButton addLineButton;
    private JButton removeLineButton;

    public PolicyPanel() {
        setPreferredSize(new Dimension(GUIConstants.PREFERRED_POLICY_SIZE_WIDTH, GUIConstants.PREFERRED_POLICY_SIZE_HEIGHT));

        this.featureTypes = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.features = new ArrayList<>();
        this.comboBoxes3 = new ArrayList<>();   // TODO: remove; just for testing purposes

        this.okButton = new JButton("Ok");
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                org.verapdf.gui.PolicyPanel.this.ok = true;
                org.verapdf.gui.PolicyPanel.this.dialog.setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                org.verapdf.gui.PolicyPanel.this.ok = false;
                org.verapdf.gui.PolicyPanel.this.dialog.setVisible(false);
            }
        });

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addLineButton = new JButton("New assertion");
        addLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                org.verapdf.gui.PolicyPanel.this.addLineToMainPanel();
            }
        });

        removeLineButton = new JButton("Delete last assertion");
        removeLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                org.verapdf.gui.PolicyPanel.this.removeLineFromMainPanel();
            }
        });

        mainPanelLayout = new GridLayout(0, 4, 10, 10);
        mainPanel = new JPanel(mainPanelLayout);
        mainPanel.add(addLineButton);
        mainPanel.add(removeLineButton);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    public boolean showDialog(Component parent) {
        Frame owner = parent instanceof Frame ?
                (Frame) parent :
                (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        if (this.dialog == null || this.dialog.getOwner() != owner) {
            this.dialog = new JDialog(owner, true);
            this.dialog.setResizable(false);
            this.dialog.add(this);
            this.dialog.getRootPane().setDefaultButton(this.okButton);
            this.dialog.pack();
            this.dialog.setTitle("Policy Config");
        }

        this.dialog.setLocation(GUIConstants.POLICY_DIALOG_COORD_X, GUIConstants.POLICY_DIALOG_COORD_Y);
        this.dialog.setVisible(true);

        return this.ok;
    }

    public File getPolicyFile() {
        return policyFile;
    }

    public void setPoilcyFile(File poilcyFile) {
        this.policyFile = policyFile;
    }

    private void addLineToMainPanel() {
        mainPanel.remove(removeLineButton);
        mainPanel.remove(addLineButton);
        this.dialog.setSize(new Dimension(this.getWidth(),
                this.getHeight() + GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));

        // For some weird reason setSize moves dialog 28 down.
        Rectangle oldBorder = dialog.getBounds();
        dialog.setBounds(oldBorder.x, oldBorder.y - 28, oldBorder.width, oldBorder.height);

        JLabeledComboBox<FeatureObjectType> featuresTypeComboBox = getFeatureTypeComboBox();
        featuresTypeComboBox.setLabel(featureTypes.size());
        this.featureTypes.add(featuresTypeComboBox);
        mainPanel.add(featuresTypeComboBox);

        JLabeledComboBox<Feature> featuresComboBox = new JLabeledComboBox<>();
        setFeaturesComboBoxForFeature(featuresComboBox,
                (FeatureObjectType) featuresTypeComboBox.getSelectedItem());
        featuresComboBox.setLabel(features.size());
        featuresComboBox.setRenderer(new FeatureRenderer());
        this.features.add(featuresComboBox);
        mainPanel.add(featuresComboBox);

        JLabeledComboBox<Example> anotherTestComboBox = new JLabeledComboBox<>(Example.values()); // TODO: remove; just for testing purposes
        this.comboBoxes3.add(anotherTestComboBox);
        mainPanel.add(anotherTestComboBox);    // TODO: we need operators here?

        JTextField argumentsTextField = new JTextField();
        this.arguments.add(argumentsTextField);
        mainPanel.add(argumentsTextField);

        mainPanel.add(addLineButton);
        mainPanel.add(removeLineButton);
        this.dialog.revalidate();
        this.dialog.repaint();
    }

    private void removeLineFromMainPanel() {
        int linesNum = this.featureTypes.size();
        if (linesNum > 0) {
            mainPanel.remove(removeLineButton);
            mainPanel.remove(addLineButton);

            this.mainPanel.remove(this.featureTypes.get(linesNum - 1));
            this.mainPanel.remove(this.features.get(linesNum - 1));
            this.mainPanel.remove(this.comboBoxes3.get(linesNum - 1));
            this.mainPanel.remove(this.arguments.get(linesNum - 1));

            this.dialog.getBounds();
            this.dialog.setSize(new Dimension(this.getWidth(),
                    this.getHeight() - GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));

            // For some weird reason setSize moves dialog 28 down.
            Rectangle oldBorder = dialog.getBounds();
            dialog.setBounds(oldBorder.x, oldBorder.y - 28, oldBorder.width, oldBorder.height);

            this.featureTypes.remove(linesNum - 1);
            this.features.remove(linesNum - 1);
            this.comboBoxes3.remove(linesNum - 1);
            this.arguments.remove(linesNum - 1);

            mainPanel.add(addLineButton);
            mainPanel.add(removeLineButton);

            this.dialog.revalidate();
            this.dialog.repaint();
        }
    }

    private JLabeledComboBox<FeatureObjectType> getFeatureTypeComboBox() {
        JLabeledComboBox<FeatureObjectType> featuresTypeComboBox = new JLabeledComboBox<>(FeatureObjectType.values());
        featuresTypeComboBox.setRenderer(new FeatureObjectTypeRenderer());
        featuresTypeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItemSelectable() instanceof JLabeledComboBox) {
                    int index = ((JLabeledComboBox<FeatureObjectType>) e.getItemSelectable()).getLabel();
                    PolicyPanel.setFeaturesComboBoxForFeature(PolicyPanel.this.features.get(index),
                            (FeatureObjectType) e.getItem());
                }
            }
        });
        return featuresTypeComboBox;
    }

    private static void setFeaturesComboBoxForFeature(JLabeledComboBox<Feature> comboBox,
                                                      FeatureObjectType type) {
        List<Feature> features = FeaturesStructureContainer.getFeaturesListForType(type);
        comboBox.removeAllItems();
        for (Feature feature : features) {
            comboBox.addItem(feature);
        }
    }

    private class FeatureObjectTypeRenderer extends JLabel implements ListCellRenderer<FeatureObjectType> {
        @Override
        public Component getListCellRendererComponent(JList<? extends FeatureObjectType> list, FeatureObjectType value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                this.setText(value.getFullName());
            }
            return this;
        }
    }

    private class FeatureRenderer extends JLabel implements ListCellRenderer<Feature> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Feature> list, Feature value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                this.setText(value.getFeatureName());
            }
            return this;
        }
    }

    private class JLabeledComboBox<E> extends JComboBox<E> {
        private int label;

        public JLabeledComboBox(ComboBoxModel<E> aModel) {
            super(aModel);
        }

        public JLabeledComboBox(E[] items) {
            super(items);
        }

        public JLabeledComboBox(Vector<E> items) {
            super(items);
        }

        public JLabeledComboBox() {
        }

        public int getLabel() {
            return label;
        }

        public void setLabel(int label) {
            this.label = label;
        }
    }

    private enum Example {  // TODO: remove
        EENY,
        MEENY,
        MINY,
        MOE
    }
}
