package de.tud.cib.bimsage.icdd.utility.filter;

import de.tud.cib.bimsage.icdd.configuration.input.ModelElement;
import icdd.beans.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class that provides functions for filtering data from a given ICDD
 */
public class ICDDFilter {

    protected ICDDFilter() {}

    /**
     * Filters an ICDD for a specific ICDDDocument, based on its name parameter
     * @param documentName The name of the ICDDDocument
     * @param icdd The ICDD Container that needs to be filtered
     * @return The filtered ICDDDocument object
     */
    public static ICDDDocument filterDocument(String documentName, Container icdd) {
        Iterator<ICDDDocument> documentIterator = icdd.getContainerDescription().listDocuments();
        while (documentIterator.hasNext()) {
            ICDDDocument document = documentIterator.next();
            if (document.getName().equals(documentName))
                return document;
        }
        return null;
    }

    /**
     * Filters an ICDD for all linked elements of a specific model element
     * @param modelElement The model element of which linked elements in the ICDD should be filtered
     * @param icdd The ICDD Container that needs to be filtered
     * @return The filtered elements stored in an ArrayList<ModelElement>
     */
    public static ArrayList<ModelElement> filterLinkedElements(ModelElement modelElement, Container icdd) {
        ArrayList<ModelElement> modelElementList = new ArrayList<>();
        Iterator<ICDDLinkSet> linkSetIterator = icdd.getContainerDescription().listLinkSet();
        linkSetIterator.forEachRemaining(linkSet -> {
            Iterator<ICDDLink> linkIterator = linkSet.getLinkSetOnt().listLink();
            linkIterator.forEachRemaining(link -> {
                ArrayList<ModelElement> linkedElements = checkSingleLink(modelElement, link);
                modelElementList.addAll(linkedElements);
            });
        });
        return modelElementList;
    }

    private static ArrayList<ModelElement> checkSingleLink(ModelElement modelElement, ICDDLink link) {
        AtomicBoolean linkContainsElement = new AtomicBoolean(false);
        ArrayList<ModelElement> temporaryModelElementList = new ArrayList<>();
        Iterator<ICDDLinkElement> linkElementIterator = link.listLinkElement();
        linkElementIterator.forEachRemaining(linkElement -> {
            ModelElement transformedLinkElement = transformLinkElement(linkElement);
            if (transformedLinkElement.getDocumentName().equals(modelElement.getDocumentName())
                    && transformedLinkElement.getElementID().equals(modelElement.getElementID()))
                linkContainsElement.set(true);
            else
                temporaryModelElementList.add(transformedLinkElement);
        });
        if (linkContainsElement.get() == true)
            return temporaryModelElementList;
        else return new ArrayList<>();
    }

    private static ModelElement transformLinkElement(ICDDLinkElement linkElement) {
        String documentName = linkElement.getDocument().getName();
        String modelIdentifier = null;
        ICDDIdentifier icddIdentifier = linkElement.getIdentifier();
        if (icddIdentifier != null) {
            if (icddIdentifier.getClass() == ICDDStringBasedIdentifier.class)
                modelIdentifier = ((ICDDStringBasedIdentifier) icddIdentifier).getIdentifier();
            else if (icddIdentifier.getClass() == ICDDURLBasedIdentifier.class)
                modelIdentifier = ((ICDDURLBasedIdentifier) icddIdentifier).getUrl();
        }
        return new ModelElement(documentName, modelIdentifier);
    }

}
