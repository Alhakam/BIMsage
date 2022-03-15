package de.tud.cib.bimsage.ontology.reasoner;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.*;

import java.util.ArrayList;

/**
 * Tool for managing the SHACL reasoning process for an ontology
 * Provides functions for adding SHACL-rulesets to an ontology and reasoning it with an appropriate reasoning engine.
 */
public class SHACLReasonerManager extends ReasonerManager{

    private ArrayList<String> shaclRulesetList;

    public SHACLReasonerManager(OntModel ontology) {
        super(ontology);
        this.shaclRulesetList = new ArrayList<String>();
    }

    /**
     * Adds a shacl ruleset to the list of shacl files, which will be reasoned against the ontology.
     * The reasoning process is started through one of the reasoning functions (e.g. reasonWithJenaReasoner).
     * @param shaclURL The URL of the SHACL ruleset file that needs to be added
     */
    public void addSHACLRuleset(String shaclURL) {
        shaclRulesetList.add(shaclURL);
    }

    /**
     * Removes a shacl ruleset from the list of shacl files.
     * @param shaclURL The URL of the SHACL ruleset file that needs to be removed
     */
    public void removeSHACLRuleset(String shaclURL) {
        shaclRulesetList.remove(shaclURL);
    }

    /**
     * Reasons the ontology by applying all shacl rulesets in a jena reasoning engine
     * @return The inference model that contains all reasoned results in a TTL file
     */
    @Override
    public Model reasonWithJenaReasoner() {
        infModel = ModelFactory.createOntologyModel();
        for (String shaclRuleset :
                shaclRulesetList) {
            OntModel shaclModel = ModelFactory.createOntologyModel();
            shaclModel.read(shaclRuleset);
            infModel.add(RuleUtil.executeRules(ontology, shaclModel, infModel, null));
        }
        return infModel;
    }

    /**
     * Validates the ontology by applying all shacl rulesets and returning a validation report as Resource
     * @return Resource that represents the validation report (can be transformed to a RDF model)
     */
    public Resource validateWithReportResource() {
        OntModel validationModel = this.createValidationModel();
        return ValidationUtil.validateModel(ontology, validationModel, true);
    }

    /**
     * Validates the ontology by applying all shacl rulesets and returning a validation report as Validation Report object
     * @return Validation Report object
     */
    public ValidationReport validateWithValidationReport() {
        OntModel validationModel = this.createValidationModel();
        ValidationEngine validationEngine = ValidationUtil.createValidationEngine(ontology, validationModel, true);
        validationEngine.setConfiguration(new ValidationEngineConfiguration().setValidateShapes(true));
        try {
            validationEngine.applyEntailments();
            validationEngine.validateAll();
        } catch (InterruptedException e) {
            return null;
        }
        return validationEngine.getValidationReport();
    }

    private OntModel createValidationModel() {
        OntModel validationModel = ModelFactory.createOntologyModel();
        for (String shaclRuleset :
                shaclRulesetList) {
            OntModel shaclModel = ModelFactory.createOntologyModel();
            shaclModel.read(shaclRuleset);
            validationModel.add(shaclModel);
        }
        return validationModel;
    }

}
