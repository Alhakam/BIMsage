package de.tud.cib.dotknow.gui.components.anamnesis.damage;

import de.tud.cib.dotknow.gui.configuration.resources.ontology.OntologyData;

/**
 * Represents the damage that has been selected in the damage table of the platform
 */
public class SelectedDamage {

    private String damageURI;
    private OntologyData ontologyData;
    private String directDamageClassName;

    public SelectedDamage(String damageURI) {
        this.damageURI = damageURI;
    }

    public SelectedDamage(String damageURI, OntologyData ontologyData, String directDamageClassName) {
        this(damageURI);
        this.ontologyData = ontologyData;
        this.directDamageClassName = directDamageClassName;
    }

    /*
    Getter
     */

    public String getDamageURI() {
        return damageURI;
    }

    public OntologyData getOntologyData() {
        return ontologyData;
    }

    public String getDirectDamageClassName() {
        return directDamageClassName;
    }

    /*
    Setter
     */

    public void setDirectDamageClassName(String directDamageClassName) {
        this.directDamageClassName = directDamageClassName;
    }

    public void setOntologyData(OntologyData ontologyData) {
        this.ontologyData = ontologyData;
    }
}
