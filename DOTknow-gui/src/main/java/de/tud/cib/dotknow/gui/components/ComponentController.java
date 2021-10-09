package de.tud.cib.dotknow.gui.components;

import com.apstex.ifctoolbox.ifc.IfcRoot;
import de.tud.cib.dotknow.gui.MainGUI;
import de.tud.cib.dotknow.gui.components.anamnesis.properties.OntologyProperty;
import de.tud.cib.dotknow.gui.components.anamnesis.properties.PropertyDatabase;
import de.tud.cib.dotknow.gui.components.filter.FilterDefinition;
import de.tud.cib.dotknow.gui.components.filter.preconfig.FilterPreConfig;
import de.tud.cib.dotknow.gui.components.ifc.IfcStatus;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.PreConfigConfiguration;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.DiagnosisConfigData;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.SHACLRuleSet;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.TherapyConfigData;
import de.tud.cib.dotknow.gui.configuration.resources.ResourceConfiguration;
import de.tud.cib.dotknow.gui.configuration.resources.ontology.OntologyData;
import de.tud.cib.dotknow.gui.configuration.resources.ontology.SHACLData;
import de.tud.cib.dotknow.gui.utility.list.checkboxlist.JCheckBoxList;
import de.tud.cib.dotknow.gui.utility.list.checkboxlist.JCheckboxWithObject;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.activedata.treemodel.ICDDTreeCreator;
import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import de.tud.cib.dotknow.icdd.utility.filter.ICDDFilter;
import de.tud.dotknow.ontology.namespaces.OntologyNamespaces;
import de.tud.dotknow.ontology.representation.ActiveOntology;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.topbraid.shacl.validation.ValidationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Provides functions for manipulating the state of the GUI components
 */
public class ComponentController {

    private final String[] linkColumn = {"Document", "Identifier"};

    private final String[] constructionColumn = {"Data Property", "Value"};

    private final String[] damageColumn = {"Name", "DOT-Type", "Damage Class"};

    private final String[][] buildingData = {{"Construction Year", ""},{"Historic Preservation", ""}};

    private final String[] validationColumn = {"Node name", "Path", "Result Message"};

    private static ComponentController ourInstance = new ComponentController();

    private MainGUI mainGUI;

    public static ComponentController getInstance() {
        return ourInstance;
    }

    private ComponentController() {
    }

    /**
     * Sets the enability of all JButton objects that are relevant for ICDD Management according to a given boolean
     * @param enabled false = buttons disabled ; true = buttons enabled
     */
    public void enableICDDButtons(boolean enabled) {
        this.mainGUI.getAddResource().setEnabled(enabled);
        this.mainGUI.getLinkDocument().setEnabled(enabled);
        this.mainGUI.getLinkIFCEntity().setEnabled(enabled);
        this.mainGUI.getGuiMenu().getMiSaveProject().setEnabled(enabled);
    }

    /**
     * Updates the JTree representing the ICDD structure according to the status of the ActiveICDD object
     */
    public void updateICDDTree() {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        MainGUI.treeModel.setRoot(ICDDTreeCreator.createTreeModel(activeICDD.getIcdd()));
    }

    /**
     * Initially sets the Jtable, which shows all linked elements
     */
    public void setLinkTable() {
        JTable linkTable = this.mainGUI.getLinktable();
        linkTable.setModel(new DefaultTableModel(null, this.linkColumn));
    }

    /**
     * Queries for elements that are linked to the selected IFC entities and updates the link Jtable accordingly
     * @param ifcEntities The IFC entities selected in the IFC 3D Viewer
     */
    public void updateLinkTable(LinkedHashSet<IfcRoot> ifcEntities) {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        JTable linkTable = this.mainGUI.getLinktable();
        DefaultTableModel tableModel = (DefaultTableModel) linkTable.getModel();
        this.resetTableModel(tableModel);
        for (IfcRoot ifcEntity:
                ifcEntities) {
            ModelElement modelElement = new ModelElement(IfcStatus.getInstance().getIfcModelName(),
                    ifcEntity.getGlobalId().getDecodedValue());
            ArrayList<ModelElement> linkedElementList = ICDDFilter.filterLinkedElements(modelElement, activeICDD.getIcdd());
            for (ModelElement linkedElement :
                    linkedElementList) {
                tableModel.addRow(new Object[]{linkedElement.getDocumentName(), linkedElement.getElementID()});
            }
        }
        linkTable.updateUI();
    }

