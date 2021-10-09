package de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans;

import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.ConfigData;

import java.util.ArrayList;

public class DiagnosisConfigData extends ConfigData {

    private ArrayList<String> tboxList;
    private ArrayList<String> shaclList;

    public DiagnosisConfigData() {
        tboxList = new ArrayList<>();
        shaclList = new ArrayList<>();
    }

    public DiagnosisConfigData(String id, String name, ArrayList<String> tboxList, ArrayList<String> shaclList) {
        this.id = id;
        this.name = name;
        this.tboxList = tboxList;
        this.shaclList = shaclList;
    }

    /**
     * Adds a String that references a TBox to the tbox list
     * @param tboxPrefix The prefix that is used in the ResourceConfiguration.json for ontologies
     */
    public void addTBoxReference(String tboxPrefix) {
        this.tboxList.add(tboxPrefix);
    }

    /**
     * Adds a String that references a SHACL file to the shacl list
     * @param shaclID The ID that is used in the ResourceConfiguration.json for shacl rules
     */
    public void addSHACLReference(String shaclID) {
        this.shaclList.add(shaclID);
    }

    /*
    Setter
     */

    public void setTboxList(ArrayList<String> tboxList) {
        this.tboxList = tboxList;
    }

    public void setShaclList(ArrayList<String> shaclList) {
        this.shaclList = shaclList;
    }

    /*
    Getter
     */

    public ArrayList<String> getTboxList() {
        return tboxList;
    }

    public ArrayList<String> getShaclList() {
        return shaclList;
    }
}
