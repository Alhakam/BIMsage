package de.tud.cib.dotknow.gui.components.anamnesis.damage;

import de.tud.cib.dotknow.gui.utility.list.JListModelReader;
import de.tud.cib.dotknow.gui.utility.messages.MessageFactory;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import de.tud.dotknow.ontology.namespaces.tbox.DOT;
import de.tud.dotknow.ontology.representation.ActiveOntology;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class AggregateDamageDialog extends JDialog {

    private LinkedHashSet<ModelElement> damageSet;

    private static final int posX = 250;
    private static final int posY = 250;

    private JPanel contentPane;
    private JButton buttonAssignDamages;
    private JButton buttonCancel;
    private JList damageList;
    private ConfigureDamageDialog configureDamageDialog;
    private String damageName;

    public AggregateDamageDialog(String damageName) {
        this.damageName = damageName;
        this.setDamageList();

        getRootPane().setDefaultButton(buttonAssignDamages);

        buttonAssignDamages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAssignDamages();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

        /*
        Window-Visualization
         */
        this.panelVisualization();
    }

    public AggregateDamageDialog (ConfigureDamageDialog configureDamageDialog) {
        this("");
        this.configureDamageDialog = configureDamageDialog;
    }

    private void panelVisualization() {
        this.setContentPane(contentPane);
        this.setModal(true);
        this.pack();
        this.setLocation(posX, posY);
        this.setVisible(true);
    }

    //TODO: Bessere Lösung: Liste mit ausgewählten Schäden ausgeben und diese dann von dem entsprechenden Objekt abhandeln
    private void onAssignDamages() {
        if (configureDamageDialog != null)
            this.addDamagesToDamageConfigurationTable();
        else
            this.assignDamagesToDamageIndividual();
    }

    private void addDamagesToDamageConfigurationTable() {
        JList aggregatedDamageList = configureDamageDialog.getRelatedDamagesList();
        ListModel<String> aggregatedDamageListModel = aggregatedDamageList.getModel();
        LinkedHashSet<String> tableContent = JListModelReader.read(aggregatedDamageListModel);
        List<String> selectedDamageList = this.damageList.getSelectedValuesList();
        for (String selectedDamage :
                selectedDamageList) {
            tableContent.add(selectedDamage);
        }
        DefaultListModel<String> newListModel = new DefaultListModel<>();
        for (String tableElement :
                tableContent) {
            newListModel.addElement(tableElement);
        }
        aggregatedDamageList.setModel(newListModel);
        configureDamageDialog.setRelatedDamagesList(aggregatedDamageList);
        dispose();
    }

    private void assignDamagesToDamageIndividual() {
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        String ontologyURI = activeOntology.getOntologyURI();
        OntModel activeOntModel = activeOntology.getOntology();
        ObjectProperty aggregatesDamageElement = activeOntModel.createObjectProperty(DOT.AGGREGATES_DAMAGE_ELEMENT);
        Individual damageIndividual = activeOntModel.getIndividual(ontologyURI + this.damageName);
        List<String> selectedDamageList = this.damageList.getSelectedValuesList();
        for (String selectedDamage :
                selectedDamageList) {
            activeOntModel.add(damageIndividual, aggregatesDamageElement,
                    activeOntModel.getIndividual(ontologyURI + selectedDamage));
        }
        activeOntology.setOntology(activeOntModel);
        try {
            ActiveICDD.getInstance().saveOntologyinICDD(activeOntModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
        MessageFactory.showMessageWithCustomString(MessageFactory.MessageType.DAMAGE_ASSIGNED, damageName);
    }

    //TODO:Fehlerabhandlung überarbeiten
    private void setDamageList() {
        Vector<String> damageElementList = new Vector<>();
        OntModel ontology = ActiveOntology.getInstance().getOntology();
        //Error-Handling if no damage element exists
        AtomicBoolean damageElementExists = new AtomicBoolean(false);
        ontology.listClasses().forEachRemaining(ontClass -> {
            if (ontClass.getURI().equals(DOT.DAMAGE_ELEMENT))
                damageElementExists.set(true);
        });
        if (!damageElementExists.get())
            this.errorNoDamageElements();
        else {
            OntClass damageElementClass = ontology.getOntClass(DOT.DAMAGE_ELEMENT);
            ExtendedIterator<Individual> iterator = ontology.listIndividuals(damageElementClass);
            //Error-Handling if no damage element exists
            if (!iterator.hasNext()) {
                this.errorNoDamageElements();
            }
            else {
                iterator.forEachRemaining(damageElement -> {
                    damageElementList.add(damageElement.getLocalName());
                });
                this.damageList.setListData(damageElementList);
            }
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void errorNoDamageElements() {
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.ERROR_NO_DAMAGE_ELEMENTS);
        dispose();
    }

}
