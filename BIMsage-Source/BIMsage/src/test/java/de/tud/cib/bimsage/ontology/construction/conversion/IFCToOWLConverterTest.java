package de.tud.cib.bimsage.ontology.construction.conversion;

import de.tud.cib.bimsage.ontology.construction.conversion.IFCToOWLConverter;
import de.tud.cib.bimsage.ontology.namespaces.OntologyNamespaces;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class IFCToOWLConverterTest {

    String ifcPath = "src/test/resources/ontology/20200121_Promnitz_Stones.ifc";
    IFCToOWLConverter converter;
    Path ifcModel;
    String outputPath = "src/test/resources/ontology/output/output.ttl";

    @Before
    public void init() {
        ifcModel = Paths.get(ifcPath);
        converter = new IFCToOWLConverter();
    }

    @Test
    public void testConvert() throws IOException {
        OntModel resultOntology = converter.convert(ifcModel);
        assert resultOntology != null;
        assertFalse(resultOntology.isEmpty());
        assertNotNull(resultOntology.getIndividual(OntologyNamespaces.DEFAULT_URI + "00tMo7QcxqWdIGvc4sMN2A"));
        this.writeResults(resultOntology);
    }

    private void writeResults(OntModel model) throws FileNotFoundException {
        File file = new File(outputPath);
        OutputStream outputStream = new FileOutputStream(file);
        RDFDataMgr.write(outputStream, model, RDFFormat.TTL);
    }

}
