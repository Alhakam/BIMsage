package de.tud.cib.bimsage.gui.components.anamnesis.damage;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.tud.cib.bimsage.icdd.activedata.ActiveICDD;
import de.tud.cib.bimsage.icdd.configuration.input.ModelElement;
import de.tud.cib.bimsage.gui.utility.list.JListModelReader;
import de.tud.cib.bimsage.gui.utility.messages.MessageFactory;
import de.tud.cib.bimsage.ontology.namespaces.tbox.DOT;
import de.tud.cib.bimsage.ontology.representation.ActiveOntology;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

import javax.swing.*;
import java.awt.*;
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

    public AggregateDamageDialog(ConfigureDamageDialog configureDamageDialog) {
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
            } else {
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
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMaximumSize(new Dimension(600, 200));
        contentPane.setMinimumSize(new Dimension(600, 200));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonAssignDamages = new JButton();
        buttonAssignDamages.setText("Assign Damages");
        panel2.add(buttonAssignDamages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        damageList = new JList();
        panel3.add(damageList, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Select the Damages that should be aggregated");
        panel3.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
