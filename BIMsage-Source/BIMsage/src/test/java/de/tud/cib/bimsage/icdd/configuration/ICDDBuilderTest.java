package de.tud.cib.bimsage.icdd.configuration;

import icdd.beans.Container;
import icdd.beans.ICDDContainerDescription;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ICDDBuilderTest {

    String ifcPathEFH = "DOTknow-icdd/src/test/resources/ifc/20141215_EFH.ifc";
    ICDDBuilder icddBuilder;

    @Before
    public void init() {
        icddBuilder = new ICDDBuilder(null, Paths.get(ifcPathEFH));
    }

    @Test
    public void testBuild() throws IOException {
       Container icdd = icddBuilder.build();
       ICDDContainerDescription containerDescription = icdd.getContainerDescription();
       assertTrue(containerDescription.listInternalDocuments().hasNext());
    }

}
