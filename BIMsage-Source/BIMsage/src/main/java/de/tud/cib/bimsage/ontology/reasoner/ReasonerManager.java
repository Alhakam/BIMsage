package de.tud.cib.bimsage.ontology.reasoner;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;

/**
 * Tool for managing the reasoning process for an ontology
 */
public class ReasonerManager {

    protected OntModel ontology;
    protected OntModel infModel;

    public ReasonerManager(OntModel ontology) {
        this.ontology = ontology;
        infModel = ModelFactory.createOntologyModel();
    }

    /**
     * Merges the ontology which has been reasoned with the resulting inference model
     * @return The merged result of the ontology and inference model
     */
    public OntModel mergeOntologyWithInfModel() {
        ontology.add(infModel);
        return ontology;
    }

    /**
     * Reasons the ontology by utilizing the jena reasoning engine
     * @return The inference model that contains all reasoned results in a TTL file
     */
    public Model reasonWithJenaReasoner() {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        infModel.add(ModelFactory.createInfModel(reasoner, ontology));
        return infModel;
    }

    /*
    Setter
     */

    public void setOntology(OntModel ontology) {
        this.ontology = ontology;
    }

    /*
    Getter
     */

    public OntModel getOntology() {
        return ontology;
    }

    public Model getInfModel() {
        return infModel;
    }

}