    /**
     * Initially sets the Jtable, which shows all properties regarding the OWL construction individual
     */
    public void setConstructionInformationTable() {
        JTable constructionInformationTable = this.mainGUI.getConstructionInformationTable();
        //TODO:Fallunterscheidung für unterschiedliche Konstruktionstypen tätigen
        DefaultTableModel tableModel = new DefaultTableModel(null, this.constructionColumn);
        LinkedHashSet<OntologyProperty> constructionPropertySet = PropertyDatabase.getInstance().getConstructionPropertySet();
        for (OntologyProperty property :
                constructionPropertySet) {
            String[] tableRow = {property.getLabel(), property.getValue()};
            tableModel.addRow(tableRow);
        }
        constructionInformationTable.setModel(tableModel);
    }

    /**
     * Initially sets the Jtable, which lists all damage individuals of the OWL ontology
     */
    public void setDamageList() {
        JTable damageList = this.mainGUI.getDamageTable();
        DefaultTableModel tableModel = new DefaultTableModel(null, this.damageColumn) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        damageList.setModel(tableModel);
    }

    /**
     * Adds elements to the damage table in the anamnesis section
     * @param damageName Individual name of the added damage
     * @param dotType DOT-Type of the added damage (DamageElement or DamageArea)
     * @param damageClass Domain-specific damage class of the added damage
     */
    public void addDamageToList(String damageName, String dotType, String damageClass) {
        JTable damageList = this.mainGUI.getDamageTable();
        DefaultTableModel damageListModel = (DefaultTableModel) damageList.getModel();
        damageListModel.addRow(new Object[] {damageName, dotType, damageClass});
        damageList.setModel(damageListModel);
        damageList.updateUI();
    }

    /**
     * Removes the selected damage from the damage table
     * @param damageTableIndex The table index that refers the damage, which should be removed
     */
    public void removeDamageFromList(int damageTableIndex) {
        JTable damageList = this.mainGUI.getDamageTable();
        DefaultTableModel damageListModel = (DefaultTableModel) damageList.getModel();
        damageListModel.removeRow(damageTableIndex);
        damageList.setModel(damageListModel);
        damageList.updateUI();
    }

    /**
     * Sets the enability of all UI objects that are relevant for the management of the construction ontology according to a given boolean
     * @param enabled false = ui objects disabled ; true = ui objects enabled
     */
    public void enableConstructionOntology(boolean enabled) {
        if (enabled == false)
            this.mainGUI.getConstructionOntologyStatusLabel().setText("No construction ontology has been generated");
        else
            this.mainGUI.getConstructionOntologyStatusLabel().setText("Construction ontology of " + IfcStatus.getInstance().getIfcModelName() + " is loaded");
        this.mainGUI.getCreateOntologyButton().setEnabled(!enabled);
        this.mainGUI.getConstructionPane().setEnabled(enabled);
        this.mainGUI.getConstructionInformationTable().setEnabled(enabled);
        this.mainGUI.getUpdateOntologyButton().setEnabled(true);
        enableDamageOntology(true);
        enableDiagnosisGUI(true);
    }

    /**
     * Sets the enability of all UI objects that are relevant for the management of the damage ontology according to a given boolean
     * @param enabled false = ui objects disabled ; true = ui objects enabled
     */
    public void enableDamageOntology(boolean enabled) {
        this.mainGUI.getCreateDamageButton().setEnabled(enabled);
        this.mainGUI.getAssignDamageButton().setEnabled(enabled);
        this.mainGUI.getDeleteDamageButton().setEnabled(enabled);
        this.mainGUI.getConfigurationComboBox().setEnabled(enabled);
    }

    /**
     * Sets the enability of all UI objects that are grouped in the Diagnosis section of the plattform.
     * This affects the reasoning and validation GUI elements.
     * @param enabled false = ui objects disabled ; true = ui objects enabled
     */
    public void enableDiagnosisGUI(boolean enabled) {
        this.mainGUI.getReasonButton().setEnabled(enabled);
        this.mainGUI.getValidateButton().setEnabled(enabled);
        this.mainGUI.getTboxList().setEnabled(enabled);
        this.mainGUI.getTboxList().setEnabled(enabled);
    }

