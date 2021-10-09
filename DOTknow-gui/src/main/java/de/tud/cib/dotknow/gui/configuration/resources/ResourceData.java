package de.tud.cib.dotknow.gui.configuration.resources;

/*
Representation of a resource that holds relevant data about it.
 */
public class ResourceData {

    private String url;

    public ResourceData() {}

    public ResourceData(String url) {
        this.url = url;
    }

    /*
    Setter
     */

    public void setUrl(String url) {
        this.url = url;
    }

    /*
    Getter
     */

    public String getUrl() {
        return url;
    }
}
