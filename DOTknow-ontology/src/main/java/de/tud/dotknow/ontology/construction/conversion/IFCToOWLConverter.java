package de.tud.dotknow.ontology.construction.conversion;

import de.tud.cib.dotknow.ifc.TopologicalEntity;
import de.tud.cib.dotknow.ifc.filter.TopologicalEntityFilter;
import de.tud.dotknow.ontology.namespaces.OntologyNamespaces;
import de.tud.dotknow.ontology.namespaces.tbox.BOT;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Generates an OWL ontology from a given IFC model
 * Utilizes the IFCToLBD Converter: https://github.com/jyrkioraskari/IFCtoLBD for generating an initial ontology
 * LBD ontology is modified based on given annotations
 */
public class IFCToOWLConverter {

    private String ontologyURI;
    private String ontologyNS;

    private OntModel ontModel;

    public IFCToOWLConverter(String ontologyURI, String ontologyNS) {
        this.ontologyURI = ontologyURI;
        this.ontologyNS = ontologyNS;
        ontModel = new OntModelImpl(OntModelSpec.OWL_DL_MEM);
        ontModel.createOntology(ontologyURI);
        this.setNamespaces(ontModel);
    }

    public IFCToOWLConverter() {
        this(OntologyNamespaces.DEFAULT_URI, OntologyNamespaces.DEFAULT_NS);
    }

    /**
     * Converts a given IFC Model to an OWL ontology and integrates it in the current Converter ontology
     * @param ifc The IFC file which should be converted to an ontology
     * @return The Ontmodel representing the converted OWL ontology
     * @throws IOException
     */
    public OntModel convert(Path ifc) throws IOException {
        TopologicalEntityFilter filter = new TopologicalEntityFilter(ifc);
        ArrayList<TopologicalEntity> entityList = filter.filterEntities();
        ArrayList<String> buildingGUID = filter.filterBuildingGUIDs();
        ArrayList<String> siteGUID = filter.filterSiteGUIDs();
        this.createIndividuals(entityList);
        if (buildingGUID.size() == 1)
            this.createBuilding(buildingGUID.get(0), entityList);
        if (siteGUID.size() == 1)
            this.createSite(siteGUID.get(0), buildingGUID.get(0));
        return ontModel;
    }

    /**
     * Removes all statements in the current converter ontology and resets it.
     */
    public void resetOntology() {
        ontModel.removeAll();
    }

    /*
    Getter
     */

    public OntModel getOntModel() {
        return ontModel;
    }

    /*
    Internal Methods
     */

    private void setNamespaces(OntModel ontModel) {
        ontModel.setNsPrefix(ontologyNS, ontologyURI);
        ontModel.setNsPrefix(OntologyNamespaces.BOT_NS, OntologyNamespaces.BOT_URI);
    }

    private void createIndividuals(ArrayList<TopologicalEntity> entityList) {
        for (TopologicalEntity entity :
                entityList) {
            Individual element = ontModel.createIndividual(ontologyURI + entity.getIfcID(),
                    ontModel.createClass(BOT.ELEMENT));
        }
        this.assignAggregations(entityList);
    }

    private void createBuilding(String buildingGUID, ArrayList<TopologicalEntity> entityList) {
        Individual building = ontModel.createIndividual(ontologyURI + buildingGUID,
                ontModel.createClass(BOT.BUILDING));
        ObjectProperty hasElement = ontModel.createObjectProperty(BOT.HAS_ELEMENT);
        for (TopologicalEntity entity :
                entityList) {
            Individual element = ontModel.getIndividual(ontologyURI + entity.getIfcID());
            ontModel.add(building, hasElement, element);
        }
    }

    private void createSite(String siteGUID, String buildingGUID) {
        Individual site = ontModel.createIndividual(ontologyURI + siteGUID,
                ontModel.createClass(BOT.SITE));
        ObjectProperty containsZone = ontModel.createObjectProperty(BOT.CONTAINS_ZONE);
        Individual building = ontModel.getIndividual(ontologyURI + buildingGUID);
        ontModel.add(site, containsZone, building);
    }

    private void assignAggregations(ArrayList<TopologicalEntity> entityList) {
        for (TopologicalEntity entity :
                entityList) {
            Individual element = ontModel.getIndividual(ontologyURI + entity.getIfcID());
            ObjectProperty hasSubElement = ontModel.createObjectProperty(BOT.HAS_SUB_ELEMENT);
            ArrayList<String> decomposedElementGUIDList = entity.getDecomposedBuildingElementGUIDs();
            for (String decomposedElementGUID :
                    decomposedElementGUIDList) {
                Individual decomposedElement = ontModel.getIndividual(ontologyURI + decomposedElementGUID);
                ontModel.add(element, hasSubElement, decomposedElement);
            }
        }
    }

}
