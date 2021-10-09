package de.tud.cib.dotknow.gui.components.filter.preconfig;

import de.tud.cib.dotknow.gui.components.filter.FilterDefinition;
import de.tud.cib.dotknow.gui.configuration.resources.ResourceConfiguration;
import de.tud.cib.dotknow.gui.configuration.resources.ontology.SPARQLData;
import de.tud.dotknow.ontology.representation.ActiveOntology;
import de.tud.dotknow.ontology.sparqlquery.OntQueryTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class FilterPreConfig {

    private List<FilterDefinition> preconfigList;

    private static FilterPreConfig ourInstance = new FilterPreConfig();

    public static FilterPreConfig getInstance() {
        return ourInstance;
    }

    private FilterPreConfig() {
        preconfigList = new ArrayList<>();
        preconfigList.add(this.configureStoneDamageMapFilter());
        preconfigList.add(this.configureDamageIndexQuery());
        preconfigList.add(this.configureStoneDamageSpreadQuery());
        preconfigList.add(this.configureCategoryFilter());
        preconfigList.add(this.configureStoneDamageMultipleFilter());
    }

    /*
    Getter
     */

    public List<FilterDefinition> getPreconfigList() {
        return preconfigList;
    }

    /*
    FilterDefinitions
     */

    private FilterDefinition configureStoneDamageMapFilter() {
        String queryID = "queryStoneDamageMap";
        SPARQLData sparqlData = ResourceConfiguration.getInstance().getSPARQLEntry(queryID);
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setId(queryID);
        filterDefinition.setLabel("Stone Damage Map");
        filterDefinition.setDescription("Filters stone damages based on specific properties");
        LinkedHashSet<String> parameterList = new LinkedHashSet<>();
        parameterList.add("Damage Class");
        parameterList.add("Damage Impact");
        parameterList.add("Material Loss");
        filterDefinition.setParameterList(parameterList);
        ArrayList<String> resultTypeList = new ArrayList<>();
        resultTypeList.add("Resulting Components");
        filterDefinition.setResultTypeList(resultTypeList);
        filterDefinition.setFilterFunction(parameterSet -> {
            List<String> resultList = new ArrayList<>();
            OntModel ontology = ActiveOntology.getInstance().getOntology();
            ResultSet resultSet = OntQueryTool.applyQueryWithModifiedDamageParameters(
                    ontology, sparqlData.getUrl(), null, parameterSet.get(0)); //Pathconfig für SPARQL einstellen
            while (resultSet.hasNext()) {
                QuerySolution querySolution = resultSet.next();
                if(querySolution.getLiteral("?damageImpactValue").getInt() >= Integer.parseInt(parameterSet.get(1))
                        && querySolution.getLiteral("?materialLossValue").getInt() >= Integer.parseInt(parameterSet.get(2)))
                    resultList.add(StringUtils.substringAfter(querySolution.get("?stone").asResource().getURI(), "#"));
            }
            return resultList;
        });
        return filterDefinition;
    }

    private FilterDefinition configureDamageIndexQuery() {
        ResourceConfiguration resourceConfiguration = ResourceConfiguration.getInstance();
        SPARQLData stoneAreaQuery = resourceConfiguration.getSPARQLEntry("queryStoneDamageArea");
        SPARQLData stoneAreasByTypeQuery = resourceConfiguration.getSPARQLEntry("queryDamageAreasByType");
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setId("queryStoneDamageIndex");
        filterDefinition.setLabel("Stone Damage Index");
        filterDefinition.setDescription("Determines the damage index for a group of stones");
        LinkedHashSet<String> parameterList = new LinkedHashSet<>();
        parameterList.add("Damage Class");
        filterDefinition.setParameterList(parameterList);
        ArrayList<String> resultTypeList = new ArrayList<>();
        resultTypeList.add("Damage Index");
        filterDefinition.setResultTypeList(resultTypeList);
        filterDefinition.setFilterFunction(parameterSet -> {
            List<String> resultList = new ArrayList<>();
            OntModel ontology = ActiveOntology.getInstance().getOntology();
            double stoneArea = Double.parseDouble(OntQueryTool.applyQuery(ontology, stoneAreaQuery.getUrl()).next().getLiteral("?stoneAreaSum").getString()) ;
            ResultSet damageAreaResults = OntQueryTool.applyQueryWithModifiedDamageType(ontology, stoneAreasByTypeQuery.getUrl(), parameterSet.get(0));
            double damageIndexParameterSum = 0;
            while (damageAreaResults.hasNext()) {
                int damageImpact = 0;
                QuerySolution querySolution = damageAreaResults.next();
                String damageImpactString = querySolution.getLiteral("?damageImpact").getString();
                if (!damageImpactString.equals("null"))
                    damageImpact = Integer.parseInt(damageImpactString);
                double damageArea = querySolution.getLiteral("?damageAreaValue").getDouble();
                double damageIndexParameter = (damageArea / stoneArea) * Math.pow(damageImpact, 2);
                damageIndexParameterSum = damageIndexParameterSum + damageIndexParameter;
            }
            double resultValue = Math.sqrt(damageIndexParameterSum);
            resultList.add(Double.toString(resultValue));
            return resultList;
        });
        return filterDefinition;
    }

    private FilterDefinition configureStoneDamageSpreadQuery() {
        String queryID = "queryDamageArea";
        SPARQLData sparqlData = ResourceConfiguration.getInstance().getSPARQLEntry(queryID);
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setId(queryID);
        filterDefinition.setLabel("Stone Damage Spread");
        filterDefinition.setDescription("Determines the percentage of damage for a group of stones and a specific damage category");
        LinkedHashSet<String> parameterList = new LinkedHashSet<>();
        parameterList.add("Damage Class");
        filterDefinition.setParameterList(parameterList);
        ArrayList<String> resultTypeList = new ArrayList<>();
        resultTypeList.add("Damaged Area");
        filterDefinition.setResultTypeList(resultTypeList);
        filterDefinition.setFilterFunction(parameterSet -> {
            List<String> resultList = new ArrayList<>();
            OntModel ontology = ActiveOntology.getInstance().getOntology();
            ResultSet resultSet = OntQueryTool.applyQueryWithModifiedDamageParameters
                    (ontology, sparqlData.getUrl(), null, parameterSet.get(0));
            LinkedHashSet<String> damageAreaList = new LinkedHashSet<>();
            double damageSpread = 0;
            while (resultSet.hasNext()) {
                QuerySolution querySolution = resultSet.next();
                if (!damageAreaList.contains(querySolution.getResource("?damageArea").getURI())) {
                    double damageArea = querySolution.getLiteral("?damageAreaValue").getDouble();
                    double stoneArea = querySolution.getLiteral("?stoneAreaValue").getDouble();
                    damageSpread = damageSpread + (damageArea / stoneArea);
                }
                damageAreaList.add(querySolution.getResource("?damageArea").getURI());
            }
            resultList.add(Double.toString(damageSpread));
            return resultList;
        });
        return filterDefinition;
    }

    private FilterDefinition configureCategoryFilter() {
        String queryID = "queryDamageCategories";
        SPARQLData sparqlData = ResourceConfiguration.getInstance().getSPARQLEntry(queryID);
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setId(queryID);
        filterDefinition.setLabel("Damage Categories");
        filterDefinition.setDescription("Returns which damage categories occur.");
        LinkedHashSet<String> parameterList = new LinkedHashSet<>();
        filterDefinition.setParameterList(parameterList);
        ArrayList<String> resultTypeList = new ArrayList<>();
        resultTypeList.add("Damage Categories");
        filterDefinition.setResultTypeList(resultTypeList);
        filterDefinition.setFilterFunction(parameterSet -> {
            List<String> resultList = new ArrayList<>();
            OntModel ontology = ActiveOntology.getInstance().getOntology();
            ResultSet resultSet = OntQueryTool.applyQuery(ontology, sparqlData.getUrl());
            while (resultSet.hasNext()) {
                resultList.add(StringUtils.substringAfter(resultSet.next().get("?damageClass").asResource().getURI(), "#"));
            }
            resultList.remove("Damage");
            resultList.remove("Resource");
            resultList.remove("Thing");
            resultList.remove("UnidentifiedDamage");
            return resultList;
        });
        return filterDefinition;
    }

    private FilterDefinition configureStoneDamageMultipleFilter() {
        String queryID = "queryDamageMultiples";
        SPARQLData sparqlData = ResourceConfiguration.getInstance().getSPARQLEntry(queryID);
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setId(queryID);
        filterDefinition.setLabel("Damage Multiples");
        filterDefinition.setDescription("Returns a list of the stones that have more than one damage.");
        LinkedHashSet<String> parameterList = new LinkedHashSet<>();
        filterDefinition.setParameterList(parameterList);
        ArrayList<String> resultTypeList = new ArrayList<>();
        resultTypeList.add("Resulting Components");
        filterDefinition.setResultTypeList(resultTypeList);
        filterDefinition.setFilterFunction(parameterSet -> {
            List<String> resultList = new ArrayList<>();
            OntModel ontology = ActiveOntology.getInstance().getOntology();
            ResultSet resultSet = OntQueryTool.applyQuery(ontology, sparqlData.getUrl());
            while (resultSet.hasNext()) {
                resultList.add(StringUtils.substringAfter(resultSet.next().get("?stone").asResource().getURI(), "#"));
            }
            return resultList;
        });
        return filterDefinition;
    }

}
