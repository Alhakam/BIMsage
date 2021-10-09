package de.tud.cib.dotknow.icdd.configuration;

import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import icdd.beans.Container;
import icdd.beans.ICDDLinkElement;
import icdd.beans.ICDDStringBasedIdentifier;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;

public class ICDDModifierTest {

    String icddName = "testICDD";
    String icddNS = "https://dotknow.de";
    Path ifcFileEFH = Paths.get("src/test/resources/ifc/20141215_EFH.ifc");
    Path ifcFilePromnitz = Paths.get("src/test/resources/ifc/20191210_Promnitz_Building_IFC4.ifc");
    String ifcEFHName = "20141215_EFH.ifc";
    String ifcPromnitzName = "20191210_Promnitz_Building_IFC4.ifc";
    String linksetName = "TestLinkSet";
    String linksetNS = "testlinkset.de";
    String modelElementID = "testID";
    String linkedModelElementID = "linkedTestID";
    Container icdd;
    ModelElement testModelElement;
    ModelElement linkedModelElement;

    @Before
    public void initICDD() {
        icdd = new Container(icddName, icddNS);
        testModelElement = new ModelElement(ifcEFHName, modelElementID);
        linkedModelElement = new ModelElement(ifcEFHName, linkedModelElementID);
    }

    @Test
    public void testAddResource() {
        assertFalse(icdd.getContainerDescription().listInternalDocuments().hasNext());
        Container modifiedICDD = icdd;
        modifiedICDD = ICDDModifier.addResourceToICDD(ifcFileEFH, modifiedICDD);
        assertTrue(modifiedICDD.getContainerDescription().listInternalDocuments().hasNext());
    }

    @Test
    public void testLinkDocuments() {
        List<String> documentNames = new ArrayList<>();
        documentNames.add(ifcEFHName);
        documentNames.add(ifcPromnitzName);
        Container modifiedICDD = icdd;
        modifiedICDD = ICDDModifier.addResourceToICDD(ifcFileEFH, modifiedICDD);
        modifiedICDD = ICDDModifier.addResourceToICDD(ifcFilePromnitz, modifiedICDD);
        modifiedICDD = ICDDModifier.linkDocuments(documentNames, linksetName, linksetNS, modifiedICDD);
        assertTrue(modifiedICDD.getContainerDescription().listLinkSet().hasNext());
    }

    @Test
    public void testLinkDocumentsWithElement() {
        List<String> documentNames = new ArrayList<>();
        documentNames.add(ifcEFHName);
        Container modifiedICDD = icdd;
        modifiedICDD = ICDDModifier.addResourceToICDD(ifcFileEFH, modifiedICDD);
        ICDDModifier.linkDocumentsToElement(documentNames, testModelElement, linksetName, linksetNS, modifiedICDD);
        String linkedModelElementURI = null;
        Iterator<ICDDLinkElement> linkelementIterator = modifiedICDD.getContainerDescription().listLinkSet().next().
                getLinkSetOnt().listLink().next().listLinkElement();
        while (linkelementIterator.hasNext()) {
            ICDDLinkElement linkElement = linkelementIterator.next();
            if (linkElement.getIdentifier() != null) {
                ICDDStringBasedIdentifier linkElementID = (ICDDStringBasedIdentifier) linkElement.getIdentifier();
                linkedModelElementURI = linkElementID.getIdentifier();
            }
        }
        assertEquals(modelElementID, linkedModelElementURI);
    }

    @Test
    public void testLinkElements() {
        LinkedHashSet<ModelElement> modelElementList = new LinkedHashSet<>();
        modelElementList.add(testModelElement);
        modelElementList.add(linkedModelElement);
        Container modifiedICDD = icdd;
        ICDDModifier.linkElements(modelElementList, linksetName, linksetNS, modifiedICDD);
        String testModelElementURI = null;
        String linkedModelElementURI = null;
        Iterator<ICDDLinkElement> linkelementIterator = modifiedICDD.getContainerDescription().listLinkSet().next().
                getLinkSetOnt().listLink().next().listLinkElement();
        while (linkelementIterator.hasNext()) {
            ICDDLinkElement linkElement = linkelementIterator.next();
            ICDDStringBasedIdentifier linkElementID = (ICDDStringBasedIdentifier) linkElement.getIdentifier();
            if (linkElementID.getIdentifier().equals(modelElementID))
                testModelElementURI = linkElementID.getIdentifier();
            else
                linkedModelElementURI = linkElementID.getIdentifier();
        }
        assertEquals(modelElementID, testModelElementURI);
        assertEquals(linkedModelElementID, linkedModelElementURI);
    }

    @Test
    public void testUpdateDocument() {
        Container modifiedICDD = icdd;
        ICDDModifier.addResourceToICDD(ifcFileEFH, modifiedICDD);
        Path fileClone = ifcFileEFH;
        ICDDModifier.updateResource(fileClone, modifiedICDD);
        String updatedFileName = modifiedICDD.getContainerDescription().listDocuments().next().getName();
        assertEquals(fileClone.getFileName().toString(), updatedFileName);
    }

}
