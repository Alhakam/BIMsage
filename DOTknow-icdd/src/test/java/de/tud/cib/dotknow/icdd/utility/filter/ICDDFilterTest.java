package de.tud.cib.dotknow.icdd.utility.filter;

import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import icdd.beans.*;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ICDDFilterTest {

    String icddName = "testICDD";
    String icddNS = "https://dotknow.de";
    Path ifcFileEFH = Paths.get("src/test/resources/ifc/20141215_EFH.ifc");
    String ifcEFHName = "20141215_EFH.ifc";
    String linksetName = "TestLinkSet";
    String linksetNS = "testlinkset.de";
    String modelElementID = "testID";
    String linkedElementID = "testLinkID";
    Container icdd;

    @Before
    public void initICDD() {
        icdd = new Container(icddName, icddNS);
        ICDDInternalDocument icddInternalDocument = icdd.addInternalDocument(ifcFileEFH.toFile());
        LinkSet linkSet = icdd.addLinkSet(linksetName, linksetNS).getLinkSetOnt();
        ICDDLink link = linkSet.createNullLink();
        link.addStringLinkElement(icddInternalDocument, modelElementID, null, ICDDLink.LinkEleType.HAS);
        link.addStringLinkElement(icddInternalDocument, linkedElementID, null, ICDDLink.LinkEleType.HAS);
    }

    @Test
    public void testFilterDocument() {
        ICDDDocument icddDocument = ICDDFilter.filterDocument(ifcEFHName, icdd);
        assertEquals(ifcEFHName, icddDocument.getName());
    }

    @Test
    public void testFilterLinkedElements() {
        ArrayList<ModelElement> modelElements = ICDDFilter.filterLinkedElements
                (new ModelElement(ifcEFHName, modelElementID), icdd);
        ModelElement linkedElement = modelElements.get(0);
        assertEquals(ifcEFHName, linkedElement.getDocumentName());
        assertEquals(linkedElementID, linkedElement.getElementID());

    }

}
