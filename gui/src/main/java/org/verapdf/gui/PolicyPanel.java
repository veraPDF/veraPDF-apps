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
import java.util.*;
import java.util.List;

/**
 * @author Sergey Shemyakov
 */
public class PolicyPanel extends JPanel {

    private static final String ERROR_IDS = "Error IDs";

    private File policyFile;

    private JButton okButton;
    private JDialog dialog;
    private boolean ok;
    private JPanel mainPanel;
    private JPanel assertionsPanel;
    private LayoutManager mainPanelLayout;
    private JScrollPane scrollPane;

    private List<JComboBox<FeatureObjectType>> featureTypes;
    private List<JTextField> arguments;
    private List<JComboBox<Feature>> features;
    private List<JComboBox<SchematronOperation>> operations;
    private List<JButton> removeLineButtons;
    private List<JPanel> layoutPanels;

    private JPanel buttonPanel;
    private JButton addLineButton;

    public PolicyPanel() {
        setPreferredSize(new Dimension(GUIConstants.PREFERRED_POLICY_SIZE_WIDTH, GUIConstants.PREFERRED_POLICY_SIZE_HEIGHT));

        this.featureTypes = new LinkedList<>();
        this.arguments = new LinkedList<>();
        this.features = new LinkedList<>();
        this.operations = new LinkedList<>();
        this.removeLineButtons = new LinkedList<>();
        this.layoutPanels = new LinkedList<>();

        this.okButton = new JButton("Ok");
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (okButton.hasFocus()) {
                    JTextField emptyArguments = PolicyPanel.this.findEmptyNumberArguments();
                    if (emptyArguments == null) {
                        if (layoutPanels.size() > 0) {
                            org.verapdf.gui.PolicyPanel.this.ok = true;
                        }
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
                    org.verapdf.gui.PolicyPanel.this.dialog.setVisible(false);
                }
            }
        });

        setLayout(new BorderLayout());

        JPanel okButtonPanel = new JPanel();
        okButtonPanel.add(this.okButton);
        okButtonPanel.add(cancelButton);
        add(okButtonPanel, BorderLayout.SOUTH);

        addLineButton = new JButton("+");
        addLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                org.verapdf.gui.PolicyPanel.this.addLineToMainPanel();
            }
        });
        addLineButton.setPreferredSize(new Dimension(GUIConstants.PREFERRED_POLICY_SIZE_WIDTH / 20,
                GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addLineButton);

        assertionsPanel = new JPanel();
        mainPanelLayout = new BoxLayout(assertionsPanel, BoxLayout.Y_AXIS);
        assertionsPanel.setLayout(mainPanelLayout);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(assertionsPanel, BorderLayout.NORTH);
        scrollPane = new JScrollPane(mainPanel);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public boolean showDialog(Component parent) {
        Frame owner = parent instanceof Frame ?
                (Frame) parent :
                (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        this.ok = false;
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
        this.dialog.repaint();
        return this.ok;
    }

    public File getPolicyFile() {
        return policyFile;
    }

    public void writeSchematronFile() throws IOException, XMLStreamException {
        FileOutputStream outputStream = new FileOutputStream(policyFile);
        SchematronGenerator.writeSchematron(getAssertions(), outputStream);
        outputStream.close();
    }

    public void setPoilcyFile(File policyFile) {
        if (!getFileExtension(policyFile).equalsIgnoreCase("sch")) {
            policyFile = new File(getBaseName(policyFile) + ".sch");
        }
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
        JPanel linePanel = new JPanel();
        GridBagLayout linePanelLayout = new GridBagLayout();
        linePanel.setLayout(linePanelLayout);

        JTextField argumentsTextField = new JTextField();
        this.arguments.add(argumentsTextField);
        argumentsTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                int index = PolicyPanel.this.arguments.indexOf(textField);
                Feature feature = (Feature) PolicyPanel.this.features.get(index).getSelectedItem();
                validateArgumentTextBox(textField, feature.getFeatureType());
            }
        });

        JComboBox<FeatureObjectType> featuresTypeComboBox = getFeatureTypeComboBox();
        this.featureTypes.add(featuresTypeComboBox);
        linePanelLayout.setConstraints(featuresTypeComboBox, getFeatureObjectTypeConstraints());
        linePanel.add(featuresTypeComboBox);

        JComboBox<Feature> featuresComboBox = getFeatureComboBox(featuresTypeComboBox);
        this.features.add(featuresComboBox);
        linePanelLayout.setConstraints(featuresComboBox, getFeatureConstraints());
        linePanel.add(featuresComboBox);

        JComboBox<SchematronOperation> operationsComboBox = getOperationsComboBox(featuresComboBox);
        this.operations.add(operationsComboBox);
        linePanelLayout.setConstraints(operationsComboBox, getOperationConstraints());
        linePanel.add(operationsComboBox);

        setAvailabilityForArgumentTextBox(argumentsTextField, (SchematronOperation) operationsComboBox.getSelectedItem());
        linePanelLayout.setConstraints(argumentsTextField, getArgumentConstraints());
        linePanel.add(argumentsTextField);

        JButton removeButton = new JButton("x");
        removeButton.setPreferredSize(new Dimension(GUIConstants.PREFERRED_POLICY_SIZE_WIDTH / 20,
                GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));
        removeButton.addActionListener(new RemoveLineButtonListener());
        this.removeLineButtons.add(removeButton);
        linePanelLayout.setConstraints(removeButton, getButtonConstraints());
        linePanel.add(removeButton);

        this.layoutPanels.add(linePanel);
        assertionsPanel.add(linePanel);

        this.dialog.revalidate();
        this.dialog.repaint();
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private void removeLineFromMainPanel(int index) {
        if (index >= 0 && index < this.layoutPanels.size()) {
            assertionsPanel.remove(index);

            this.featureTypes.remove(index);
            this.features.remove(index);
            this.operations.remove(index);
            this.arguments.remove(index);
            this.removeLineButtons.remove(index);
            this.layoutPanels.remove(index);

            this.dialog.revalidate();
            this.dialog.repaint();
        }
    }

    private JComboBox<FeatureObjectType> getFeatureTypeComboBox() {
        JComboBox<FeatureObjectType> featuresTypeComboBox = new JComboBox<>(getFeatureObjectTypes());
        featuresTypeComboBox.setRenderer(new FeatureObjectTypeRenderer());
        featuresTypeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItemSelectable() instanceof JComboBox) {
                    int index = PolicyPanel.this.featureTypes.indexOf(e.getItemSelectable());
                    PolicyPanel.setFeaturesComboBoxForFeature(PolicyPanel.this.features.get(index),
                            (FeatureObjectType) e.getItem());
                }
            }
        });
        setOptimalSizeForComboBox(featuresTypeComboBox);
        return featuresTypeComboBox;
    }

    private JComboBox<Feature> getFeatureComboBox(JComboBox<FeatureObjectType> featuresTypeComboBox) {
        JComboBox<Feature> featuresComboBox = new JComboBox<>();
        setFeaturesComboBoxForFeature(featuresComboBox,
                (FeatureObjectType) featuresTypeComboBox.getSelectedItem());
        featuresComboBox.setRenderer(new FeatureRenderer());
        featuresComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItemSelectable() instanceof JComboBox) {
                    int index = PolicyPanel.this.features.indexOf(e.getItemSelectable());
                    PolicyPanel.setOperationsComboBoxForFeatureType(PolicyPanel.this.operations.get(index),
                            (Feature) e.getItem());
                }
            }
        });
        setOptimalSizeForComboBox(featuresComboBox);
        return featuresComboBox;
    }

    private JComboBox<SchematronOperation> getOperationsComboBox(JComboBox<Feature> featuresComboBox) {
        final JComboBox<SchematronOperation> operationsComboBox = new JComboBox<>();
        operationsComboBox.setRenderer(new OperationsRenderer());
        setOperationsComboBoxForFeatureType(operationsComboBox, (Feature) featuresComboBox.getSelectedItem());
        operationsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItemSelectable() instanceof JComboBox) {
                    JComboBox<SchematronOperation> comboBox =
                            (JComboBox<SchematronOperation>) e.getItemSelectable();
                    int index = PolicyPanel.this.operations.indexOf(comboBox);
                    SchematronOperation operation = (SchematronOperation) comboBox.getSelectedItem();
                    setAvailabilityForArgumentTextBox(PolicyPanel.this.arguments.get(index), operation);
                }
            }
        });
        setOptimalSizeForComboBox(operationsComboBox);
        return operationsComboBox;
    }

    private static void setFeaturesComboBoxForFeature(JComboBox<Feature> comboBox,
                                                      FeatureObjectType type) {
        List<Feature> features = FeaturesStructureContainer.getFeaturesListForType(type);
        comboBox.removeAllItems();
        for (Feature feature : features) {
            if (!feature.getFeatureName().equals(ERROR_IDS)) {
                comboBox.addItem(feature);
            }
        }
    }

    private static void setOperationsComboBoxForFeatureType(JComboBox<SchematronOperation> comboBox,
                                                            Feature type) {
        Set<SchematronOperation> operations = type.getFeatureType().getLegalOperations();
        comboBox.removeAllItems();
        for (SchematronOperation operation : operations) {
            comboBox.addItem(operation);
        }
    }

    private static void setAvailabilityForArgumentTextBox(JTextField argument, SchematronOperation operation) {
        if (operation != null) {
            argument.setEditable(operation.hasArguments());
            argument.setEnabled(operation.hasArguments());
        }
    }

    private boolean validateArgumentTextBox(JTextField argument, Feature.FeatureType type) {
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

    private JTextField findEmptyNumberArguments() {
        for (JTextField textField : this.arguments) {
            int index = this.arguments.indexOf(textField);
            Feature.FeatureType argType = ((Feature)
                    this.features.get(index).getSelectedItem()).getFeatureType();
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

    private static Vector<FeatureObjectType> getFeatureObjectTypes() {
        Vector<FeatureObjectType> res = new Vector<>();
        for (FeatureObjectType type : FeatureObjectType.values()) {
            List<Feature> features = FeaturesStructureContainer.getFeaturesListForType(type);
            if (!(features.size() == 1 && features.get(0).getFeatureName().equals(ERROR_IDS)) &&
                    features.size() != 0) {
                res.add(type);
            }
        }
        return res;
    }

    private static GridBagConstraints getFeatureObjectTypeConstraints() {
        GridBagConstraints res = new GridBagConstraints();
        res.gridheight = 1;
        res.gridwidth = 1;
        res.gridx = 0;
        res.gridy = 0;
        res.weighty = 0;
        res.weightx = 2;
        res.fill = GridBagConstraints.HORIZONTAL;
        return res;
    }

    private static GridBagConstraints getFeatureConstraints() {
        GridBagConstraints res = new GridBagConstraints();
        res.gridheight = 1;
        res.gridwidth = 1;
        res.gridx = 1;
        res.gridy = 0;
        res.weighty = 0;
        res.weightx = 2;
        res.fill = GridBagConstraints.HORIZONTAL;
        return res;
    }

    private static GridBagConstraints getOperationConstraints() {
        GridBagConstraints res = new GridBagConstraints();
        res.gridheight = 1;
        res.gridwidth = 1;
        res.gridx = 2;
        res.gridy = 0;
        res.weighty = 0;
        res.weightx = 2;
        res.fill = GridBagConstraints.HORIZONTAL;
        return res;
    }

    private static GridBagConstraints getArgumentConstraints() {
        GridBagConstraints res = new GridBagConstraints();
        res.gridheight = 1;
        res.gridwidth = 1;
        res.gridx = 3;
        res.gridy = 0;
        res.weighty = 0;
        res.weightx = 6;
        res.fill = GridBagConstraints.HORIZONTAL;
        return res;
    }

    private static GridBagConstraints getButtonConstraints() {
        GridBagConstraints res = new GridBagConstraints();
        res.gridheight = 1;
        res.gridwidth = 1;
        res.gridx = 4;
        res.gridy = 0;
        res.weighty = 0;
        res.weightx = 1;
        res.fill = GridBagConstraints.HORIZONTAL;
        return res;
    }

    private static String getFileExtension(File f) {
        String path = f.getAbsolutePath();
        int lastIndex = path.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return path.substring(lastIndex + 1);
    }

    private static String getBaseName(File f) {
        String path = f.getAbsolutePath();
        int lastIndex = path.lastIndexOf('.');
        if (lastIndex == -1) {
            return path;
        }
        return path.substring(0, lastIndex);
    }

    private class FeatureObjectTypeRenderer extends JLabel implements ListCellRenderer<FeatureObjectType> {
        @Override
        public Component getListCellRendererComponent(JList<? extends FeatureObjectType> list, FeatureObjectType value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                this.setText(value.getFullName());
                this.setHorizontalAlignment(CENTER);
                this.setVerticalAlignment(CENTER);
            }
            return this;
        }
    }

    private static <E> void setOptimalSizeForComboBox(JComboBox<E> comboBox) {
        comboBox.setMinimumSize(new Dimension(GUIConstants.POLICY_PANEL_PREFERRED_COMBO_BOX_WIDTH,
                GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));
        comboBox.setPreferredSize(new Dimension(GUIConstants.POLICY_PANEL_PREFERRED_COMBO_BOX_WIDTH,
                GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));
        comboBox.setMaximumSize(new Dimension(GUIConstants.POLICY_PANEL_PREFERRED_COMBO_BOX_WIDTH,
                GUIConstants.PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT));
    }

    private class FeatureRenderer extends JLabel implements ListCellRenderer<Feature> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Feature> list, Feature value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                this.setText(value.getFeatureName());
                this.setHorizontalAlignment(CENTER);
                this.setVerticalAlignment(CENTER);
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
                this.setHorizontalAlignment(CENTER);
                this.setVerticalAlignment(CENTER);
            }
            return this;
        }
    }

    private class RemoveLineButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = PolicyPanel.this.removeLineButtons.indexOf(e.getSource());
            PolicyPanel.this.removeLineFromMainPanel(index);
        }
    }
}
