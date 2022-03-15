package de.tud.cib.bimsage.gui.configuration.resources.ontology;

import de.tud.cib.bimsage.gui.configuration.resources.ResourceData;

public class SPARQLData extends ResourceData {

    private String id;

    public SPARQLData() {}

    public SPARQLData(String url) {
        super(url);
    }

    public SPARQLData(String url, String id) {
        super(url);
        this.id = id;
    }

    /*
    Setter
     */

    public void setId(String id) {
        this.id = id;
    }

    /*
    Getter
     */

    public String getId() {
        return id;
    }
}
