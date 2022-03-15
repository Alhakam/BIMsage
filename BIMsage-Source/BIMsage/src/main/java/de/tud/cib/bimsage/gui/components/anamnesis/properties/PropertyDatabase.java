package de.tud.cib.bimsage.gui.components.anamnesis.properties;

import java.util.LinkedHashSet;

/**
 * Database that holds predefined properties
 */
//TODO: Später Methode hinzufügen, die Datenbank aus persistentem File liest (init der Datenbank).
public class PropertyDatabase {

    private LinkedHashSet<OntologyProperty> constructionPropertySet;

    private static PropertyDatabase ourInstance = new PropertyDatabase();

    public static PropertyDatabase getInstance() {
        return ourInstance;
    }

    private PropertyDatabase() {
        this.constructionPropertySet = new LinkedHashSet<>();
    }

    /*
    Setter
     */

    public void setConstructionPropertySet(LinkedHashSet<OntologyProperty> constructionPropertySet) {
        this.constructionPropertySet = constructionPropertySet;
    }

    public void addConstructionProperty(OntologyProperty ontologyProperty) {
        this.constructionPropertySet.add(ontologyProperty);
    }

    /*
    Getter
     */

    public LinkedHashSet<OntologyProperty> getConstructionPropertySet() {
        return constructionPropertySet;
    }

    public OntologyProperty getPropertyByLabel(String label) {
        LinkedHashSet<OntologyProperty> allProperties = new LinkedHashSet<>();  //serves as container for future propertysets
        allProperties.addAll(constructionPropertySet);
        for (OntologyProperty property :
                allProperties) {
            if (property.getLabel().equals(label))
                return property;
        }
        return null;
    }
}
