package de.tud.cib.bimsage.icdd.activedata.treemodel;

import icdd.beans.Container;
import org.junit.Before;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

import static org.junit.Assert.*;

public class ICDDTreeCreatorTest {

    final String resourceName = ICDDStructure.RESOURCE_NAME;
    final String docName = ICDDStructure.DOC_NAME;
    final String tripleName = ICDDStructure.TRIPLE_NAME;

    final String ifcName = "Vogelsangbruecke_Bauwerk_D_West.ifc";
    final String docElementName = "brot.ttl";
    final String tripleElementName = "IfcToOWL";

    Container icdd;
    String icddPath = "src/test/resources/icdd/icddTreeTest.icdd";

    @Before
    public void init() {
        icdd = Container.readICDDContainer(new File(icddPath));
    }

    //TODO: Test umschreiben, sodass er immer funktioniert
    /*@Test
    public void testCreateTreeModel() {
        DefaultMutableTreeNode tree = ICDDTreeCreator.createTreeModel(icdd);
        DefaultMutableTreeNode icddResource = tree.getFirstLeaf();
        DefaultMutableTreeNode icddDoc = icddResource.getNextSibling();
        DefaultMutableTreeNode icddTriple = icddDoc.getNextSibling();
        assertEquals(icdd.getName(), tree.getUserObject());
        assertEquals(resourceName, icddResource.getUserObject());
        assertEquals(docName, icddDoc.getUserObject());
        assertEquals(tripleName, icddTriple.getUserObject());
        assertEquals(ifcName, icddDoc.getFirstLeaf().getUserObject());
        assertEquals(docElementName, icddDoc.getLastLeaf().getUserObject());
        assertEquals(tripleElementName, icddTriple.getFirstLeaf().getUserObject());
    } */

    @Test
    public void testCreateEmptyICDDTree() {
        DefaultMutableTreeNode tree = ICDDTreeCreator.createEmptyICDDTree();
        DefaultMutableTreeNode icddResource = tree.getFirstLeaf();
        DefaultMutableTreeNode icddDoc = icddResource.getNextSibling();
        DefaultMutableTreeNode icddTriple = icddDoc.getNextSibling();
        assertEquals(resourceName, icddResource.getUserObject());
        assertEquals(docName, icddDoc.getUserObject());
        assertEquals(tripleName, icddTriple.getUserObject());
    }

}
