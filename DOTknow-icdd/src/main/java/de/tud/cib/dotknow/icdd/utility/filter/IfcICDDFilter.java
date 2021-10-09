package de.tud.cib.dotknow.icdd.utility.filter;

import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import icdd.beans.Container;
import org.apache.jena.ontology.Individual;

import java.util.List;

/*
Extends the ICCDFilter with specific functions for filtering linked datasets in the central IFC model
 */
public class IfcICDDFilter extends ICDDFilter{

    private IfcICDDFilter() {
        super();
    }

    /**
     * Filters a given ICDD for the ontological representation of a given IFC element
     * @param ifcElement The ModelElement object that holds information about the filtered IFC element
     * @param ontologyName The document name of the ontology representation of the IFC model
     * @param icdd The ICDD in which the documents are stored
     * @return The URI of the linked ontology representation / individual
     */
    public static String filterForOntRepresentationURI(ModelElement ifcElement, String ontologyName, Container icdd) {
        List<ModelElement> linkElementList = ICDDFilter.filterLinkedElements(ifcElement, icdd);
        for (ModelElement linkElement :
                linkElementList) {
            if (linkElement.getDocumentName().equals(ontologyName))
                return linkElement.getElementID();
        }
        return null;
    }
}
