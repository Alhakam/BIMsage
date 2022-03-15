package de.tud.cib.bimsage.ontology.construction.conversion;

import de.tud.cib.bimsage.annotation.model.AnnotationData;
import de.tud.cib.bimsage.annotation.model.entry.ClassAnnotation;
import de.tud.cib.bimsage.ontology.construction.conversion.AnnotationConverter;
import de.tud.cib.bimsage.ontology.namespaces.OntologyNamespaces;
import de.tud.cib.bimsage.ontology.namespaces.tbox.BROT;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnnotationConverterTest {

    String ontologyInput = "src/test/resources/ontology/testOntology.ttl";
    String[] annotatedEntities = new String[]{"2NEFSxY8z81O8ZFrz7e7o$", "3I$lRVz9b7PgcnYDbz_sWt"};
    String aggregatedEntity = OntologyNamespaces.DEFAULT_URI + "2gHv_nB4X2afV2ErvNKnCp";
    String buildingUri = "00tMo7QcxqWdIGvc4sMN2A";
    String siteUri = "20FpTZCqJy2vhVJYtjuIce";
    AnnotationConverter annotationConverter;
    AnnotationData annotationData;

    @Before
    public void init() {
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.read(ontologyInput);
        annotationConverter = new AnnotationConverter(ontModel);
        annotationData = new AnnotationData();
        annotationData.addAnnotationEntry(
                new ClassAnnotation(annotatedEntities[0], "test", "BridgeComponent"));
        annotationData.addAnnotationEntry(
                new ClassAnnotation(annotatedEntities[1], "test", "Stone"));
    }

    @Test
    public void testConvertAnnotations() {
        OntModel resultOntology = annotationConverter.convertAnnotations(annotationData);
        String resultOntologyUri = resultOntology.listIndividuals().next().getNameSpace().replace("0", "");
        Individual bridgeComponent = resultOntology.getIndividual(resultOntologyUri + annotatedEntities[0]);
        Individual stone = resultOntology.getIndividual(resultOntologyUri + annotatedEntities[1]);
        assertEquals(OntologyNamespaces.BROT_URI + "Component", bridgeComponent.getOntClass().getURI());
        assertEquals(OntologyNamespaces.SCO_URI + "Stone", stone.getOntClass().getURI());
    }

    @Test
    public void testBridgeComponentConversion() {
        OntModel resultOntology = annotationConverter.convertAnnotations(annotationData);
        String resultOntologyUri = resultOntology.listIndividuals().next().getNameSpace().replace("0", "");
        Individual bridgeComponent = resultOntology.getIndividual(resultOntologyUri + annotatedEntities[0]);
        Individual bridge = resultOntology.getIndividual(resultOntologyUri + buildingUri);
        Individual site = resultOntology.getIndividual(resultOntologyUri + siteUri);
        ObjectProperty aggregates = resultOntology.getObjectProperty(BROT.AGGREGATES);
        assertTrue(bridgeComponent.hasProperty(aggregates));
        assertEquals(aggregatedEntity, bridgeComponent.getPropertyResourceValue(aggregates).getURI());
        assertEquals(BROT.BRIDGE, bridge.getOntClass().getURI());
        assertEquals(BROT.SITE, site.getOntClass().getURI());
    }

}
