package de.tud.dotknow.ontology.reasoner;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;

import java.util.ArrayList;

/**
 * Tool for managing the OWL reasoning process for an ontology
 * Provides functions for adding Tboxes to an ontology and reasoning it with an appropriate reasoning engine.
 */
public class OWLReasonerManager extends ReasonerManager{

    private ArrayList<String> tboxList;

    public OWLReasonerManager(OntModel ontology) {
        super(ontology);
        this.tboxList = new ArrayList<String>();
    }

    /**
     * Adds a tbox to the list of tboxes, which will be reasoned against the ontology.
     * The reasoning process is started through one of the reasoning functions (e.g. reasonWithJenaReasoner).
     * @param tboxURL The URL or file path of the tbox, which is added.
     */
    public void addTBox(String tboxURL) {
        tboxList.add(tboxURL);
    }

    /**
     * Removes a tbox from the list of tboxes.
     * @param tboxURL The URL or file path of the tbox, which is removed.
     */
    public void removeTBox(String tboxURL) {
        for (String tbox :
                tboxList) {
            if (tbox.equals(tboxURL))
                tboxList.remove(tbox);
        }
    }

    /**
     * Reasons the ontology by applying all added TBoxes in a jena reasoning engine
     * @return The inference model that contains all reasoned results in a TTL file
     */
    @Override
    public OntModel reasonWithJenaReasoner() {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        for (String tbox :
                tboxList) {
            reasoner.bindSchema(FileManager.get().loadModel(tbox));
        }
        infModel.add(ModelFactory.createInfModel(reasoner, ontology));
        return infModel;
    }
}
