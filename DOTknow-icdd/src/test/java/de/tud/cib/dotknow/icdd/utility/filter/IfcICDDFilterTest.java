package de.tud.cib.dotknow.icdd.utility.filter;

import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import icdd.beans.Container;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.Individual;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class IfcICDDFilterTest {

    private Container icdd;
    private ModelElement ifcElement;
    private final String icddPath = "src/test/resources/icdd/TestICDD.icdd";
    private final String ifcModelName = "20141215_EFH.ifc";
    private final String ifcElementId = "1ZJKF$F_f84ww7__q1FDPX";
    private final String ontologyName = "Construction.ttl";

    @Before
    public void init() {
        icdd = Container.readICDDContainer(new File(icddPath));
        ifcElement = new ModelElement(ifcModelName, ifcElementId);
    }

    @Test
    public void testFilterForOntRepresentation () {
        String ontRepresentation = IfcICDDFilter.filterForOntRepresentationURI(ifcElement, ontologyName, icdd);
        String localID = StringUtils.substringAfter(ontRepresentation, "#");
        assertEquals(localID, ifcElementId);
    }

}
