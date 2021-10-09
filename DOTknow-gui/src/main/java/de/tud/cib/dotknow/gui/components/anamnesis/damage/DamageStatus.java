package de.tud.cib.dotknow.gui.components.anamnesis.damage;

import de.tud.cib.dotknow.gui.MainGUI;
import de.tud.dotknow.ontology.representation.ActiveOntology;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;

import javax.swing.table.DefaultTableModel;

/**
Holds information about all modelled damages and provides functions for querying them
 **/
public class DamageStatus {

    private MainGUI mainGUI;

    private static DamageStatus ourInstance = new DamageStatus();

    public static DamageStatus getInstance() {
        return ourInstance;
    }

    private DamageStatus() {
    }

    /**
     * Returns a DOT individual based on the table index of the DOTknow damage table
     * @param i the table index, which is used in the DOTknow damage table
     * @return individual that is referenced by the damage table entry of the respective index
     */
    public Individual getSelectedDamageIndividualBasedOnTableIndex(int i) {
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        String ontURI = activeOntology.getOntologyURI();
        OntModel ontModel = activeOntology.getOntology();
        DefaultTableModel damageTable = (DefaultTableModel) mainGUI.getDamageTable().getModel();
        String damageName = (String) damageTable.getValueAt(i, 0);
        return ontModel.getIndividual(ontURI + damageName);
    }

    /**
     * Returns the name of the damage which is referenced by the given index in the DOTknow damage table
     * @param i the table index, which is used in the DOTknow damage table
     * @return name of the damage that is referenced by the damage table entry of the respective index
     */
    public String getSelectedDamageNameBasedOnTableIndex(int i) {
        return (String) mainGUI.getDamageTable().getModel().getValueAt(i, 0);
    }

    /*
    Setter
     */

    public void setMainGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

}
