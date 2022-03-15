package de.tud.cib.bimsage.ontology.representation;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;

public class ActiveOntology {

    private final String validationReportFileName = "validationReport.ttl";

    private OntModel ontology;
    private OntModel validationReport;

    private String ontologyURI;
    private String ontologyNS;

    //TODO: Funktion einbauen, die den Namen variabel wechseln lässt
    private final String fileName = "Construction.ttl";

    private static ActiveOntology ourInstance = new ActiveOntology();

    public static ActiveOntology getInstance() {
        return ourInstance;
    }

    private ActiveOntology() {
        this.ontology = new OntModelImpl(OntModelSpec.OWL_DL_MEM);
    }

    /*
    Setter
     */

    public void setOntology(OntModel ontology) {
        this.ontology = ontology;
    }

    public void setValidationReport(OntModel validationReport) {
        this.validationReport = validationReport;
    }

    public void setOntologyURI(String ontologyURI) {
        this.ontologyURI = ontologyURI;
    }

    public void setOntologyNS(String ontologyNS) {
        this.ontologyNS = ontologyNS;
    }

    /*
    Getter
     */

    public OntModel getOntology() {
        return ontology;
    }

    public OntModel getValidationReport() {
        return validationReport;
    }

    public String getOntologyURI() {
        return ontologyURI;
    }

    public String getOntologyNS() {
        return ontologyNS;
    }

    public String getValidationReportFileName() {
        return validationReportFileName;
    }

    public String getFileName() {
        return fileName;
    }
}
