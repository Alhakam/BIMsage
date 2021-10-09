package de.tud.cib.dotknow.gui.components.filter;

import java.util.List;
import java.util.TreeMap;

/**
 * Function used in FilterDefinition objects for processing a specific filteroperation.
 */
public interface FilterFunction {

    /**
     * Processes a filter function. Should be applied on a given ontology.
     * @param parameterSet The set of parameters that modify the function
     * @return the list of results
     */
    public List<String> applyFilter(TreeMap<Integer,String> parameterSet);

}
