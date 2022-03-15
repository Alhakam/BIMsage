package de.tud.cib.bimsage.gui.configuration.preconfigdata.configbeans;

import java.util.ArrayList;

public class TherapyConfigData extends ConfigData {

    private ArrayList<SHACLRuleSet> shaclList;

    public TherapyConfigData() {
        shaclList = new ArrayList<>();
    }

    public TherapyConfigData(String id, String name, ArrayList<SHACLRuleSet> shaclList) {
        this.id = id;
        this.name = name;
        this.shaclList = shaclList;
    }

    /**
     * Adds a String that references a SHACL file to the shacl list
     * @param shaclID The ID that is used in the ResourceConfiguration.json for shacl rules
     */
    public void addSHACLRuleConfig(SHACLRuleSet shaclID) {
        this.shaclList.add(shaclID);
    }

    /*
    Setter
     */

    public void setSHACLRuleConfigList(ArrayList<SHACLRuleSet> shaclList) {
        this.shaclList = shaclList;
    }

    /*
    Getter
     */

    public ArrayList<SHACLRuleSet> getSHACLRuleConfigList() {
        return shaclList;
    }
}
