package de.tud.cib.bimsage.gui;

import com.apstex.gui.core.views.modelexplorer.ModelExplorer;
import com.apstex.gui.ifc.views.spatialview.IfcSpatialStructureView;
import com.apstex.gui.ifc.views.typeview.IfcTypeView;
import com.apstex.gui.ifc.views.view3d.j3d.ModelViewer;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.io.Files;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.tud.cib.bimsage.icdd.activedata.ActiveICDD;
import de.tud.cib.bimsage.icdd.activedata.treemodel.ICDDTreeCreator;
import de.tud.cib.bimsage.icdd.configuration.ICDDModifier;
import de.tud.cib.bimsage.icdd.configuration.input.ModelElement;
import de.tud.cib.bimsage.gui.components.ComponentController;
import de.tud.cib.bimsage.gui.components.anamnesis.construction.ConstructionOntologyDialog;
import de.tud.cib.bimsage.gui.components.anamnesis.damage.AggregateDamageDialog;
import de.tud.cib.bimsage.gui.components.anamnesis.damage.ConfigureDamageDialog;
import de.tud.cib.bimsage.gui.components.anamnesis.damage.DamageStatus;
import de.tud.cib.bimsage.gui.components.anamnesis.properties.OntologyProperty;
import de.tud.cib.bimsage.gui.components.anamnesis.properties.PropertyDatabase;
import de.tud.cib.bimsage.gui.components.filter.FilterDefinition;
import de.tud.cib.bimsage.gui.components.filter.preconfig.FilterPreConfig;
import de.tud.cib.bimsage.gui.components.icdd.DocumentSelectionDialog;
import de.tud.cib.bimsage.gui.components.icdd.ICDDFileManager;
import de.tud.cib.bimsage.gui.components.ifc.IfcStatus;
import de.tud.cib.bimsage.gui.components.menu.GUIMenu;
import de.tud.cib.bimsage.gui.configuration.preconfigdata.PreConfigConfiguration;
import de.tud.cib.bimsage.gui.configuration.preconfigdata.configbeans.SHACLRuleSet;
import de.tud.cib.bimsage.gui.configuration.resources.ResourceConfiguration;
import de.tud.cib.bimsage.gui.configuration.resources.ontology.OntologyData;
import de.tud.cib.bimsage.gui.configuration.resources.ontology.SHACLData;
import de.tud.cib.bimsage.gui.controller.annotation.AnnotationController;
import de.tud.cib.bimsage.gui.utility.FileChooserUtility;
import de.tud.cib.bimsage.gui.utility.list.checkboxlist.JCheckBoxList;
import de.tud.cib.bimsage.gui.utility.list.checkboxlist.JCheckboxWithObject;
import de.tud.cib.bimsage.gui.utility.messages.MessageFactory;
import de.tud.cib.bimsage.ontology.namespaces.tbox.BOT;
import de.tud.cib.bimsage.ontology.reasoner.OWLReasonerManager;
import de.tud.cib.bimsage.ontology.reasoner.SHACLReasonerManager;
import de.tud.cib.bimsage.ontology.representation.ActiveOntology;
import icdd.beans.Container;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationResult;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class MainGUI {

    private static final String appTitle = "BIMsage";
    private static final Rectangle frameBounds = new Rectangle(100, 100, 1100, 900);
    public static DefaultTreeModel treeModel;
    //TODO: Predefined Annotations in XML zwischenspeichern
    public static final String[] predefinedAnnotations = {"BridgeComponent", "Stone", "StoneGap"};

    private final String validationReportFileName = "ValidationReport.ttl";
    private final String[] parameterColumn = {"Query Parameter", "Value"};


    private JPanel mainPanel;
    private GUIMenu guiMenu;
    private JTabbedPane navigationPane;
    private JTree icddTree;
    private ICDDTreeCreator icddTreeCreator;
    private ModelViewer modelViewer;
    private ModelExplorer modelExplorer;
    private JTabbedPane ifcNavigation;
    private IfcTypeView typeView;
    private IfcSpatialStructureView structureView;
    private JTabbedPane ifcEntityExplorer;
    private JButton addRessource;
    private JButton linkIFCEntity;
    private JButton linkDocument;
    private JTable linktable;
    private JScrollPane linkScrollPane;
    private JRadioButton radioButtonPredefinedAnnotation;
    private JComboBox predefinedAnnotationBox;
    private JRadioButton radioButtonFreeAnnotation;
    private JTextField freeAnnotationField;
    private JButton applyAnnotationButton;
    private JTabbedPane anamnesisPane;
    private JLabel constructionOntologyStatusLabel;
    private JButton createOntologyButton;
    private JButton updateOntologyButton;
    private JScrollPane constructionPane;
    private JTable constructionInformationTable;
    private JButton createDamageButton;
    private JButton assignDamageButton;
    private JButton deleteDamageButton;
    private JTable damageTable;
    private JComboBox configurationComboBox;
    private JCheckBoxList tboxList;
    private JButton reasonButton;
    private JCheckBoxList shaclList;
    private JButton validateButton;
    private JTable validationResultTable;
    private JScrollPane validationResultPane;
    private JCheckBoxList renovationSHACLList;
    private JList renovationMeasureResultList;
    private JButton reasonRenovationButton;
    private JComboBox renovationConfigurationComboBox;
    private JTabbedPane tabbedPane1;
    private JComboBox predefinedFilterComboBox;
    private JButton predefinedQueryButton;
    private JTable predefinedQueryResultTable;
    private JTextArea sparqlQueryInputArea;
    private JButton sparqlQueryButton;
    private JTable sparqlQueryResultTable;
    private JTable filterParameterTable;
    private JLabel predefinedQueryDescription;

    public MainGUI() {
        $$$setupUI$$$();
        addRessource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddResource();
            }
        });
        linkDocument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLinkDocuments();
            }
        });
        linkIFCEntity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLinkIfcEntity();
            }
        });
        radioButtonFreeAnnotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onFreeAnnotation();
            }
        });
        radioButtonPredefinedAnnotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPredefinedAnnotation();
            }
        });
        applyAnnotationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onAddAnnotation();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        createOntologyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateConstructionOntology();
            }
        });
        updateOntologyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onUpdateConstructionOntology();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        createDamageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateDamage();
            }
        });
        damageTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                int row = table.rowAtPoint(e.getPoint());
                if (e.getClickCount() == 2) {
                    onModifyDamage(row);
                }
            }
        });
        assignDamageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAssignDamage();
            }
        });
        deleteDamageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteDamage();
            }
        });
        reasonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onReasonDiagnosis();
            }
        });
        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onValidateDiagnosis();
            }
        });
        reasonRenovationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onReasonTherapy();
            }
        });
        sparqlQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onQuerySPARQL();
            }
        });
        predefinedFilterComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    onPredefinedQuerySelection((FilterDefinition) e.getItem());
            }
        });
        predefinedQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onQueryPredefinedFilter();
            }
        });
    }

    //TODO: StatusBar für Laden des Viewers einfügen von Apstex
    //TODO: Verlinken von Dokumenten und IFC-Entitäten -> Dialogfeld soll dafür Linkset-Management regeln
    private void createUIComponents() {
        modelViewer = new ModelViewer();
        modelExplorer = new ModelExplorer();
        icddTree = new JTree(treeModel);
        predefinedAnnotationBox = new JComboBox(predefinedAnnotations);
        DefaultListModel tboxListModel = new DefaultListModel();
        tboxList = new JCheckBoxList();
        tboxList.setModel(tboxListModel);
        DefaultListModel shaclListModel = new DefaultListModel();
        shaclList = new JCheckBoxList();
        shaclList.setModel(shaclListModel);
    }

    public static void main(String[] args) {
        ResourceConfiguration resourceConfiguration = ResourceConfiguration.getInstance();
        PreConfigConfiguration preConfigConfiguration = PreConfigConfiguration.getInstance();
        FilterPreConfig filterPreConfig = FilterPreConfig.getInstance();
        FileChooserUtility.preLoad();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Throwable e) {
            System.err.println("Failed to initialize LaF-GUI");
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    initPropertyDatabase();
                    treeModel = new DefaultTreeModel(ICDDTreeCreator.createEmptyICDDTree());
                    JFrame frame = new JFrame(appTitle);
                    MainGUI mainGUI = new MainGUI();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setContentPane(mainGUI.mainPanel);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setBounds(frameBounds);
                    frame.pack();
                    initComponentController(mainGUI);
                    DamageStatus.getInstance().setMainGUI(mainGUI);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    Internal PSVM Methods
     */

    private static void initPropertyDatabase() {
        PropertyDatabase propertyDatabase = PropertyDatabase.getInstance();
        propertyDatabase.addConstructionProperty(new OntologyProperty("Construction Year", "https://wisib.de/ontologie/bridge#constructionYear", ""));
        propertyDatabase.addConstructionProperty(new OntologyProperty("Historic Preservation", "https://wisib.de/ontologie/bridge#historicPreservation", ""));
    }

    private static void initComponentController(MainGUI mainGUI) {
        ComponentController componentController = ComponentController.getInstance();
        componentController.setMainGUI(mainGUI);
        componentController.setDiagnosisConfigCombobox();
        componentController.setValidationResultTable();
        componentController.setTherapyConfigCombobox();
        componentController.setFilterComboBox();
    }

    /*
    Action Events
     */

    private void onAddResource() {
        Path resource = FileChooserUtility.browseFile().toPath();
        if (resource != null) {
            ActiveICDD activeICDD = ActiveICDD.getInstance();
            Container modifiedICDD = ICDDModifier.addResourceToICDD(resource, activeICDD.getIcdd());
            activeICDD.setIcdd(modifiedICDD);
            ComponentController.getInstance().updateICDDTree();
            MessageFactory.showMessageWithCustomString(MessageFactory.MessageType.ICDD_ADDEDRESOURCE, resource.getFileName().toString());
        }
    }

    private void onLinkDocuments() {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        new DocumentSelectionDialog(activeICDD.listDocuments(), null);
    }

    private void onLinkIfcEntity() {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        new DocumentSelectionDialog(activeICDD.listDocuments(), IfcStatus.getInstance().getIfcModelElements());
    }

    private void onPredefinedAnnotation() {
        predefinedAnnotationBox.setEnabled(true);
        freeAnnotationField.setEnabled(false);
    }

    private void onFreeAnnotation() {
        predefinedAnnotationBox.setEnabled(false);
        freeAnnotationField.setEnabled(true);
    }

    //TODO:Exception falls Selected IFC Items leer sind erstellen
    private void onAddAnnotation() throws IOException {
        String annotation = null;
        if (predefinedAnnotationBox.isEnabled())
            annotation = String.valueOf(predefinedAnnotationBox.getModel().getSelectedItem());
        else
            annotation = freeAnnotationField.getText();
        LinkedHashSet<ModelElement> selectedElementList = IfcStatus.getInstance().getIfcModelElements();
        AnnotationController.createAnnotationFile(annotation, selectedElementList);
        AnnotationController.linkAnnotationFile();
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.ANNOTATION_COMPLETE);
    }

    private void onCreateConstructionOntology() {
        new ConstructionOntologyDialog();
    }

    //TODO:Fallunterscheidung für Brücke machen
    //TODO:Funktion prüfen
    //TODO:Ontologie in ICDD nach Update integrieren
    private void onUpdateConstructionOntology() throws IOException {
        OntModel activeOntology = ActiveOntology.getInstance().getOntology();
        Individual building = activeOntology.listIndividuals(activeOntology.getOntClass(BOT.BUILDING)).next();
        TableModel tableModel = constructionInformationTable.getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String value = (String) tableModel.getValueAt(i, 1);
            String propertyLabel = (String) tableModel.getValueAt(i, 0);
            DatatypeProperty datatypeProperty = activeOntology.createDatatypeProperty(
                    PropertyDatabase.getInstance().getPropertyByLabel(propertyLabel).getUri());
            activeOntology.add(building, datatypeProperty, value);
        }
        ICDDFileManager.getInstance().updateOntology();
    }

    private void onCreateDamage() {
        new ConfigureDamageDialog(IfcStatus.getInstance().getIfcModelElements(), -1);
    }

    private void onModifyDamage(int row) {
        new ConfigureDamageDialog(IfcStatus.getInstance().getIfcModelElements(), row);
    }

    //TODO: Schäden müssen dem Bauteil zugewiesen werden (und nicht dem Schaden, da hierfür schon Aggregationsdialog in Modify)
    private void onAssignDamage() {
        if (damageTable.getSize().height > 0) {
            int selectedRow = damageTable.getSelectedRow();
            String damageName = (String) damageTable.getModel().getValueAt(selectedRow, 0);
            new AggregateDamageDialog(damageName);
        } else
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.ERROR_NO_DAMAGES);
    }

    //TODO: Sicherheitsabfrage erstellen
    private void onDeleteDamage() {
        if (damageTable.getSize().height > 0) {
            ActiveOntology activeOntology = ActiveOntology.getInstance();
            OntModel activeOntModel = activeOntology.getOntology();
            int selectedRow = damageTable.getSelectedRow();
            DefaultTableModel damageTableModel = (DefaultTableModel) damageTable.getModel();
            String damageName = (String) damageTableModel.getValueAt(selectedRow, 0);
            damageTableModel.removeRow(selectedRow);
            activeOntModel.getIndividual(activeOntology.getOntologyURI() + damageName).remove();
            activeOntology.setOntology(activeOntModel);
            MessageFactory.showMessageWithCustomString(MessageFactory.MessageType.DAMAGE_DELETED, damageName);
        } else
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.ERROR_NO_DAMAGES);
    }

    private void onReasonDiagnosis() {
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        this.reasonOWL();
        this.reasonSHACL(shaclList);
        try {
            ActiveICDD.getInstance().saveOntologyinICDD(activeOntology.getOntology());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.REASONING_FINISHED);
    }

    private void onValidateDiagnosis() {
        ValidationReport validationReport = this.validateSHACL();
        List<ValidationResult> validationResultList = validationReport.results();
        ComponentController.getInstance().modifyValidationResultTable(validationResultList);
        this.addValidationReportToICDD(this.validateSHACLAsResource());
        if (validationResultList.isEmpty())
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.VALIDATION_SUCCESSFUL);
        else
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.VALIDATION_FAILED);
    }

    private void onReasonTherapy() {
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        this.reasonSHACL(renovationSHACLList);
        try {
            ActiveICDD.getInstance().saveOntologyinICDD(activeOntology.getOntology());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ComponentController.getInstance().setRenovationResultList();
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.REASONING_FINISHED);
    }

    private void onQuerySPARQL() {
        ResultSet resultSet = this.processQuery(sparqlQueryInputArea.getText());
        TableModel resultTableModel = this.addResultSetToTableModel(resultSet);
        this.sparqlQueryResultTable.setModel(resultTableModel);
        this.sparqlQueryResultTable.updateUI();
    }

    private void onPredefinedQuerySelection(FilterDefinition filterDefinition) {
        this.predefinedQueryDescription.setText(filterDefinition.getDescription());
        this.setQueryParameterTable(filterDefinition);
        this.setResultTable(filterDefinition);
    }

    private void onQueryPredefinedFilter() {
        FilterDefinition filterDefinition = (FilterDefinition) this.getPredefinedFilterComboBox().getSelectedItem();
        TreeMap<Integer, String> parameterSet = new TreeMap<>();
        DefaultTableModel parameterTableModel = (DefaultTableModel) filterParameterTable.getModel();
        for (int i = 0; i < parameterTableModel.getRowCount(); i++)
            parameterSet.put(i, (String) parameterTableModel.getValueAt(i, 1));
        List<String> resultList = filterDefinition.getFilterFunction().applyFilter(parameterSet);
        DefaultTableModel resultTableModel = (DefaultTableModel) this.predefinedQueryResultTable.getModel();
        for (String result :
                resultList) {
            resultTableModel.addRow(new String[]{result});
        }
        this.predefinedQueryResultTable.setModel(resultTableModel);
    }

    /*
    Internal UI Methods
     */

    private void reasonOWL() {
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        OntModel activeOntModel = activeOntology.getOntology();
        OWLReasonerManager owlReasonerManager = new OWLReasonerManager(activeOntModel);
        List<JCheckboxWithObject> selectedTBoxList = this.getSelectedCheckboxesFromList(tboxList);
        for (JCheckboxWithObject tboxCheckbox :
                selectedTBoxList) {
            OntologyData ontologyData = (OntologyData) tboxCheckbox.getObject();
            owlReasonerManager.addTBox(ontologyData.getUrl());
        }
        owlReasonerManager.reasonWithJenaReasoner();
        OntModel mergedOntModel = owlReasonerManager.mergeOntologyWithInfModel();
        activeOntology.setOntology(mergedOntModel);
    }

    private ValidationReport validateSHACL() {
        SHACLReasonerManager shaclReasonerManager = this.setSHACLReasonerManager(shaclList);
        return shaclReasonerManager.validateWithValidationReport();
    }

    private Resource validateSHACLAsResource() {
        SHACLReasonerManager shaclReasonerManager = this.setSHACLReasonerManager(shaclList);
        return shaclReasonerManager.validateWithReportResource();
    }

    private SHACLReasonerManager setSHACLReasonerManager(JCheckBoxList checkBoxList) {
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        OntModel activeOntModel = activeOntology.getOntology();
        SHACLReasonerManager shaclReasonerManager = new SHACLReasonerManager(activeOntModel);
        List<JCheckboxWithObject> selectedSHACLList = this.getSelectedCheckboxesFromList(checkBoxList);
        for (JCheckboxWithObject shaclCheckbox :
                selectedSHACLList) {
            SHACLData shaclData = (SHACLData) shaclCheckbox.getObject();
            shaclReasonerManager.addSHACLRuleset(shaclData.getUrl());
        }
        return shaclReasonerManager;
    }

    private void reasonSHACL(JCheckBoxList checkBoxList) {
        ResourceConfiguration resourceConfiguration = ResourceConfiguration.getInstance();
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        OntModel activeOntModel = activeOntology.getOntology();
        SHACLReasonerManager shaclReasonerManager = new SHACLReasonerManager(activeOntModel);
        List<JCheckboxWithObject> selectedSHACLList = this.getSelectedCheckboxesFromList(checkBoxList);
        for (JCheckboxWithObject shaclCheckbox :
                selectedSHACLList) {
            SHACLRuleSet<String> shaclRuleSet = (SHACLRuleSet<String>) shaclCheckbox.getObject();
            Iterator<String> ruleSetIterator = shaclRuleSet.iterator();
            while (ruleSetIterator.hasNext()) {
                SHACLData shaclData = resourceConfiguration.getSHACLEntry(ruleSetIterator.next());
                shaclReasonerManager.addSHACLRuleset(shaclData.getUrl());
                shaclReasonerManager.reasonWithJenaReasoner();
                shaclReasonerManager.removeSHACLRuleset(shaclData.getUrl());
                activeOntModel = shaclReasonerManager.mergeOntologyWithInfModel();
            }
        }
        ActiveOntology.getInstance().setOntology(activeOntModel);
    }

    private ArrayList<JCheckboxWithObject> getSelectedCheckboxesFromList(JCheckBoxList checkBoxList) {
        ArrayList<JCheckboxWithObject> selectedCheckboxesList = new ArrayList<>();
        ListModel<JCheckboxWithObject> listModel = checkBoxList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            JCheckboxWithObject checkbox = listModel.getElementAt(i);
            if (checkbox.isSelected())
                selectedCheckboxesList.add(checkbox);
        }
        return selectedCheckboxesList;
    }

    //TODO:Dialogfeld für optionales Hinzufügen des Validation Reports zu ICDD hinzufügen
    private void addValidationReportToICDD(Resource validationReportFile) {
        try {
            ArrayList<String> linkedDocumentList = new ArrayList<>();
            linkedDocumentList.add(validationReportFileName);
            //TODO:Management-Feld für Nutzer zur Einstellung der File-Names
            linkedDocumentList.add("Construction.ttl");
            Container icdd = ActiveICDD.getInstance().getIcdd();
            Path tempfile = new File(Files.createTempDir(), validationReportFileName).toPath();
            RDFDataMgr.write(new FileOutputStream(tempfile.toFile()), validationReportFile.getModel(), RDFFormat.TTL);
            ICDDModifier.addResourceToICDD(tempfile, icdd);
            ICDDModifier.linkDocuments(linkedDocumentList, "validationLinkSet", "https://bimsage.com/validation#", icdd);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ComponentController.getInstance().updateICDDTree();
    }

    private ResultSet processQuery(String queryContent) {
        OntModel activeOntology = ActiveOntology.getInstance().getOntology();
        Query damageQuery = QueryFactory.create(queryContent);
        return QueryExecutionFactory.create(damageQuery, activeOntology).execSelect();
    }

    private TableModel addResultSetToTableModel(ResultSet resultSet) {
        List<String> resultVarList = resultSet.getResultVars();
        DefaultTableModel resultTableModel = new DefaultTableModel(null, resultVarList.toArray());
        resultSet.forEachRemaining(querySolution -> {
            ArrayList<String> resultRow = new ArrayList<>();
            for (String resultVariable :
                    resultVarList) {
                String result = querySolution.get(resultVariable).toString();
                resultRow.add(result);
            }
            resultTableModel.addRow(resultRow.toArray());
        });
        return resultTableModel;
    }

    private void setQueryParameterTable(FilterDefinition filterDefinition) {
        DefaultTableModel parameterTableModel = new DefaultTableModel(null, parameterColumn);
        LinkedHashSet<String> parameterList = filterDefinition.getParameterList();
        for (String parameter :
                parameterList) {
            parameterTableModel.addRow(new String[]{parameter, ""});
        }
        this.filterParameterTable.setModel(parameterTableModel);
    }

    private void setResultTable(FilterDefinition filterDefinition) {
        DefaultTableModel resultTableModel = new DefaultTableModel(null, new String[]{});
        ArrayList<String> resultTypeList = filterDefinition.getResultTypeList();
        for (String resultType :
                resultTypeList) {
            resultTableModel.addColumn(resultType);
        }
        this.predefinedQueryResultTable.setModel(resultTableModel);
    }

    /*
    Getter
     */

    public JButton getAddResource() {
        return addRessource;
    }

    public JButton getLinkDocument() {
        return linkDocument;
    }

    public JButton getLinkIFCEntity() {
        return linkIFCEntity;
    }

    public JTable getLinktable() {
        return linktable;
    }

    public JLabel getConstructionOntologyStatusLabel() {
        return constructionOntologyStatusLabel;
    }

    public JButton getCreateOntologyButton() {
        return createOntologyButton;
    }

    public JScrollPane getConstructionPane() {
        return constructionPane;
    }

    public JTable getConstructionInformationTable() {
        return constructionInformationTable;
    }

    public JButton getUpdateOntologyButton() {
        return updateOntologyButton;
    }

    public JComboBox getPredefinedAnnotationBox() {
        return predefinedAnnotationBox;
    }

    public GUIMenu getGuiMenu() {
        return guiMenu;
    }

    public JButton getCreateDamageButton() {
        return createDamageButton;
    }

    public JButton getAssignDamageButton() {
        return assignDamageButton;
    }

    public JButton getDeleteDamageButton() {
        return deleteDamageButton;
    }

    public JTable getDamageTable() {
        return damageTable;
    }

    public JComboBox getConfigurationComboBox() {
        return configurationComboBox;
    }

    public JCheckBoxList getTboxList() {
        return tboxList;
    }

    public JCheckBoxList getShaclList() {
        return shaclList;
    }

    public JTable getValidationResultTable() {
        return validationResultTable;
    }

    public JButton getReasonButton() {
        return reasonButton;
    }

    public JButton getValidateButton() {
        return validateButton;
    }

    public JComboBox getRenovationConfigurationComboBox() {
        return renovationConfigurationComboBox;
    }

    public JCheckBoxList getRenovationSHACLList() {
        return renovationSHACLList;
    }

    public JList getRenovationMeasureResultList() {
        return renovationMeasureResultList;
    }

    public JComboBox getPredefinedFilterComboBox() {
        return predefinedFilterComboBox;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(5, 4, new Insets(0, 10, 10, 10), -1, -1));
        mainPanel.setMinimumSize(new Dimension(1350, 594));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        navigationPane = new JTabbedPane();
        mainPanel.add(navigationPane, new GridConstraints(2, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(425, 700), new Dimension(425, 700), new Dimension(425, 700), 2, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(39, 8, new Insets(0, 0, 0, 0), -1, -1));
        navigationPane.addTab("ICDD", panel1);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 7, 39, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("ICDD Content");
        panel1.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        panel1.add(icddTree, new GridConstraints(4, 0, 15, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        addRessource = new JButton();
        addRessource.setEnabled(false);
        addRessource.setSelected(false);
        addRessource.setText("Add Ressource");
        panel1.add(addRessource, new GridConstraints(20, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        linkIFCEntity = new JButton();
        linkIFCEntity.setEnabled(false);
        linkIFCEntity.setText("Link IFC Entity");
        panel1.add(linkIFCEntity, new GridConstraints(22, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        linkDocument = new JButton();
        linkDocument.setEnabled(false);
        linkDocument.setText("Link Documents");
        panel1.add(linkDocument, new GridConstraints(22, 3, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        navigationPane.addTab("Annotator", panel2);
        radioButtonPredefinedAnnotation = new JRadioButton();
        radioButtonPredefinedAnnotation.setSelected(true);
        radioButtonPredefinedAnnotation.setText("Predefined Class Annotation");
        panel2.add(radioButtonPredefinedAnnotation, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(predefinedAnnotationBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(-1, 40), new Dimension(-1, 40), new Dimension(-1, 40), 1, false));
        radioButtonFreeAnnotation = new JRadioButton();
        radioButtonFreeAnnotation.setText("User-defined Class Annotation");
        panel2.add(radioButtonFreeAnnotation, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        applyAnnotationButton = new JButton();
        applyAnnotationButton.setText("Apply Annotation");
        panel2.add(applyAnnotationButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 1, false));
        final Spacer spacer5 = new Spacer();
        panel2.add(spacer5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 1, false));
        freeAnnotationField = new JTextField();
        freeAnnotationField.setEnabled(false);
        panel2.add(freeAnnotationField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        navigationPane.addTab("Anamnesis", panel3);
        anamnesisPane = new JTabbedPane();
        panel3.add(anamnesisPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        anamnesisPane.addTab("Construction Ontology", panel4);
        constructionOntologyStatusLabel = new JLabel();
        constructionOntologyStatusLabel.setText("No Construction Ontology has been generated");
        panel4.add(constructionOntologyStatusLabel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createOntologyButton = new JButton();
        createOntologyButton.setText("Create New Construction Ontology");
        panel4.add(createOntologyButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Construction Information");
        panel4.add(label2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateOntologyButton = new JButton();
        updateOntologyButton.setEnabled(false);
        updateOntologyButton.setText("Update Ontology");
        panel4.add(updateOntologyButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        constructionPane = new JScrollPane();
        constructionPane.setEnabled(false);
        panel4.add(constructionPane, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        constructionInformationTable = new JTable();
        constructionPane.setViewportView(constructionInformationTable);
        final Spacer spacer6 = new Spacer();
        panel4.add(spacer6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel4.add(spacer7, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JButton button1 = new JButton();
        button1.setEnabled(false);
        button1.setText("Load Existing Construction Ontology");
        panel4.add(button1, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        anamnesisPane.addTab("Damage Ontology", panel5);
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, -1, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Building Element");
        panel5.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel5.add(spacer8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Damage List");
        panel5.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel5.add(scrollPane1, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        damageTable = new JTable();
        scrollPane1.setViewportView(damageTable);
        createDamageButton = new JButton();
        createDamageButton.setEnabled(false);
        createDamageButton.setText("Create");
        panel5.add(createDamageButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        assignDamageButton = new JButton();
        assignDamageButton.setEnabled(false);
        assignDamageButton.setText("Assign");
        panel5.add(assignDamageButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteDamageButton = new JButton();
        deleteDamageButton.setEnabled(false);
        deleteDamageButton.setText("Delete");
        panel5.add(deleteDamageButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        navigationPane.addTab("Diagnosis", panel6);
        final JLabel label5 = new JLabel();
        label5.setText("TBox-Configuration");
        panel6.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel6.add(spacer9, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        configurationComboBox = new JComboBox();
        configurationComboBox.setEnabled(false);
        panel6.add(configurationComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setEnabled(true);
        panel6.add(panel7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "TBox-List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tboxList = new JCheckBoxList();
        tboxList.setEnabled(false);
        panel7.add(tboxList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.setEnabled(true);
        panel6.add(panel8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel8.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SHACL-Rules", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        shaclList = new JCheckBoxList();
        shaclList.setEnabled(false);
        panel8.add(shaclList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reasonButton = new JButton();
        reasonButton.setEnabled(false);
        reasonButton.setText("Reason");
        panel6.add(reasonButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        validateButton = new JButton();
        validateButton.setEnabled(false);
        validateButton.setText("Validate");
        panel6.add(validateButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel9, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Validation Report", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        validationResultPane = new JScrollPane();
        panel9.add(validationResultPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        validationResultTable = new JTable();
        validationResultPane.setViewportView(validationResultTable);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        navigationPane.addTab("Therapy", panel10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel11.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SHACL-Rules", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        renovationSHACLList = new JCheckBoxList();
        panel11.add(renovationSHACLList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel10.add(spacer10, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel12, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel12.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Recommended Renovation Measures", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel12.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        renovationMeasureResultList = new JList();
        scrollPane2.setViewportView(renovationMeasureResultList);
        reasonRenovationButton = new JButton();
        reasonRenovationButton.setText("Reason");
        panel10.add(reasonRenovationButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Renovation rules configuration");
        panel10.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        renovationConfigurationComboBox = new JComboBox();
        panel10.add(renovationConfigurationComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        navigationPane.addTab("Filter", panel13);
        tabbedPane1 = new JTabbedPane();
        panel13.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        tabbedPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Predefined Queries", panel14);
        final JLabel label7 = new JLabel();
        label7.setText("Query Selection");
        panel14.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        panel14.add(spacer11, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        predefinedFilterComboBox = new JComboBox();
        panel14.add(predefinedFilterComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel14.add(panel15, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel15.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Query Description", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        predefinedQueryDescription = new JLabel();
        predefinedQueryDescription.setText("Query Description Text");
        panel15.add(predefinedQueryDescription, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel15.add(scrollPane3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        filterParameterTable = new JTable();
        scrollPane3.setViewportView(filterParameterTable);
        predefinedQueryButton = new JButton();
        predefinedQueryButton.setText("Query");
        panel14.add(predefinedQueryButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel14.add(scrollPane4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Results", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        predefinedQueryResultTable = new JTable();
        scrollPane4.setViewportView(predefinedQueryResultTable);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("SPARQL Query", panel16);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel17.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SPARQL Query", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        sparqlQueryInputArea = new JTextArea();
        panel17.add(sparqlQueryInputArea, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final Spacer spacer12 = new Spacer();
        panel16.add(spacer12, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sparqlQueryButton = new JButton();
        sparqlQueryButton.setText("Query");
        panel16.add(sparqlQueryButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane5 = new JScrollPane();
        panel16.add(scrollPane5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Results", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        sparqlQueryResultTable = new JTable();
        scrollPane5.setViewportView(sparqlQueryResultTable);
        final JLabel label8 = new JLabel();
        label8.setText("IFC Model Viewer");
        mainPanel.add(label8, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 16), null, 0, false));
        final Spacer spacer13 = new Spacer();
        mainPanel.add(spacer13, new GridConstraints(4, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        ifcNavigation = new JTabbedPane();
        mainPanel.add(ifcNavigation, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel18.setMinimumSize(new Dimension(300, 500));
        panel18.setPreferredSize(new Dimension(300, 500));
        ifcNavigation.addTab("Spatial Structure", panel18);
        structureView = new IfcSpatialStructureView();
        panel18.add(structureView, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        ifcNavigation.addTab("IFC Types", panel19);
        typeView = new IfcTypeView();
        panel19.add(typeView, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        guiMenu = new GUIMenu();
        mainPanel.add(guiMenu, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(84, 22), null, 0, false));
        ifcEntityExplorer = new JTabbedPane();
        mainPanel.add(ifcEntityExplorer, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(725, 200), new Dimension(725, 200), null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        ifcEntityExplorer.addTab("IFC Attributes", panel20);
        panel20.add(modelExplorer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(725, 200), new Dimension(725, 200), new Dimension(725, 200), 1, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        ifcEntityExplorer.addTab("Links", panel21);
        linkScrollPane = new JScrollPane();
        panel21.add(linkScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 1, false));
        linktable = new JTable();
        linkScrollPane.setViewportView(linktable);
        mainPanel.add(modelViewer, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(425, 500), new Dimension(425, 500), new Dimension(425, 500), 1, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonPredefinedAnnotation);
        buttonGroup.add(radioButtonFreeAnnotation);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
