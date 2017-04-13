package org.verapdf.gui;

import org.verapdf.features.FeatureObjectType;
import org.verapdf.features.objects.Feature;
import org.verapdf.features.objects.FeaturesStructureContainer;
import org.verapdf.gui.tools.GUIConstants;
import org.verapdf.policy.SchematronGenerator;
import org.verapdf.policy.SchematronOperation;

import javax.swing.*;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    private List<JLabeledTextField> arguments;
    private List<JLabeledComboBox<Feature>> features;
    private List<JLabeledComboBox<SchematronOperation>> operations;

    private JButton addLineButton;
    private JButton removeLineButton;

    public PolicyPanel() {
        setPreferredSize(new Dimension(GUIConstants.PREFERRED_POLICY_SIZE_WIDTH, GUIConstants.PREFERRED_POLICY_SIZE_HEIGHT));

        this.featureTypes = new LinkedList<>();
        this.arguments = new LinkedList<>();
        this.features = new LinkedList<>();
        this.operations = new LinkedList<>();

        this.okButton = new JButton("Ok");
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (okButton.hasFocus()) {
                    JTextField emptyArguments = PolicyPanel.this.findEmptyNumberArguments();
                    if (emptyArguments == null) {
                        org.verapdf.gui.PolicyPanel.this.ok = true;
                        org.verapdf.gui.PolicyPanel.this.dialog.setVisible(false);
                    } else {
                        emptyArguments.requestFocus();
                        PolicyPanel.this.showErrorMessage("Argument required");
                    }
                }
            }
        });

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (cancelButton.hasFocus()) {
                    org.verapdf.gui.PolicyPanel.this.ok = false;
                    org.verapdf.gui.PolicyPanel.this.dialog.setVisible(false);
                }
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

    public void writeSchematronFile() throws IOException, XMLStreamException {
        FileOutputStream outputStream = new FileOutputStream(this.policyFile);
        SchematronGenerator.writeSchematron(getAssertions(), outputStream);
        outputStream.close();
    }

    public void setPoilcyFile(File policyFile) {
        this.policyFile = policyFile;
    }

    public List<SchematronGenerator.Assertion> getAssertions() {
        List<SchematronGenerator.Assertion> res = new ArrayList<>(featureTypes.size());
        for (int i = 0; i < this.featureTypes.size(); ++i) {
            FeatureObjectType assertionType = (FeatureObjectType) featureTypes.get(i).getSelectedItem();
            Feature assertionFeature = (Feature) features.get(i).getSelectedItem();
            SchematronOperation assertionOperation = (SchematronOperation) operations.get(i).getSelectedItem();
            String assertionArgument = arguments.get(i).getText();
            res.add(new SchematronGenerator.Assertion(assertionType, assertionFeature,
                    assertionOperation, assertionArgument));
        }
        return res;
    }

    private void addLineToMainPanel() {
        mainPanel.remove(removeLineButton);
        mainPanel.remove(addLineButton);
        this.dialog.setSize(new Dimension(this.getWidth(),
                this.getHeight() + GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));

        // For some weird reason setSize moves dialog 28 down.
        Rectangle oldBorder = dialog.getBounds();
        dialog.setBounds(oldBorder.x, oldBorder.y - 28, oldBorder.width, oldBorder.height);

        JLabeledTextField argumentsTextField = new JLabeledTextField();
        argumentsTextField.setLabel(this.arguments.size());
        this.arguments.add(argumentsTextField);
        argumentsTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JLabeledTextField textField = (JLabeledTextField) e.getSource();
                Feature feature = (Feature) PolicyPanel.this.features.get(
                        textField.getLabel()).getSelectedItem();
                validateArgumentTextBox(textField, feature.getFeatureType());
            }
        });

        JLabeledComboBox<FeatureObjectType> featuresTypeComboBox = getFeatureTypeComboBox();
        featuresTypeComboBox.setLabel(featureTypes.size());
        this.featureTypes.add(featuresTypeComboBox);
        mainPanel.add(featuresTypeComboBox);

        JLabeledComboBox<Feature> featuresComboBox = getFeatureComboBox(featuresTypeComboBox);
        featuresComboBox.setLabel(features.size());
        this.features.add(featuresComboBox);
        mainPanel.add(featuresComboBox);

        JLabeledComboBox<SchematronOperation> operationsComboBox = getOperationsComboBox(featuresComboBox);
        operationsComboBox.setLabel(this.operations.size());
        this.operations.add(operationsComboBox);
        mainPanel.add(operationsComboBox);

        setAvailabilityForArgumentTextBox(argumentsTextField, (SchematronOperation) operationsComboBox.getSelectedItem());
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
            this.mainPanel.remove(this.operations.get(linesNum - 1));
            this.mainPanel.remove(this.arguments.get(linesNum - 1));

            this.dialog.getBounds();
            this.dialog.setSize(new Dimension(this.getWidth(),
                    this.getHeight() - GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));

            // For some weird reason setSize moves dialog 28 down.
            Rectangle oldBorder = dialog.getBounds();
            dialog.setBounds(oldBorder.x, oldBorder.y - 28, oldBorder.width, oldBorder.height);

            this.featureTypes.remove(linesNum - 1);
            this.features.remove(linesNum - 1);
            this.operations.remove(linesNum - 1);
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

    private JLabeledComboBox<Feature> getFeatureComboBox(JComboBox<FeatureObjectType> featuresTypeComboBox) {
        JLabeledComboBox<Feature> featuresComboBox = new JLabeledComboBox<>();
        setFeaturesComboBoxForFeature(featuresComboBox,
                (FeatureObjectType) featuresTypeComboBox.getSelectedItem());
        featuresComboBox.setRenderer(new FeatureRenderer());
        featuresComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItemSelectable() instanceof JLabeledComboBox) {
                    int index = ((JLabeledComboBox<Feature>) e.getItemSelectable()).getLabel();
                    PolicyPanel.setOperationsComboBoxForFeatureType(PolicyPanel.this.operations.get(index),
                            (Feature) e.getItem());
                }
            }
        });
        return featuresComboBox;
    }

    private JLabeledComboBox<SchematronOperation> getOperationsComboBox(JComboBox<Feature> featuresComboBox) {
        JLabeledComboBox<SchematronOperation> operationsComboBox = new JLabeledComboBox<>();
        operationsComboBox.setRenderer(new OperationsRenderer());
        setOperationsComboBoxForFeatureType(operationsComboBox, (Feature) featuresComboBox.getSelectedItem());
        operationsComboBox.setLabel(this.operations.size());
        operationsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItemSelectable() instanceof JLabeledComboBox) {
                    JLabeledComboBox<SchematronOperation> comboBox =
                            (JLabeledComboBox<SchematronOperation>) e.getItemSelectable();
                    int index = (comboBox).getLabel();
                    SchematronOperation operation = (SchematronOperation) comboBox.getSelectedItem();
                    setAvailabilityForArgumentTextBox(PolicyPanel.this.arguments.get(index), operation);
                }
            }
        });
        return operationsComboBox;
    }

    private static void setFeaturesComboBoxForFeature(JLabeledComboBox<Feature> comboBox,
                                                      FeatureObjectType type) {
        List<Feature> features = FeaturesStructureContainer.getFeaturesListForType(type);
        comboBox.removeAllItems();
        for (Feature feature : features) {
            if (!feature.getFeatureName().equals("Error IDs")) {
                comboBox.addItem(feature);
            }
        }
    }

    private static void setOperationsComboBoxForFeatureType(JLabeledComboBox<SchematronOperation> comboBox,
                                                            Feature type) {
        List<SchematronOperation> operations = SchematronOperation.getOperationsForType(type.getFeatureType());
        comboBox.removeAllItems();
        for (SchematronOperation operation : operations) {
            comboBox.addItem(operation);
        }
    }

    private static void setAvailabilityForArgumentTextBox(JLabeledTextField argument, SchematronOperation operation) {
        if (operation != null) {
            argument.setEditable(operation.hasArguments());
            argument.setEnabled(operation.hasArguments());
        }
    }

    private boolean validateArgumentTextBox(JLabeledTextField argument, Feature.FeatureType type) {
        if (type == Feature.FeatureType.NUMBER) {
            String argumentValue = argument.getText();
            try {
                if (argumentValue.length() > 0) {
                    Double.valueOf(argumentValue);
                }
            } catch (NumberFormatException e) {
                this.showErrorMessage("Please enter a valid number");
                argument.requestFocus();
                return false;
            }
        }
        return true;
    }

    private JLabeledTextField findEmptyNumberArguments() {
        for (JLabeledTextField textField : this.arguments) {
            Feature.FeatureType argType = ((Feature)
                    this.features.get(textField.getLabel()).getSelectedItem()).getFeatureType();
            if (textField.isEnabled() && textField.getText().isEmpty() &&
                    argType != Feature.FeatureType.STRING) {
                return textField;
            }
        }
        return null;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message,
                GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
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

    private class OperationsRenderer extends JLabel implements ListCellRenderer<SchematronOperation> {
        @Override
        public Component getListCellRendererComponent(JList<? extends SchematronOperation> list,
                                                      SchematronOperation value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                this.setText(value.getDescription());
            }
            return this;
        }
    }

    private class JLabeledComboBox<E> extends JComboBox<E> {
        private int label;

        public JLabeledComboBox(E[] items) {
            super(items);
        }

        public JLabeledComboBox() {
        }

        private int getLabel() {
            return label;
        }

        private void setLabel(int label) {
            this.label = label;
        }
    }

    private class JLabeledTextField extends JTextField {
        private int label;

        public JLabeledTextField() {
        }

        public JLabeledTextField(String text) {
            super(text);
        }

        private int getLabel() {
            return label;
        }

        private void setLabel(int label) {
            this.label = label;
        }
    }
}
