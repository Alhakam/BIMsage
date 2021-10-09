package de.tud.cib.dotknow.gui.configuration.preconfigdata;

import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.DiagnosisConfigData;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.SHACLRuleSet;
import de.tud.cib.dotknow.gui.configuration.preconfigdata.configbeans.TherapyConfigData;
import de.tud.cib.dotknow.gui.configuration.resources.ResourceConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Singleton used for referencing config files.
 * Paths of resource files are initialized by reading the respective json files that are referenced in ResourceConfiguration.json
 */
public class PreConfigConfiguration {

    private static PreConfigConfiguration instance;

    private ArrayList<DiagnosisConfigData> diagnosisConfigDataList;
    private ArrayList<TherapyConfigData> therapyConfigDataList;

    public static PreConfigConfiguration getInstance() {
        if (instance == null) {
            instance = new PreConfigConfiguration();
        }
        return instance;
    }

    private PreConfigConfiguration() {
        try {
            diagnosisConfigDataList = new ArrayList<>();
            therapyConfigDataList = new ArrayList<>();
            ResourceConfiguration resourceConfiguration = ResourceConfiguration.getInstance();
            JSONParser parser = new JSONParser();
            JSONObject diagnosisRootObject = (JSONObject) parser.parse(new FileReader(resourceConfiguration.getDiagnosisConfigURL()));
            this.importDiagnosisConfig(diagnosisRootObject);
            JSONObject therapyRootObject = (JSONObject) parser.parse(new FileReader(resourceConfiguration.getTherapyConfigURL()));
            this.importTherapyConfig(therapyRootObject);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
    Getter
     */

    public ArrayList<DiagnosisConfigData> getDiagnosisConfigDataList() {
        return diagnosisConfigDataList;
    }

    public ArrayList<TherapyConfigData> getTherapyConfigDataList() {
        return therapyConfigDataList;
    }

    /*
    Internal Methods
     */

    private void importDiagnosisConfig(JSONObject rootObject) {
        JSONArray ontConfigurations = (JSONArray) rootObject.get("diagnosisConfigurations");
        ontConfigurations.forEach(diagnosisConfig -> {
            diagnosisConfigDataList.add(this.readDiagnosisConfigDataElement((JSONObject) diagnosisConfig));
        });
    }

    private DiagnosisConfigData readDiagnosisConfigDataElement(JSONObject diagnosisConfig) {
        DiagnosisConfigData diagnosisConfigData = new DiagnosisConfigData();
        diagnosisConfigData.setId(diagnosisConfig.get("id").toString());
        diagnosisConfigData.setName(diagnosisConfig.get("name").toString());
        JSONArray tboxList = (JSONArray) diagnosisConfig.get("tboxList");
        tboxList.forEach(jsonEntry -> {
            diagnosisConfigData.addTBoxReference(jsonEntry.toString());
        });
        JSONArray shaclList = (JSONArray) diagnosisConfig.get("shaclRulesList");
        shaclList.forEach(jsonEntry -> {
            diagnosisConfigData.addSHACLReference(jsonEntry.toString());
        });
        return diagnosisConfigData;
    }

    private void importTherapyConfig(JSONObject rootObject) {
        JSONArray ontConfigurations = (JSONArray) rootObject.get("therapyConfigurations");
        ontConfigurations.forEach(therapyConfig -> {
            therapyConfigDataList.add(this.readTherapyConfigDataElement((JSONObject) therapyConfig));
        });
    }

    private TherapyConfigData readTherapyConfigDataElement(JSONObject therapyConfig) {
        TherapyConfigData therapyConfigData = new TherapyConfigData();
        therapyConfigData.setId(therapyConfig.get("id").toString());
        therapyConfigData.setName(therapyConfig.get("name").toString());
        JSONArray shaclConfigList = (JSONArray) therapyConfig.get("shaclRulesList");
        shaclConfigList.forEach(jsonEntry -> {
            SHACLRuleSet<String> shaclSubruleSet = this.readSHACLConfigEntry((JSONObject) jsonEntry);
            therapyConfigData.addSHACLRuleConfig(shaclSubruleSet);
        });
        return therapyConfigData;
    }

    private SHACLRuleSet readSHACLConfigEntry(JSONObject shaclConfigEntry) {
        SHACLRuleSet<String> shaclRuleSet = new SHACLRuleSet<>();
        shaclRuleSet.setId(shaclConfigEntry.get("id").toString());
        shaclRuleSet.setName(shaclConfigEntry.get("name").toString());
        JSONArray shaclSubrulesList = (JSONArray) shaclConfigEntry.get("shaclSubRules");
        shaclSubrulesList.forEach(jsonEntry -> {
            shaclRuleSet.add(jsonEntry.toString());
        });
        return shaclRuleSet;
    }

}
