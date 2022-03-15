package de.tud.cib.bimsage.ontology.construction.conversion;

import de.tud.cib.bimsage.annotation.model.AnnotationData;
import de.tud.cib.bimsage.annotation.model.entry.AnnotationEntry;
import de.tud.cib.bimsage.annotation.model.entry.ClassAnnotation;
import de.tud.cib.bimsage.ontology.namespaces.OntologyNamespaces;
import de.tud.cib.bimsage.ontology.namespaces.tbox.BOT;
import de.tud.cib.bimsage.ontology.namespaces.tbox.BROT;
import de.tud.cib.bimsage.ontology.namespaces.tbox.SCO;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Applies annotations on a generated construction ontology
 */
public class AnnotationConverter {

    private OntModel ontModel;

    public AnnotationConverter(OntModel ontModel) {
        this.ontModel = ontModel;
    }

    /**
     * Converts given annotation data and applies them to the ontology of the annotation.
     * @param annotationData The annotation data that will be converted
     * @return The OWL ontology represented as OntModel, which holds the integrated annotation data
     */
    public OntModel convertAnnotations(AnnotationData annotationData) {
        HashMap<String, AnnotationEntry> annotationMap = annotationData.getAnnotationEntries();
        for (int i = 0; i<annotationMap.size(); i++) {
            AnnotationEntry annotationEntry = annotationMap.get(Integer.toString(i));
            if (annotationEntry.getClass() == ClassAnnotation.class)
                this.convertClassAnnotation((ClassAnnotation) annotationEntry);
        }
        return ontModel;
    }

    /*
    Internal Methods
     */

    //TODO: Predefined Annotations in separater Klasse strukturieren
    //TODO: Verlinkungen erstellen
    private void convertClassAnnotation(ClassAnnotation annotation) {
        String className = annotation.getClassName();
        //Assumption that every individual in the ontology has the same baseURI
        String ontologyURI = ontModel.listIndividuals().next().getNameSpace().replace("0", "");
        Individual individual = ontModel.getIndividual(ontologyURI + annotation.getAnnotatedEntityGUID());
        individual.removeOntClass(ontModel.getOntClass(BOT.ELEMENT));
        if (className.equals("BridgeComponent")) {
            individual.setOntClass(ontModel.createClass(BROT.COMPONENT));
            this.convertProperties(individual, BROT.AGGREGATES, BOT.HAS_SUB_ELEMENT);
            this.convertBuildingToBridge();
            this.convertSiteToBridgeSite();
        }
        else if (className.equals("Stone")) {
            individual.setOntClass(ontModel.createClass(SCO.STONE));
            this.convertProperties(individual, SCO.AGGREGATES_STONE, BOT.HAS_SUB_ELEMENT);
        }
        else
            individual.setOntClass(ontModel.createClass(OntologyNamespaces.ANNOTATION_URI + className));
    }

    //TODO:Überlegen ob Conversion Methode in Utility-Klasse ausgelagert werden soll
    private void convertProperties(Individual bridgeComponent, String newPropertyUri, String exchangedPropertyUri) {
        ObjectProperty newProperty = ontModel.createObjectProperty(newPropertyUri);
        ObjectProperty exchangedProperty = ontModel.createObjectProperty(exchangedPropertyUri);
        if (bridgeComponent.hasProperty(exchangedProperty)) {
            ArrayList<Statement> removableStatements = new ArrayList<>();
            ArrayList<Statement> newStatements = new ArrayList<>();
            bridgeComponent.listProperties(exchangedProperty).forEachRemaining(statement -> {
                RDFNode object = statement.getObject();
                newStatements.add(new StatementImpl(bridgeComponent, newProperty, object));
            });
            ontModel.add(newStatements);
            ontModel.remove(removableStatements);
        }
    }

    private void convertBuildingToBridge() {
        ExtendedIterator<Individual> buildingIterator = ontModel.listIndividuals(ontModel.createClass(BOT.BUILDING));
        //Assumption that max 1 building or site individual is existent (constraint from IFCToOWLConverter)
        if (buildingIterator.hasNext()) {
            Individual building = buildingIterator.next();
            building.setOntClass(ontModel.createClass(BROT.BRIDGE));
            this.convertProperties(building, BROT.CONTAINS_COMPONENT, BOT.HAS_ELEMENT);
        }
    }

    private void convertSiteToBridgeSite() {
        ExtendedIterator<Individual> siteIterator = ontModel.listIndividuals(ontModel.createClass(BOT.SITE));
        //Assumption that max 1 building or site individual is existent (constraint from IFCToOWLConverter)
        if (siteIterator.hasNext()) {
            Individual site = siteIterator.next();
            site.setOntClass(ontModel.createClass(BROT.SITE));
            this.convertProperties(site, BROT.CONTAINS_ZONE, BOT.CONTAINS_ZONE);
        }
    }
}
