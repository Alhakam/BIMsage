package de.tud.cib.bimsage.gui.configuration.resources;

import de.tud.cib.bimsage.gui.configuration.resources.ontology.OntologyData;
import de.tud.cib.bimsage.gui.configuration.resources.ontology.SHACLData;
import de.tud.cib.bimsage.gui.configuration.resources.ontology.SPARQLData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Singleton used for referencing resource files.
 * Paths of resource files are initialized by reading the ResourceConfiguration.json
 */
public class ResourceConfiguration {

    private final String configurationPath = "src/main/resources/ResourceConfiguration.json";
    private static ResourceConfiguration instance;

    private ArrayList<OntologyData> coreOntologyList;
    private ArrayList<OntologyData> extensionList;
    private ArrayList<OntologyData> constructionOntologyList;
    private ArrayList<OntologyData> completeOntologyList;
    private ArrayList<SHACLData> shaclDataList;
    private ArrayList<SPARQLData> sparqlDataList;
    private String diagnosisConfigURL;
    private String therapyConfigURL;

    //TODO:Fehlerbehandlung von Configuration erledigen
    private ResourceConfiguration() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject rootObject = (JSONObject) parser.parse(new FileReader(configurationPath));
            this.importCoreOntology(rootObject);
            this.importDOTExtensions(rootObject);
            this.importConstructionOntologies(rootObject);
            this.importSHACLRules(rootObject);
            this.importSPARQLData(rootObject);
            this.importDiagnosisConfigURL(rootObject);
            this.importTherapyConfigURL(rootObject);
            this.mergeOntologyLists();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static ResourceConfiguration getInstance() {
        if (instance == null) {
            instance = new ResourceConfiguration();
        }
        return instance;
    }

    /*
    Getter
     */

    public ArrayList<OntologyData> getExtensionList() {
        return extensionList;
    }

    /**
     * Returns the ontology data of a specific tbox
     * @param prefix The prefix of the tbox, which is queried
     * @return The ontology data of the requested tbox
     */
    public OntologyData getTboxEntry(String prefix) {
        for (OntologyData tboxEntry :
                completeOntologyList) {
            if (prefix.equals(tboxEntry.getPrefix()))
                return tboxEntry;
        }
        return null;
    }

    /**
     * Returns the shacl data of a specific shacl-rules file
     * @param id The id of the shacl file, which is queried
     * @return The shacl data of the requested shacl file
     */
    public SHACLData getSHACLEntry(String id) {
        for (SHACLData shaclEntry :
                shaclDataList) {
            if (id.equals(shaclEntry.getId()))
                return shaclEntry;
        }
        return null;
    }

    /**
     * Returns the sparql data of a specific sparql-query file
     * @param id The id of the sparql file, which is queried
     * @return The sparql data of the requested sparql file
     */
    public SPARQLData getSPARQLEntry(String id) {
        for (SPARQLData sparqlData :
                sparqlDataList) {
            if (id.equals(sparqlData.getId()))
                return sparqlData;
        }
        return null;
    }

    public String getDiagnosisConfigURL() {
        return diagnosisConfigURL;
    }

    public String getTherapyConfigURL() {
        return therapyConfigURL;
    }

    /*
    Internal Methods
     */

    private void importCoreOntology(JSONObject rootObject) {
        coreOntologyList = new ArrayList<>();
        JSONObject ontologies = (JSONObject) rootObject.get("ontologies");
        JSONObject coreOntology = (JSONObject) ontologies.get("coreOntology");
        coreOntologyList.add(this.createOntologyData(coreOntology));
    }

    private void importDOTExtensions(JSONObject rootObject) {
        extensionList = new ArrayList<>();
        JSONObject ontologies = (JSONObject) rootObject.get("ontologies");
        JSONArray damageOntologies = (JSONArray) ontologies.get("dotExtensions");
        damageOntologies.forEach(jsonEntry -> {
            extensionList.add(this.createExtensionData((JSONObject) jsonEntry));
        });
    }

    private void importConstructionOntologies(JSONObject rootObject) {
        constructionOntologyList = new ArrayList<>();
        JSONObject ontologies = (JSONObject) rootObject.get("ontologies");
        JSONArray constructionOntologies = (JSONArray) ontologies.get("constructionOntologies");
        constructionOntologies.forEach(jsonEntry -> {
            constructionOntologyList.add(this.createOntologyData((JSONObject) jsonEntry));
        });
    }

    private OntologyData createOntologyData(JSONObject rootObject) {
        OntologyData ontologyData = new OntologyData(rootObject.get("url").toString());
        ontologyData.setPrefix(rootObject.get("prefix").toString());
        ontologyData.setNamespace(rootObject.get("namespace").toString());
        ontologyData.setDomainLabel(rootObject.get("domainLabel").toString());
        return ontologyData;
    }

    private OntologyData createExtensionData(JSONObject rootObject) {
        OntologyData ontologyData = this.createOntologyData(rootObject);
        ontologyData.setRootClass(rootObject.get("rootClass").toString());
        return ontologyData;
    }

    private void importSHACLRules(JSONObject rootObject) {
        shaclDataList = new ArrayList<>();
        JSONArray shaclRules = (JSONArray) rootObject.get("shaclRules");
        shaclRules.forEach(jsonEntry -> {
            shaclDataList.add(this.createSHACLData((JSONObject) jsonEntry));
        });
    }

    private SHACLData createSHACLData(JSONObject rootObject) {
        SHACLData shaclData = new SHACLData(rootObject.get("url").toString());
        shaclData.setId(rootObject.get("id").toString());
        shaclData.setName(rootObject.get("name").toString());
        shaclData.setValidating(Boolean.getBoolean(rootObject.get("validation").toString()));
        return shaclData;
    }

    private void importSPARQLData(JSONObject rootObject) {
        sparqlDataList = new ArrayList<>();
        JSONArray shaclRules = (JSONArray) rootObject.get("sparqlQueries");
        shaclRules.forEach(jsonEntry -> {
            sparqlDataList.add(this.createSPARQLData((JSONObject) jsonEntry));
        });
    }

    private SPARQLData createSPARQLData(JSONObject rootObject) {
        SPARQLData sparqlData = new SPARQLData(rootObject.get("url").toString());
        sparqlData.setId(rootObject.get("id").toString());
        return sparqlData;
    }

    private void importDiagnosisConfigURL(JSONObject rootObject) {
        JSONObject configurations = (JSONObject) rootObject.get("configurations");
        JSONObject tboxConfiguration = (JSONObject) configurations.get("diagnosisConfiguration");
        diagnosisConfigURL = tboxConfiguration.get("url").toString();
    }

    private void importTherapyConfigURL(JSONObject rootObject) {
        JSONObject configurations = (JSONObject) rootObject.get("configurations");
        JSONObject tboxConfiguration = (JSONObject) configurations.get("therapyConfiguration");
        therapyConfigURL = tboxConfiguration.get("url").toString();
    }

    private void mergeOntologyLists() {
        completeOntologyList = new ArrayList<>();
        completeOntologyList.addAll(coreOntologyList);
        completeOntologyList.addAll(extensionList);
        completeOntologyList.addAll(constructionOntologyList);
    }

}
