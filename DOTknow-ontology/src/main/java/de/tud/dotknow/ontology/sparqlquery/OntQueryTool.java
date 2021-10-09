package de.tud.dotknow.ontology.sparqlquery;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;

/**
 * Class that provides static functions for applying damage-related SPARQL-Queries to an existing ontology
 */
public class OntQueryTool {

    private OntQueryTool(){}

    /**
     * Applies a SPARQL query to an existing ontology
     * @param ontology The given ontology as OntModel
     * @param queryPath The file location of the SPARQL query
     * @return the ResultSet of the processed SPARQL query
     */
    public static ResultSet applyQuery(OntModel ontology, String queryPath) {
        Query damageQuery = QueryFactory.read(queryPath);
        QueryExecution queryExecution = QueryExecutionFactory.create(damageQuery, ontology);
        return queryExecution.execSelect();
    }

    /**
     * Modifies a SPARQL query to fit a certain damageClass and apply it to an existing ontology
     * @param ontology The given ontology as OntModel
     * @param queryPath The file location of the SPARQL query, which will be modified
     * @param damageClass The damage class that exchanges the general dot:Damage class in the SPARQL query
     * @return the ResultSet of the modified and processed SPARQL query
     */
    public static ResultSet applyQueryWithModifiedDamageType(OntModel ontology, String queryPath, String damageClass) {
        Query damageQuery = QueryFactory.read(queryPath);
        String modifiedQueryString = damageQuery.toString().replace("dot:Damage ", "nsd:" + damageClass + " ");
        damageQuery = QueryFactory.create(modifiedQueryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(damageQuery, ontology);
        return queryExecution.execSelect();
    }

    /**
     * Modifies a SPARQL query to fit a certain damageClass, modifies filtering parameters and applies it to an existing ontology
     * @param ontology The given ontology as OntModel
     * @param queryPath The file location of the SPARQL query, which will be modified
     * @param damageAreaID (optional) The ID of the damage area. If this parameter is null, the query is applied on all damage areas.
     * @param damageClass The damage class that exchanges the general dot:Damage class in the SPARQL query
     * @return the ResultSet of the modified and processed SPARQL query
     */
    public static ResultSet applyQueryWithModifiedDamageParameters(
            OntModel ontology, String queryPath, String damageAreaID, String damageClass) {
        Query damageQuery = QueryFactory.read(queryPath);
        String modifiedQueryString = damageQuery.toString().replace("dot:Damage ", "nsd:" + damageClass + " ");
        if(damageAreaID != null)
            modifiedQueryString = modifiedQueryString.replace("damageAreaID", damageAreaID);
        else {
            modifiedQueryString = modifiedQueryString.replace("dot:hasDamageArea  ?damageArea ;", "");
            modifiedQueryString = modifiedQueryString.replace("FILTER regex(str(?damageArea), \"damageAreaID\")", "");
        }
        damageQuery = QueryFactory.create(modifiedQueryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(damageQuery, ontology);
        return queryExecution.execSelect();
    }

    /**
     * Modifies SPARQL query for renovation class filtering to fit a certain renovation type and apply it to an existing ontology
     * @param ontology The given ontology as OntModel
     * @param queryPath The file location of the SPARQL query, which will be modified
     * @param renovationName The renovation name that exchanges the general srmo:renovationName class in the SPARQL query
     * @return the ResultSet of the modified and processed SPARQL query
     */
    public static ResultSet applyRenovationClassQuery(OntModel ontology, String queryPath, String renovationName) {
        Query renovationQuery = QueryFactory.read(queryPath);
        String modifiedQueryString = renovationQuery.toString().replace("renovationName", renovationName);
        renovationQuery = QueryFactory.create(modifiedQueryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(renovationQuery, ontology);
        return queryExecution.execSelect();
    }

}