    /**
     * Initially sets the JComboBox for showing the selection of predefined Diagnosis Configs.
     */
    public void setDiagnosisConfigCombobox() {
        JComboBox configurationComboBox = this.mainGUI.getConfigurationComboBox();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        ArrayList<DiagnosisConfigData> diagnosisConfigDataList = PreConfigConfiguration.getInstance().getDiagnosisConfigDataList();
        for (DiagnosisConfigData diagnosisConfigData :
                diagnosisConfigDataList) {
            comboBoxModel.addElement(diagnosisConfigData.getName());
        }
        configurationComboBox.setModel(comboBoxModel);
        configurationComboBox.updateUI();
        this.setTBoxCheckBoxList(diagnosisConfigDataList.get(0).getName());
        this.setSHACLCheckBoxList(diagnosisConfigDataList.get(0).getName());
    }

    //TODO:Methoden für TBOX und SHACL Filterung sehr ähnlich -> zusammenfassen bzw. generischer programmieren
    /**
     * Sets the checkbox list of tboxes referenced in the selected diagnosis config.
     * @param diagnosisConfigName name of the given tbox config
     */
    public void setTBoxCheckBoxList(String diagnosisConfigName) {
        ResourceConfiguration resourceConfiguration = ResourceConfiguration.getInstance();
        JCheckBoxList tboxCheckList = this.mainGUI.getTboxList();
        DefaultListModel tboxCheckListModel = new DefaultListModel();
        ArrayList<DiagnosisConfigData> diagnosisConfigDataList = PreConfigConfiguration.getInstance().getDiagnosisConfigDataList();
        for (DiagnosisConfigData tboxConfigData :
                diagnosisConfigDataList) {
            if (tboxConfigData.getName().equals(diagnosisConfigName)) {
                ArrayList<String> tboxList = tboxConfigData.getTboxList();
                for (String tboxReference :
                        tboxList) {
                    OntologyData ontologyData = resourceConfiguration.getTboxEntry(tboxReference);
                    tboxCheckListModel.addElement(new JCheckboxWithObject(ontologyData));
                }
            }
        }
        tboxCheckList.setModel(tboxCheckListModel);
    }

    /**
     * Sets the checkbox list of shacl-rules referenced in the selected diagnosis config.
     * @param diagnosisConfigName name of the given diagnosis config
     */
    public void setSHACLCheckBoxList(String diagnosisConfigName) {
        ResourceConfiguration resourceConfiguration = ResourceConfiguration.getInstance();
        JCheckBoxList shaclCheckBoxList = this.mainGUI.getShaclList();
        DefaultListModel shaclCheckListModel = new DefaultListModel();
        ArrayList<DiagnosisConfigData> diagnosisConfigDataList = PreConfigConfiguration.getInstance().getDiagnosisConfigDataList();
        for (DiagnosisConfigData diagnosisConfigData :
                diagnosisConfigDataList) {
            if (diagnosisConfigData.getName().equals(diagnosisConfigName)) {
                ArrayList<String> shaclList = diagnosisConfigData.getShaclList();
                for (String shaclReference :
                        shaclList) {
                    SHACLData shaclData = resourceConfiguration.getSHACLEntry(shaclReference);
                    shaclCheckListModel.addElement(new JCheckboxWithObject(shaclData));
                }
            }
        }
        shaclCheckBoxList.setModel(shaclCheckListModel);
    }

    /**
     * Initially sets the Jtable, which shows all violations from the validation report
     */
    public void setValidationResultTable() {
        JTable validationResultTable = this.mainGUI.getValidationResultTable();
        DefaultTableModel tableModel = new DefaultTableModel(null, this.validationColumn) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        validationResultTable.setModel(tableModel);
    }

    /**
     * Initially sets the comboBox, through which all predefined filters could be selected
     */
    public void setFilterComboBox() {
        List<FilterDefinition> filterPreConfigList = FilterPreConfig.getInstance().getPreconfigList();
        JComboBox comboBox = mainGUI.getPredefinedFilterComboBox();
        DefaultComboBoxModel<FilterDefinition> comboBoxModel = new DefaultComboBoxModel<>();
        filterPreConfigList.forEach(filterDefinition -> {
            comboBoxModel.addElement(filterDefinition);
        });
        comboBox.setModel(comboBoxModel);
        comboBox.updateUI();
    }

