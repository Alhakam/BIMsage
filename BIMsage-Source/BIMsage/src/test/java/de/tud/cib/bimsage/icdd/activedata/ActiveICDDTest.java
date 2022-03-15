package de.tud.cib.bimsage.icdd.activedata;

import icdd.beans.Container;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Vector;

import static org.junit.Assert.*;

public class ActiveICDDTest {

    ActiveICDD activeICDD;
    String containerName = "testContainer";
    String containerNS = "test.de";
    String ifcPathEFH = "DOTknow-icdd/src/test/resources/ifc/20141215_EFH.ifc";
    String ifcEFHName = "20141215_EFH.ifc";
    String ifcPathPromnitz = "DOTknow-icdd/src/test/resources/ifc/20191210_Promnitz_Building_IFC4.ifc";
    String ifcPromnitzName = "20191210_Promnitz_Building_IFC4.ifc";

    @Before
    public void init() {
        activeICDD = ActiveICDD.getInstance();
        Container icdd = new Container(containerName, containerNS);
        File ifcEFH = new File(ifcPathEFH);
        File ifcPromnitz = new File(ifcPathPromnitz);
        icdd.addInternalDocument(ifcEFH);
        icdd.addInternalDocument(ifcPromnitz);
        activeICDD.setIcdd(icdd);
    }

    @Test
    public void testListDocuments() {
        Vector<String> documentList = activeICDD.listDocuments();
        assertTrue(documentList.contains("20141215_EFH.ifc"));
        assertTrue(documentList.contains("20191210_Promnitz_Building_IFC4.ifc"));
    }


}
