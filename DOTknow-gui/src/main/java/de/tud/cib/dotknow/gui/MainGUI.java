package de.tud.cib.dotknow.gui;

import com.apstex.gui.core.views.modelexplorer.ModelExplorer;
import com.apstex.gui.ifc.views.spatialview.IfcSpatialStructureView;
import com.apstex.gui.ifc.views.typeview.IfcTypeView;
import com.apstex.gui.ifc.views.view3d.j3d.ModelViewer;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.common.io.Files;
import de.tud.cib.dotknow.gui.components.ComponentController;
import de.tud.cib.dotknow.gui.components.anamnesis.construction.ConstructionOntologyDialog;
import de.tud.cib.dotknow.gui.components.anamnesis.damage.AggregateDamageDialog;
import de.tud.cib.dotknow.gui.components.anamnesis.damage.ConfigureDamageDialog;
import de.tud.cib.dotknow.gui.components.anamnesis.damage.DamageStatus;
import de.tud.cib.dotknow.gui.components.anamnesis.properties.OntologyProperty;
import de.tud.cib.dotknow.gui.components.anamnesis.properties.PropertyDatabase;
import de.tud.cib.dotknow.gui.components.filter.FilterDefinition;
import de.tud.cib.dotknow.gui.components.filter.preconfig.FilterPreConfig;
import de.tud.cib.dotknow.gui.components.icdd.DocumentSelectionDialog;
import de.tud.cib.dotknow.gui.components.icdd.ICDDFileManager;
import de.tud.cib.dotknow.gui.components.ifc.IfcStatus;
import de.tud.cib.dotknow.gui.components.menu.GUIMenu;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.PreConfigConfiguration;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.SHACLRuleSet;
import de.tud.cib.dotknow.gui.configuration.resources.ResourceConfiguration;
import de.tud.cib.dotknow.gui.configuration.resources.ontology.OntologyData;
import de.tud.cib.dotknow.gui.configuration.resources.ontology.SHACLData;
import de.tud.cib.dotknow.gui.controller.annotation.AnnotationController;
import de.tud.cib.dotknow.gui.utility.FileChooserUtility;
import de.tud.cib.dotknow.gui.utility.list.checkboxlist.JCheckBoxList;
import de.tud.cib.dotknow.gui.utility.list.checkboxlist.JCheckboxWithObject;
import de.tud.cib.dotknow.gui.utility.messages.MessageFactory;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.activedata.treemodel.ICDDTreeCreator;
import de.tud.cib.dotknow.icdd.configuration.ICDDModifier;
import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import de.tud.dotknow.ontology.namespaces.tbox.BOT;
import de.tud.dotknow.ontology.reasoner.OWLReasonerManager;
import de.tud.dotknow.ontology.reasoner.SHACLReasonerManager;
import de.tud.dotknow.ontology.representation.ActiveOntology;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
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

    private static final String appTitle = "Damage Assessment System";
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
                if(e.getStateChange()==ItemEvent.SELECTED)
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
            System.err.println( "Failed to initialize LaF-GUI" );
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
        if(predefinedAnnotationBox.isEnabled())
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
        for (int i=0; i<tableModel.getRowCount(); i++) {
            String value = (String) tableModel.getValueAt(i,1);
            String propertyLabel = (String) tableModel.getValueAt(i,0);
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
        }
        else
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
        }
        else
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
        TreeMap<Integer,String> parameterSet = new TreeMap<>();
        DefaultTableModel parameterTableModel = (DefaultTableModel) filterParameterTable.getModel();
        for (int i=0; i<parameterTableModel.getRowCount(); i++)
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
        for(int i=0; i<listModel.getSize(); i++) {
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
            ICDDModifier.linkDocuments(linkedDocumentList, "validationLinkSet", "https://dotknow.com/validation#", icdd);
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
}
