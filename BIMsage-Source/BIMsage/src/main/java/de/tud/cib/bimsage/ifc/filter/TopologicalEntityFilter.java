package de.tud.cib.bimsage.ifc.filter;

import com.apstex.ifctoolbox.ifc.*;
import com.apstex.ifctoolbox.ifcmodel.IfcModel;
import com.apstex.step.core.SET;
import de.tud.cib.bimsage.ifc.TopologicalEntity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class TopologicalEntityFilter {

    protected IfcModel ifcModel;

    /**
     * Initializes the IFC model representation for further filtering operations
     * @param ifc IFC-File
     */
    public TopologicalEntityFilter(Path ifc) {
        ifcModel = new IfcModel();
        try {
            ifcModel.readStepFile(ifc.toFile());
        } catch (Exception e) {
            System.out.println("Reading of IFC-File failed");
            e.printStackTrace();
        }
    }

    /**
     * Filters the IFC Model for entities of IfcBuildingElement
     * @return ArrayList of TopologicalEntity
     */
    public ArrayList<TopologicalEntity> filterEntities() {
        ArrayList<TopologicalEntity> entityList = new ArrayList<>();
        Collection<IfcBuildingElement> ifcBuildingElementList = ifcModel.getCollection(IfcBuildingElement.class);
        for (IfcBuildingElement ifcBuildingElement :
                ifcBuildingElementList) {
            TopologicalEntity topologicalEntity = new TopologicalEntity(ifcBuildingElement.getGlobalId().getDecodedValue());
            topologicalEntity.setDecomposedBuildingElementGUIDs(this.addFilteredAggregations((IfcProduct) ifcBuildingElement));
            entityList.add(topologicalEntity);
        }
        return entityList;
    }

    /**
     * Filters all IfcBuildingElement entities and stores the IFC-GUIDs in an ArrayList
     * @return ArrayList with GUID-Strings
     */
    public ArrayList<String> filterEntityGUIDs() {
        ArrayList<String> guidList = new ArrayList<>();
        Collection<IfcBuildingElement> buildingElementSet = ifcModel.getCollection(IfcBuildingElement.class);
        for (IfcBuildingElement buildingElement :
                buildingElementSet) {
            guidList.add(buildingElement.getGlobalId().getDecodedValue());
        }
        return guidList;
    }

    /**
     * Filters all IfcBuilding entities and stores the IFC-GUIDs in an ArrayList
     * @return ArrayList with GUID-Strings
     */
    public ArrayList<String> filterBuildingGUIDs() {
        ArrayList<String> guidList = new ArrayList<>();
        Collection<IfcBuilding> buildingSet = ifcModel.getCollection(IfcBuilding.class);
        for (IfcBuilding building :
                buildingSet) {
            guidList.add(building.getGlobalId().getDecodedValue());
        }
        return guidList;
    }

    /**
     * Filters all IfcSite entities and stores the IFC-GUIDs in an ArrayList
     * @return ArrayList with GUID-Strings
     */
    public ArrayList<String> filterSiteGUIDs() {
        ArrayList<String> guidList = new ArrayList<>();
        Collection<IfcSite> siteSet = ifcModel.getCollection(IfcSite.class);
        for (IfcSite site :
                siteSet) {
            guidList.add(site.getGlobalId().getDecodedValue());
        }
        return guidList;
    }

    /*
    Internal Methods
     */

    private ArrayList<String> addFilteredAggregations(IfcProduct product) {
        ArrayList<String> decomposedEntities = new ArrayList<>();
        Collection<IfcRelAggregates> aggregationList = ifcModel.getCollection(IfcRelAggregates.class);
        for (IfcRelAggregates aggregation :
                aggregationList) {
            if (aggregation.getRelatingObject().getGlobalId().getDecodedValue().equals(product.getGlobalId().getDecodedValue())) {
                SET<? extends IfcObjectDefinition> aggregatedObjects = aggregation.getRelatedObjects();
                for (IfcObjectDefinition aggregatedObject :
                        aggregatedObjects) {
                    decomposedEntities.add(aggregatedObject.getGlobalId().getDecodedValue());
                }
            }
        }
        return decomposedEntities;
    }

}