    public void modifyValidationResultTable(List<ValidationResult> validationResultList) {
        JTable validationResultTable = this.mainGUI.getValidationResultTable();
        DefaultTableModel tableModel = (DefaultTableModel) validationResultTable.getModel();
        for (ValidationResult validationResult :
                validationResultList) {
            String focusNode = validationResult.getFocusNode().asResource().getLocalName();
            String path = validationResult.getPath().getLocalName();
            String resultMessage = validationResult.getMessage();
            tableModel.addRow(new String[]{focusNode, path, resultMessage});
        }
        validationResultTable.setModel(tableModel);
        validationResultTable.updateUI();
    }

    /**
     * Initially sets the JComboBox for showing the selection of predefined Therapy Configs.
     */
    public void setTherapyConfigCombobox() {
        JComboBox configurationComboBox = this.mainGUI.getRenovationConfigurationComboBox();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        ArrayList<TherapyConfigData> therapyConfigDataList = PreConfigConfiguration.getInstance().getTherapyConfigDataList();
        for (TherapyConfigData therapyConfigData :
                therapyConfigDataList) {
            comboBoxModel.addElement(therapyConfigData.getName());
        }
        configurationComboBox.setModel(comboBoxModel);
        configurationComboBox.updateUI();
        this.setRenovationSHACLCheckBoxList(therapyConfigDataList.get(0).getName());
    }

    /**
     * Sets the checkbox list of shacl-rules referenced in the selected diagnosis config.
     * @param therapyConfigName name of the given diagnosis config
     */
    public void setRenovationSHACLCheckBoxList(String therapyConfigName) {
        JCheckBoxList shaclCheckBoxList = this.mainGUI.getRenovationSHACLList();
        DefaultListModel shaclCheckListModel = new DefaultListModel();
        ArrayList<TherapyConfigData> therapyConfigDataList = PreConfigConfiguration.getInstance().getTherapyConfigDataList();
        for (TherapyConfigData therapyConfigData :
                therapyConfigDataList) {
            if (therapyConfigData.getName().equals(therapyConfigName)) {
                ArrayList<SHACLRuleSet> shaclList = therapyConfigData.getSHACLRuleConfigList();
                for (SHACLRuleSet shaclRuleSet :
                        shaclList) {
                    shaclCheckListModel.addElement(new JCheckboxWithObject(shaclRuleSet));
                }
            }
        }
        shaclCheckBoxList.setModel(shaclCheckListModel);
    }

    /**
     * Sets the list that references all possible renovation measures
     */
    public void setRenovationResultList() {
        IfcStatus ifcStatus = IfcStatus.getInstance();
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        OntModel activeOntModel = activeOntology.getOntology();
        String ontologyURI = activeOntology.getOntologyURI();
        JList renovationMeasureResultList = this.mainGUI.getRenovationMeasureResultList();
        DefaultListModel listModel = new DefaultListModel();
        LinkedHashSet<ModelElement> selectedIfcElements = ifcStatus.getIfcModelElements();
        for (ModelElement ifcElement :
                selectedIfcElements) {
            Individual ifcIndividual = activeOntModel.getIndividual(ontologyURI + ifcElement.getElementID());
            //TODO: separate Definitionen für SRMO erstellen und referenzieren
            //TODO: Bug -> kein Bezug auf Verlinkung in URI!
            //TODO: Zugehörigkeit von Schaden zu Element prüfen
            StmtIterator stmtIterator = activeOntModel.listStatements();
            stmtIterator.forEachRemaining(statement -> {
                if (statement.getPredicate().getURI().equals(OntologyNamespaces.SRMO_URI + "recommendedRenovationMeasure")) {
                    String objectURI = statement.getObject().asResource().getURI();
                    Individual renovationMeasure = activeOntModel.getIndividual(objectURI);
                    String renovationMeasureName = renovationMeasure.getOntClass().getLocalName();
                    if (!listModel.contains(renovationMeasureName))
                        listModel.addElement(renovationMeasureName);
                }
            });
        }
        renovationMeasureResultList.setModel(listModel);
        renovationMeasureResultList.updateUI();
    }

    /*
    Setter
     */

    public void setMainGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    /*
    Internal Functions
     */

    private DefaultTableModel resetTableModel(DefaultTableModel tableModel) {
        if (tableModel.getRowCount() > 0) {
            for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
                tableModel.removeRow(i);
            }
        }
        return tableModel;
    }
}
