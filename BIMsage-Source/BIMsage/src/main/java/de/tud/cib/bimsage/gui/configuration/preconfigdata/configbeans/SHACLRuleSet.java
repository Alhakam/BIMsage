package de.tud.cib.bimsage.gui.configuration.preconfigdata.configbeans;

import java.util.LinkedHashSet;

public class SHACLRuleSet<S> extends LinkedHashSet {

    private String id;
    private String name;

    public SHACLRuleSet() {
        super();
    }

    public SHACLRuleSet(String id, String name) {
        super();
        this.id = id;
        this.name = name;
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
