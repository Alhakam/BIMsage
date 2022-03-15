package de.tud.cib.bimsage.gui.components.anamnesis.damage;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.tud.cib.bimsage.icdd.activedata.ActiveICDD;
import de.tud.cib.bimsage.icdd.configuration.input.ModelElement;
import de.tud.cib.bimsage.icdd.utility.filter.IfcICDDFilter;
import de.tud.cib.bimsage.gui.components.ComponentController;
import de.tud.cib.bimsage.gui.configuration.resources.ResourceConfiguration;
import de.tud.cib.bimsage.gui.configuration.resources.ontology.OntologyData;
import de.tud.cib.bimsage.gui.utility.messages.MessageFactory;
import de.tud.cib.bimsage.ontology.namespaces.OntologyNamespaces;
import de.tud.cib.bimsage.ontology.namespaces.tbox.DOT;
import de.tud.cib.bimsage.ontology.representation.ActiveOntology;
import icdd.beans.Container;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class ConfigureDamageDialog extends JDialog {
    private JComboBox damageClassComboBox;
    private JComboBox dotTypeComboBox;
    private JComboBox classificationDomainComboBox;
    private JTextField damageNameBox;
    private JButton addAggregatedDamageButton;
    private JTable propertyTable;
    private JPanel contentPane;
    private JButton buttonCreate;
    private JButton buttonCancel;
    private JList relatedDamagesList;
    private JButton removeDamageButton;

    private final String appTitle = "Construction Ontology Creation";
    private final int posX = 250;
    private final int posY = 250;
    private final String dotTypeDamageElementName = "Damage Element";
    private final String dotTypeDamageAreaName = "Damage Area";
    private final String propertyTableTypeName = "Data Property";
    private final String propertyTableValueName = "Value";
    private final String modifyButtonText = "Modify";

    private ResourceConfiguration resourceConfiguration;
    private String cdoDomainLabel;
    private String nsdDomainLabel;

    private OntModel selectedTBox;
    private OntologyData selectedTBoxData;
    private int modifiedDamageIndex;
    private LinkedHashSet<ModelElement> selectedIFCElements;

    public ConfigureDamageDialog(LinkedHashSet<ModelElement> selectedIFCElements, int modifiedDamageIndex) {

        this.resourceConfiguration = ResourceConfiguration.getInstance();
        this.cdoDomainLabel = resourceConfiguration.getTboxEntry("cdo").getDomainLabel();
        this.nsdDomainLabel = resourceConfiguration.getTboxEntry("nsd").getDomainLabel();

        this.selectedIFCElements = selectedIFCElements;
        this.modifiedDamageIndex = modifiedDamageIndex;
        this.initDomainComboBox();
        this.initPropertyTable();
        this.onDomainSelection(classificationDomainComboBox.getSelectedItem().toString());

        if (modifiedDamageIndex >= 0)
            this.arrangeGUIForModification();

        /*
        Listeners
         */

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        classificationDomainComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    onDomainSelection(e.getItem().toString());
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        damageClassComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    updateDamageProperties(e.getItem().toString());
            }
        });

        buttonCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreate();
            }
        });

        addAggregatedDamageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddAggregatedDamage();
            }
        });

        this.panelVisualization();
        removeDamageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemoveDamage();
            }
        });
    }

    /*
    Initialization
     */

    private void initDomainComboBox() {
        DefaultComboBoxModel domainComboBoxModel = new DefaultComboBoxModel();
        domainComboBoxModel.addElement(cdoDomainLabel);
        domainComboBoxModel.addElement(nsdDomainLabel);
        this.classificationDomainComboBox.setModel(domainComboBoxModel);
    }

    private void initPropertyTable() {
        this.propertyTable.setModel(this.resetPropertyTableModel());
    }

    //TODO: Methode für DialogFenster als Template erstellen (und in Util-Klasse auslagern?)
    private void panelVisualization() {
        this.setTitle(appTitle);
        this.setContentPane(contentPane);
        this.setModal(false);
        this.getRootPane().setDefaultButton(buttonCreate);
        this.pack();
        this.setLocation(posX, posY);
        this.setVisible(true);
    }

    /*
    Action Events
     */

    private void onCancel() {
        dispose();
    }

    //TODO: Überlegen Switch Case Funktion einzubauen
    private void onDomainSelection(String selectedDomain) {
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        selectedTBoxData = null;
        if (selectedDomain.equals(cdoDomainLabel))
            selectedTBoxData = resourceConfiguration.getTboxEntry("cdo");
        if (selectedDomain.equals(nsdDomainLabel))
            selectedTBoxData = resourceConfiguration.getTboxEntry("nsd");
        this.setSelectedTBox(selectedTBoxData.getUrl());
        LinkedHashSet<String> classNames = this.getClassesOfDomain(selectedTBoxData.getNamespace() + selectedTBoxData.getRootClass());
        for (String className :
                classNames) {
            comboBoxModel.addElement(className);
        }
        this.damageClassComboBox.setModel(comboBoxModel);
        this.updateDamageProperties(this.damageClassComboBox.getSelectedItem().toString());
    }

    private void updateDamageProperties(String selectedClass) {
        String classUri = selectedTBoxData.getNamespace() + selectedClass;
        LinkedHashSet<String> dataPropertyList = this.getDataPropertiesofClass(classUri);
        DefaultTableModel propertyTableModel = this.resetPropertyTableModel();
        for (String dataProperty :
                dataPropertyList) {
            propertyTableModel.addRow(new Object[]{dataProperty, ""});
        }
        this.propertyTable.setModel(propertyTableModel);
    }

    private void onCreate() {
        if (modifiedDamageIndex >= 0) {
            DamageStatus.getInstance().getSelectedDamageIndividualBasedOnTableIndex(modifiedDamageIndex).remove();
            ComponentController.getInstance().removeDamageFromList(modifiedDamageIndex);
        }
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        OntModel activeOntModel = activeOntology.getOntology();
        String damageName = damageNameBox.getText();
        String damageClass = damageClassComboBox.getSelectedItem().toString();
        String dotType = null;
        String selectedDotType = dotTypeComboBox.getSelectedItem().toString();
        if (selectedDotType.equals(dotTypeDamageElementName))
            dotType = DOT.DAMAGE_ELEMENT;
        else if (selectedDotType.equals(dotTypeDamageAreaName))
            dotType = DOT.DAMAGE_AREA;
        Individual damageIndividual = this.createDamageIndividual(activeOntModel, activeOntology.getOntologyURI(), dotType);
        //TODO: Weitergabe des Singletons ActiveOntology unnötig -> Entfernen
        this.addPropertiesToDamage(activeOntModel, damageIndividual);
        this.addDamageAggregationsToDamage(activeOntModel, activeOntology.getOntologyURI(), damageIndividual);
        this.linkDamageToComponents(damageIndividual, activeOntModel, activeOntology.getFileName(), dotType);
        activeOntology.setOntology(activeOntModel);
        try {
            ActiveICDD.getInstance().saveOntologyinICDD(activeOntModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ComponentController.getInstance().addDamageToList(damageName, selectedDotType, damageClass);
        dispose();
        if (modifiedDamageIndex >= 0)
            MessageFactory.showMessageWithCustomString(MessageFactory.MessageType.DAMAGE_MODIFIED, damageNameBox.getText());
        else
            MessageFactory.showMessageWithCustomString(MessageFactory.MessageType.DAMAGE_CREATED, damageNameBox.getText());
    }

    private void onAddAggregatedDamage() {
        /*Vector<String> damageList = new Vector<>();
        OntModel ontology = ActiveOntology.getInstance().getOntology();
        OntClass damageElementClass = ontology.getOntClass(DOT.DAMAGE_ELEMENT);
        ontology.listIndividuals(damageElementClass).forEachRemaining(damageElement -> {
            damageList.add(damageElement.getLocalName());
        });*/
        new AggregateDamageDialog(this);
    }

    private void onRemoveDamage() {
        List<String> selectedDamageList = relatedDamagesList.getSelectedValuesList();
        DefaultListModel<String> damageListModel = (DefaultListModel<String>) relatedDamagesList.getModel();
        for (String damage :
                selectedDamageList) {
            damageListModel.removeElement(damage);
        }
    }

    private void addDamageAggregationsToDamage(OntModel activeOntModel, String ontologyURI, Individual damageIndividual) {
        ObjectProperty aggregatesDamageElement = activeOntModel.createObjectProperty(DOT.AGGREGATES_DAMAGE_ELEMENT);
        ListModel listModel = relatedDamagesList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            String damageName = (String) listModel.getElementAt(i);
            activeOntModel.add(damageIndividual, aggregatesDamageElement,
                    activeOntModel.getIndividual(ontologyURI + damageName));
        }
    }

    /*
    Internal Methods
     */

    private void setSelectedTBox(String ontologyURL) {
        selectedTBox = ModelFactory.createOntologyModel();
        selectedTBox.read(ontologyURL);
    }

    private DefaultTableModel resetPropertyTableModel() {
        DefaultTableModel propertyTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // make read only fields except column 1
                return column == 1;
            }
        };
        propertyTableModel.addColumn(propertyTableTypeName);
        propertyTableModel.addColumn(propertyTableValueName);
        return propertyTableModel;
    }

    private LinkedHashSet<String> getClassesOfDomain(String rootClassURI) {
        LinkedHashSet<String> classNames = new LinkedHashSet<>();
        OntClass rootClass = selectedTBox.getOntClass(rootClassURI);
        classNames.addAll(this.getSubClassesOfClass(rootClass));
        return classNames;
    }

    private LinkedHashSet<String> getSubClassesOfClass(OntClass ontClass) {
        LinkedHashSet<String> classNames = new LinkedHashSet<>();
        ontClass.listSubClasses().forEachRemaining(subClass -> {
            classNames.add(subClass.getLocalName());
            if (subClass.hasSubClass())
                classNames.addAll(this.getSubClassesOfClass(subClass));
        });
        return classNames;
    }

    private LinkedHashSet<String> getDataPropertiesofClass(String classURI) {
        LinkedHashSet<String> propertyNameSet = new LinkedHashSet<>();
        OntClass ontClass = selectedTBox.getOntClass(classURI);
        selectedTBox.listDatatypeProperties().forEachRemaining(datatypeProperty -> {
            String propertyName = datatypeProperty.getLocalName();
            if (datatypeProperty.hasDomain(ontClass) || ontClass.hasSuperClass(datatypeProperty.getDomain()))
                propertyNameSet.add(propertyName);
            else if (this.hasDomainInclusion(datatypeProperty, ontClass)) {
                propertyNameSet.add(propertyName);
            }
        });
        return propertyNameSet;
    }

    //TODO: Magic Number entfernen (Schema.org)
    private boolean hasDomainInclusion(DatatypeProperty datatypeProperty, OntClass ontClass) {
        AtomicBoolean domainInclusion = new AtomicBoolean(false);
        ObjectProperty domainIncludes = selectedTBox.getObjectProperty(OntologyNamespaces.SCHEMA_URI + "domainIncludes");
        if (!(domainIncludes == null)) {
            datatypeProperty.listProperties(domainIncludes).forEachRemaining(domainIncludesStatement -> {
                Resource includedClass = (Resource) domainIncludesStatement.getObject();
                if (ontClass.hasSuperClass(includedClass)) {
                    domainInclusion.set(true);
                }
            });
        }
        return domainInclusion.get();
    }

    private Individual createDamageIndividual(OntModel activeOntModel, String ontologyURI, String dotType) {
        OntClass dotClass = activeOntModel.createClass(dotType);
        OntClass damageSpecification = activeOntModel.createClass(selectedTBoxData.getNamespace() + damageClassComboBox.getSelectedItem().toString());
        Individual damageIndividual = activeOntModel.createIndividual(ontologyURI + damageNameBox.getText(), dotClass);
        damageIndividual.addOntClass(damageSpecification);
        return damageIndividual;
    }

    private void addPropertiesToDamage(OntModel activeOntModel, Individual damageIndividual) {
        TableModel propertyTableModel = propertyTable.getModel();
        for (int i = 0; i < propertyTableModel.getRowCount(); i++) {
            String propertyName = (String) propertyTableModel.getValueAt(i, 0);
            String propertyValue = (String) propertyTableModel.getValueAt(i, 1);
            Property property = activeOntModel.createProperty(selectedTBoxData.getNamespace() + propertyName);
            if (StringUtils.isNumeric(propertyValue))
                activeOntModel.addLiteral(damageIndividual, property, Double.parseDouble(propertyValue));
            else
                activeOntModel.add(damageIndividual, property, propertyValue);
        }
    }

    private void arrangeGUIForModification() {
        DamageStatus damageStatus = DamageStatus.getInstance();
        damageNameBox.setText(damageStatus.getSelectedDamageNameBasedOnTableIndex(modifiedDamageIndex));
        Individual damage = damageStatus.getSelectedDamageIndividualBasedOnTableIndex(modifiedDamageIndex);
        this.setDamageComboBoxes(damage);
        this.setDamageProperties(damage);
        this.setDamageAggregations(damage);
        buttonCreate.setText(modifyButtonText);
    }

    private void setDamageComboBoxes(Individual damage) {
        if (damage.hasOntClass(DOT.DAMAGE_AREA))
            dotTypeComboBox.setSelectedIndex(1);
        SelectedDamage selectedDamage = this.determineClassificationDomain(damage);
        classificationDomainComboBox.setSelectedItem(selectedDamage.getOntologyData().getDomainLabel());
        this.onDomainSelection(selectedDamage.getOntologyData().getDomainLabel());
        damageClassComboBox.setSelectedItem(selectedDamage.getDirectDamageClassName());
    }

    private SelectedDamage determineClassificationDomain(Individual damage) {
        SelectedDamage selectedDamage = new SelectedDamage(damage.getURI());
        ArrayList<OntologyData> tboxList = resourceConfiguration.getExtensionList();
        for (OntologyData tbox :
                tboxList) {
            this.setSelectedTBox(tbox.getUrl());
            LinkedHashSet<String> classNames = this.getClassesOfDomain(tbox.getNamespace() + tbox.getRootClass());
            for (String className :
                    classNames) {
                if (damage.hasOntClass(tbox.getNamespace() + className)) {
                    selectedDamage.setOntologyData(tbox);
                    selectedDamage.setDirectDamageClassName(className);
                    return selectedDamage;
                }
            }
        }
        return null;
    }

    private void setDamageProperties(Individual damage) {
        DefaultTableModel propertyTableModel = (DefaultTableModel) propertyTable.getModel();
        damage.listProperties().forEachRemaining(statement -> {
            String propertyName = statement.getPredicate().getLocalName();
            for (int i = 0; i < propertyTableModel.getRowCount(); i++) {
                if (propertyName.equals(propertyTableModel.getValueAt(i, 0))) {
                    propertyTableModel.setValueAt(statement.getLiteral().getString(), i, 1);
                    break;
                }
            }
        });
        propertyTable.updateUI();
    }

    private void setDamageAggregations(Individual damage) {
        DefaultListModel<String> damageListModel = new DefaultListModel<>();
        OntModel activeOntModel = ActiveOntology.getInstance().getOntology();
        ObjectProperty aggregatesDamageElement = activeOntModel.getObjectProperty(DOT.AGGREGATES_DAMAGE_ELEMENT);
        damage.listProperties(aggregatesDamageElement).forEachRemaining(statement -> {
            String damageName = statement.getObject().asResource().getLocalName();
            damageListModel.addElement(damageName);
        });
        relatedDamagesList.setModel(damageListModel);
    }

    //TODO:Fehlerabhandlung für fehlende Links ausführen
    private void linkDamageToComponents(Individual damage, OntModel ontology, String ontologyFileName, String dotType) {
        Container icdd = ActiveICDD.getInstance().getIcdd();
        ObjectProperty hasDamage = null;
        if (dotType.equals(DOT.DAMAGE_AREA))
            hasDamage = ontology.createObjectProperty(DOT.HAS_DAMAGE_AREA);
        else if (dotType.equals(DOT.DAMAGE_ELEMENT))
            hasDamage = ontology.createObjectProperty(DOT.HAS_DAMAGE_ELEMENT);
        for (ModelElement ifcElement :
                selectedIFCElements) {
            String componentURI = IfcICDDFilter.filterForOntRepresentationURI(ifcElement, ontologyFileName, icdd);
            Individual component = ontology.getIndividual(componentURI);
            ontology.add(component, hasDamage, damage);
        }
    }

    /*
    Getter
     */

    public JList getRelatedDamagesList() {
        return relatedDamagesList;
    }

    /*
    Setter
     */

    public void setRelatedDamagesList(JList relatedDamagesList) {
        this.relatedDamagesList = relatedDamagesList;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(8, 5, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("DOT Type");
        contentPane.add(label1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Classification Domain");
        contentPane.add(label2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Damage Class");
        contentPane.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        damageClassComboBox = new JComboBox();
        contentPane.add(damageClassComboBox, new GridConstraints(3, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Damage Name");
        contentPane.add(label4, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dotTypeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Damage Element");
        defaultComboBoxModel1.addElement("Damage Area");
        dotTypeComboBox.setModel(defaultComboBoxModel1);
        contentPane.add(dotTypeComboBox, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        classificationDomainComboBox = new JComboBox();
        classificationDomainComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        classificationDomainComboBox.setModel(defaultComboBoxModel2);
        contentPane.add(classificationDomainComboBox, new GridConstraints(2, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        damageNameBox = new JTextField();
        contentPane.add(damageNameBox, new GridConstraints(0, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Damage Attributes");
        contentPane.add(label5, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        propertyTable = new JTable();
        scrollPane1.setViewportView(propertyTable);
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Related Damages");
        contentPane.add(label6, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        contentPane.add(scrollPane2, new GridConstraints(6, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        relatedDamagesList = new JList();
        scrollPane2.setViewportView(relatedDamagesList);
        removeDamageButton = new JButton();
        removeDamageButton.setText("Remove");
        contentPane.add(removeDamageButton, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCreate = new JButton();
        buttonCreate.setText("Create");
        contentPane.add(buttonCreate, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        contentPane.add(buttonCancel, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addAggregatedDamageButton = new JButton();
        addAggregatedDamageButton.setText("Add");
        contentPane.add(addAggregatedDamageButton, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
