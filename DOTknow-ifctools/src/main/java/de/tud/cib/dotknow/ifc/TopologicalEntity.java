package de.tud.cib.dotknow.ifc;

import java.util.ArrayList;

/**
 * Representation of an IFC entity
 */
public class TopologicalEntity {

    private String ifcID;
    private ArrayList<String> decomposedBuildingElementGUIDs;

    public TopologicalEntity(String ifcID) {
        this.ifcID = ifcID;
        this.decomposedBuildingElementGUIDs = new ArrayList<>();
    }

    public TopologicalEntity(){}

    public String getIfcID() {
        return ifcID;
    }

    public ArrayList<String> getDecomposedBuildingElementGUIDs() {
        return decomposedBuildingElementGUIDs;
    }

    public void setIfcID(String ifcID) {
        this.ifcID = ifcID;
    }

    public void setDecomposedBuildingElementGUIDs(ArrayList<String> decomposedBuildingElementGUIDs) {
        this.decomposedBuildingElementGUIDs = decomposedBuildingElementGUIDs;
    }

    public void addBuildingElementAsDecomposed(String guid) {
        this.decomposedBuildingElementGUIDs.add(guid);
    }
}