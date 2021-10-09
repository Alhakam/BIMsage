package de.tud.cib.dotknow.icdd.activedata.treemodel;

import icdd.beans.Container;
import icdd.beans.ICDDInternalDocument;
import icdd.beans.ICDDLinkSet;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Iterator;

/**
 * Provides methods for creating a Tree from an ICDD Container
 */
public class ICDDTreeCreator {

    private static final String EMPTY_ICDD_NAME = "No ICDD Selected";

    private ICDDTreeCreator() {}

    /**
     * Creates a DefaultMutableTreeNode from a given ICDD Container.
     * The DefaultMutableTreeNode represents all data models in a the standardized ICDD Container structure.
     * @param icdd The Container object, of which a DefaultMutableTreeNode should be created
     * @return the mapped DefaultMutableTreeNode object
     */
    public static DefaultMutableTreeNode createTreeModel(Container icdd) {
        DefaultMutableTreeNode icddTree = new DefaultMutableTreeNode(icdd.getName());
        icddTree.add(createOntResourceTree(icdd));
        icddTree.add(createPayloadDocTree(icdd));
        icddTree.add(createPayloadTripleTree(icdd));
        return icddTree;
    }

    /**
     * Creates a DefaultMutableTreeNode for an empty ICDD Container.
     * Therefore, only the standardized ICDD folder structure is returned
     * @return DefaultMutableTreeNode, which contains only the ICDD folder structure (empty folders)
     */
    public static DefaultMutableTreeNode createEmptyICDDTree() {
        Container emptyICDD = new Container(EMPTY_ICDD_NAME, null);
        return createTreeModel(emptyICDD);
    }

    //returns empty folder, since ICDD Framework does not support additional ontology resources
    private static DefaultMutableTreeNode createOntResourceTree(Container icdd) {
        DefaultMutableTreeNode ontResourceTree = new DefaultMutableTreeNode(ICDDStructure.RESOURCE_NAME);
        return ontResourceTree;
    }

    //TODO: Ordnerstrukturen werden derzeit nicht abgehandelt -> Problem von ICDD-Framework?
    //returns only internal documents
    private static DefaultMutableTreeNode createPayloadDocTree(Container icdd) {
        DefaultMutableTreeNode payloadDocTree = new DefaultMutableTreeNode(ICDDStructure.DOC_NAME);
        Iterator<ICDDInternalDocument> iterator = icdd.getContainerDescription().listInternalDocuments();
        while (iterator.hasNext()) {
            ICDDInternalDocument document = iterator.next();
            DefaultMutableTreeNode documentNode = new DefaultMutableTreeNode(document.getFileName());
            payloadDocTree.add(documentNode);
        }
        return payloadDocTree;
    }

    private static DefaultMutableTreeNode createPayloadTripleTree(Container icdd) {
        DefaultMutableTreeNode payloadTripleTree = new DefaultMutableTreeNode(ICDDStructure.TRIPLE_NAME);
        Iterator<ICDDLinkSet> iterator = icdd.getContainerDescription().listLinkSet();
        while (iterator.hasNext()) {
            ICDDLinkSet linkSet = iterator.next();
            DefaultMutableTreeNode linkSetNode = new DefaultMutableTreeNode(linkSet.getFileName());
            payloadTripleTree.add(linkSetNode);
        }
        return payloadTripleTree;
    }

}
