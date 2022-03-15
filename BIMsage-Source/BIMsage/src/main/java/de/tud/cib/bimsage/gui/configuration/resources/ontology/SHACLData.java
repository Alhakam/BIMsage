package de.tud.cib.bimsage.gui.configuration.resources.ontology;

import de.tud.cib.bimsage.gui.configuration.resources.ResourceData;

public class SHACLData extends ResourceData {

    private String id;
    private String name;
    private boolean validating;

    public SHACLData() {}

    public SHACLData(String url) {
        super(url);
    }

    public SHACLData(String id, String name, boolean validating) {
        this.id = id;
        this.name = name;
        this.validating = validating;
    }

    @Override
    public String toString() {
        return name;
    }

    /*
    Setter
     */

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    /*
    Getter
     */

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isValidating() {
        return validating;
    }
}
