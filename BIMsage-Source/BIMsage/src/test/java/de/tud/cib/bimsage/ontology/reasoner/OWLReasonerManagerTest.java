package de.tud.cib.bimsage.ontology.reasoner;

import de.tud.cib.bimsage.ontology.namespaces.OntologyNamespaces;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class OWLReasonerManagerTest {

    private OWLReasonerManager owlReasonerManager;
    private OntModel abox;
    private String aboxPath = "src/test/resources/ontology/dotSampleData_v1.ttl";
    private String tboxURL = "https://alhakam.github.io/dot/ontology.ttl";
    private String infModelOutput = "src/test/resources/ontology/output/OWLOutput/infModelTest.ttl";
    private String mergedOntologyOutput = "src/test/resources/ontology/output/OWLOutput/renovationOntologyPhase1.ttl";
    private String aboxNamespace = "http://ex.org/alhak/data/D1#";

    @Before
    public void init() throws FileNotFoundException {
        abox = ModelFactory.createOntologyModel();
        abox.read(new FileInputStream(aboxPath), null, "TTL");
        owlReasonerManager = new OWLReasonerManager(abox);
        owlReasonerManager.addTBox(tboxURL);
    }

    @Test
    public void testReasoning() throws IOException {
        OntModel infModel = owlReasonerManager.reasonWithJenaReasoner();
        infModel.write(Files.newBufferedWriter(Paths.get(infModelOutput)), "TTL");
        assertNotNull(infModel.getResource(aboxNamespace+"damageElement1"));
    }

    //Superklassen scheinen nicht gefolgert zu werden -> Anderen Reasoner testen
    @Test
    public void testMerging() throws IOException {
        owlReasonerManager.reasonWithJenaReasoner();
        OntModel reasonedAbox = owlReasonerManager.mergeOntologyWithInfModel();
        reasonedAbox.write(Files.newBufferedWriter(Paths.get(mergedOntologyOutput)), "TTL");
        assertNotNull(reasonedAbox.getIndividual(aboxNamespace+"frameA-column1"));
    }

}
