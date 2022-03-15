package de.tud.cib.bimsage.icdd.configuration.input;


import java.io.Serializable;

/**
 * Representation of a model element that could be linked in an ICDD
 * Contains information about the related document and the element ID
 */
public class ModelElement implements Serializable {

    private String documentName;
    private String elementID;

    public ModelElement() {}

    //TODO: Nullable Annotation hinzufügen
    public ModelElement(String documentName, String elementID) {
        this.documentName = documentName;
        this.elementID = elementID;
    }

    /*
    Getter
     */

    public String getDocumentName() {
        return documentName;
    }

    public String getElementID() {
        return elementID;
    }

    /*
    Setter
     */

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setElementID(String elementID) {
        this.elementID = elementID;
    }
}
