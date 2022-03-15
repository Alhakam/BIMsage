package de.tud.cib.bimsage.gui.components.filter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class FilterDefinition {

    private String id;
    private String label;
    private String description;
    private LinkedHashSet<String> parameterList;
    private ArrayList<String> resultTypeList;
    private List<String> resultList;
    private FilterFunction filterFunction;

    @Override
    public String toString() {
        return this.label;
    }

    /*
    Setter
     */

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setParameterList(LinkedHashSet<String> parameterList) {
        this.parameterList = parameterList;
    }

    public void setResultTypeList(ArrayList<String> resultTypeList) {
        this.resultTypeList = resultTypeList;
    }

    public void setResultList(List<String> resultList) {
        this.resultList = resultList;
    }

    public void setFilterFunction(FilterFunction filterFunction) {
        this.filterFunction = filterFunction;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
    Getter
     */

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public LinkedHashSet<String> getParameterList() {
        return parameterList;
    }

    public ArrayList<String> getResultTypeList() {
        return resultTypeList;
    }

    public List<String> getResultList() {
        return resultList;
    }

    public FilterFunction getFilterFunction() {
        return filterFunction;
    }

    public String getDescription() {
        return description;
    }
}
