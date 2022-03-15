package de.tud.cib.bimsage.gui.components.anamnesis.properties;

/**
 * Representation of a predefined property that are stored in the property database of the software platform.
 * Each property contains:
 *  - a label that is presented in the GUI
 *  - the URI of the related property
 *  - a value that can be stored for later integration in an ontology
 */
public class OntologyProperty {

    private String label, uri, value;

    public OntologyProperty() {}

    public OntologyProperty(String label, String uri, String value) {
        this.label = label;
        this.uri = uri;
        this.value = value;
    }

    /*
    Setter
     */

    public void setLabel(String label) {
        this.label = label;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /*
    Getter
     */

    public String getLabel() {
        return label;
    }

    public String getUri() {
        return uri;
    }

    public String getValue() {
        return value;
    }
}
