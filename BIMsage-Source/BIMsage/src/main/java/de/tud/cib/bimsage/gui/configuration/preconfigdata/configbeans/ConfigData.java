package de.tud.cib.bimsage.gui.configuration.preconfigdata.configbeans;

/*
Representation of a config file that holds relevant data about it.
 */
public class ConfigData {

    protected String id;
    protected String name;

    /*
    Setter
     */

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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
}
