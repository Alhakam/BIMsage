package de.tud.cib.dotknow.icdd.configuration;

import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import de.tud.cib.dotknow.icdd.utility.filter.ICDDFilter;
import icdd.beans.*;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Provides functions for modifying a given ICDD
 */
public class ICDDModifier {

    private ICDDModifier() {}

    /**
     * Adds a given Resource to a specific ICDD Container
     * @param resource The resource that will be added to the ICDD
     * @param icdd The ICDD Container that will be modified
     * @return The modified ICDD Container
     */
    public static Container addResourceToICDD(Path resource, Container icdd) {
        icdd.addInternalDocument(resource.toFile());
        return icdd;
    }

    /**
     * Searches for the a specific file in the ICDD and exchanges it with the given version.
     * @param resource The file that needs to be updated
     * @param icdd The ICDD Container that contains the file
     * @return The updated ICDD Container
     */
    public static Container updateResource(Path resource, Container icdd) {
        String resourceName = resource.getFileName().toString();
        icdd.getContainerDescription().listDocuments().forEachRemaining(document -> {
            if (document.getName().equals(resourceName))
                icdd.removeDocument(document);
        });
        icdd.addInternalDocument(resource.toFile());
        return icdd;
    }

    /**
     * Creates an ICDDLinkset and adds it to the given ICDD, which links a given set of documents
     * @param documentNames The file names of the documents that will be linked
     * @param linksetName The name of the created ICDDLinkset object
     * @param linksetNS The namespace of the ICDD Linkset
     * @param icdd The ICDD Container, in which the documents will be linked
     * @return The modified ICDD Container
     */
    public static Container linkDocuments(List<String> documentNames, String linksetName, String linksetNS, Container icdd) {
        LinkSet linkSet = icdd.addLinkSet(linksetName, linksetNS).getLinkSetOnt();
        for (String documentName :
                documentNames) {
            ICDDLink link = linkSet.createNullLink();
            ICDDDocument document = ICDDFilter.filterDocument(documentName, icdd);
            link.addLinkElement(document, null);
        }
        return icdd;
    }

    /**
     * Creates an ICDDLinkset and adds it to the given ICDD, which links a given set of documents to a given data element
     * @param documentNames The file names of the documents that will be linked
     * @param modelElement The element that will be linked with the documents
     * @param linksetName The name of the created ICDDLinkset object
     * @param linksetNS The namespace of the ICDD Linkset
     * @param icdd The ICDD Container, in which the documents will be linked
     * @return The modified ICDD Container
     */
    public static Container linkDocumentsToElement(List<String> documentNames, ModelElement modelElement, String linksetName,
                                                   String linksetNS, Container icdd) {
        LinkSet linkSet = createLinkSet(linksetName, linksetNS, icdd);
        ICDDLink link = linkSet.createNullLink();
        createLinkElement(modelElement, link, icdd);
        for (String documentName :
                documentNames) {
            link.addLinkElement(ICDDFilter.filterDocument(documentName, icdd), null);
        }
        return icdd;
    }

    /**
     * Creates an ICDDLinkset or updates an existing one and adds it to the given ICDD.
     * The linkset links a given set of data elements.
     * @param modelElementSet The set of elements that will be linked with each other
     * @param linksetName The name of the created ICDDLinkset object
     * @param linksetNS The namespace of the ICDD Linkset
     * @param icdd The ICDD Container, in which the documents will be linked
     * @return The modified ICDD Container
     */
    public static Container linkElements(LinkedHashSet<ModelElement> modelElementSet, String linksetName,
                                         String linksetNS, Container icdd) {
        LinkSet linkSet = createLinkSet(linksetName, linksetNS, icdd);
        ICDDLink link = linkSet.createNullLink();
        for (ModelElement modelElement :
                modelElementSet) {
            createLinkElement(modelElement, link, icdd);
        }
        return icdd;
    }

    private static LinkSet createLinkSet(String linksetName, String linksetNS, Container icdd) {
        Iterator<ICDDLinkSet> linkSetIterator = icdd.getContainerDescription().listLinkSet();
        while (linkSetIterator.hasNext()) {
            ICDDLinkSet linkSet = linkSetIterator.next();
            if (linkSet.getFileName().equals(linksetName))
                return linkSet.getLinkSetOnt();
        }
        return icdd.addLinkSet(linksetName, linksetNS).getLinkSetOnt();
    }

    private static void createLinkElement(ModelElement modelElement, ICDDLink link, Container icdd) {
        ICDDDocument model = ICDDFilter.filterDocument(modelElement.getDocumentName(), icdd);
        link.addStringLinkElement(model, modelElement.getElementID(), null, ICDDLink.LinkEleType.HAS);
    }
}
