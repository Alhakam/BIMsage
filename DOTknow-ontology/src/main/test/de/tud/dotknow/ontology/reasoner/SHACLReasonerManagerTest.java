package de.tud.dotknow.ontology.reasoner;

import de.tud.dotknow.ontology.namespaces.OntologyNamespaces;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Before;
import org.junit.Test;
import org.topbraid.shacl.validation.ValidationReport;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SHACLReasonerManagerTest {

    private SHACLReasonerManager simpleShaclReasonerManager;
    private SHACLReasonerManager bimsisShaclReasonerManager;
    private SHACLReasonerManager renovationShaclReasonerManager;
    private OntModel simpleAbox;
    private String simpleAboxPath = "src/main/test/resources/nsdSampleData.ttl";
    private OntModel bimsisAbox;
    private String bimsisAboxPath = "src/main/test/resources/bimsisSampleData.ttl";
    private OntModel renovationAbox;
    private String renovationAboxPath = "src/main/test/resources/renovationOntologyPhase1.ttl";
    private String shaclReasoningURL = "src/main/test/resources/SHACL/unassignedProperties.ttl";
    private String shaclValidationURL = "src/main/test/resources/SHACL/damageValidation.ttl";
    private String shaclRenovationURL = "src/main/test/resources/SHACL/renovationMeasures.ttl";
    private String shaclRenovationClassification = "src/main/test/resources/SHACL/renovationMeasuresClassification.ttl";
    private String infModelOutput = "src/main/test/resources/output/SHACLOutput/infModelTest.ttl";
    private String mergedOntologyOutput = "src/main/test/resources/output/SHACLOutput/renovationOntologyPhase1.ttl";
    private String aboxNamespace = "http://ex.org/alhak/data/D2#";
    private File reportFile;
    private String validationReportPath = "src/main/test/resources/output/validationReport.ttl";

    @Before
    public void init() throws FileNotFoundException {
        //Reasoning Abox
        simpleAbox = ModelFactory.createOntologyModel();
        simpleAbox.read(new FileInputStream(simpleAboxPath), null, "TTL");
        simpleShaclReasonerManager = new SHACLReasonerManager(simpleAbox);
        //Validation Abox
        bimsisAbox = ModelFactory.createOntologyModel();
        bimsisAbox.read(new FileInputStream(bimsisAboxPath), null, "TTL");
        bimsisShaclReasonerManager = new SHACLReasonerManager(bimsisAbox);
        reportFile = new File(validationReportPath);
        //Renovation Abox
        renovationAbox = ModelFactory.createOntologyModel();
        renovationAbox.read(new FileInputStream(renovationAboxPath), null, "TTL");
        renovationShaclReasonerManager = new SHACLReasonerManager(renovationAbox);
    }

    @Test
    public void testReasoning() throws IOException {
        simpleShaclReasonerManager.addSHACLRuleset(shaclReasoningURL);
        Model infModel = simpleShaclReasonerManager.reasonWithJenaReasoner();
        simpleShaclReasonerManager.removeSHACLRuleset(shaclReasoningURL);
        infModel.write(Files.newBufferedWriter(Paths.get(infModelOutput)), "TTL");
        assertNotNull(infModel.getResource(aboxNamespace+"nsdDamage1"));
    }

    @Test
    public void testMerging() throws IOException {
        simpleShaclReasonerManager.addSHACLRuleset(shaclReasoningURL);
        simpleShaclReasonerManager.reasonWithJenaReasoner();
        simpleShaclReasonerManager.removeSHACLRuleset(shaclReasoningURL);
        OntModel reasonedAbox = simpleShaclReasonerManager.mergeOntologyWithInfModel();
        reasonedAbox.write(Files.newBufferedWriter(Paths.get(mergedOntologyOutput)), "TTL");
        assertNotNull(reasonedAbox.getProperty(OntologyNamespaces.NSD_URI + "crackDepth"));
    }

    @Test
    public void testValidation() throws IOException {
        bimsisShaclReasonerManager.addSHACLRuleset(shaclValidationURL);
        Resource validationReport = bimsisShaclReasonerManager.validateWithReportResource();
        bimsisShaclReasonerManager.removeSHACLRuleset(shaclValidationURL);
        assertNotNull(validationReport);
        reportFile.createNewFile();
        OutputStream reportOutputStream = new FileOutputStream(reportFile);
        RDFDataMgr.write(reportOutputStream, validationReport.getModel(), RDFFormat.TTL);
        assertTrue(reportFile.exists());
    }

    @Test
    public void testValidationWithReport() {
        bimsisShaclReasonerManager.addSHACLRuleset(shaclValidationURL);
        ValidationReport validationReport = bimsisShaclReasonerManager.validateWithValidationReport();
        bimsisShaclReasonerManager.removeSHACLRuleset(shaclValidationURL);
        assertNotNull(validationReport);
    }

    /*
    SHACL-Tests
     */

    @Test
    public void testRenovationReasoning() throws IOException {
        simpleShaclReasonerManager.addSHACLRuleset(shaclRenovationURL);
        Model infModel = simpleShaclReasonerManager.reasonWithJenaReasoner();
        simpleShaclReasonerManager.removeSHACLRuleset(shaclRenovationURL);
        infModel.write(Files.newBufferedWriter(Paths.get(infModelOutput)), "TTL");
        assertNotNull(infModel.getResource(aboxNamespace+"nsdDamage1"));
    }

    @Test
    public void testRenovationClassification() throws IOException {
        renovationShaclReasonerManager.addSHACLRuleset(shaclRenovationClassification);
        Model infModel = renovationShaclReasonerManager.reasonWithJenaReasoner();
        renovationShaclReasonerManager.removeSHACLRuleset(shaclRenovationClassification);
        infModel.write(Files.newBufferedWriter(Paths.get(infModelOutput)), "TTL");
        assertNotNull(infModel.getResource(aboxNamespace+"nsdDamage1"));
    }


}
