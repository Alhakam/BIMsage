package de.tud.cib.bimsage.gui.configuration.resources.ontology;

import de.tud.cib.bimsage.gui.configuration.resources.ResourceData;

/**
 * Representation of an ontology resource (usually a TBox) that holds relevant data about it.
 */
public class OntologyData extends ResourceData {

    private String prefix;
    private String domainLabel;
    private String namespace;
    private String rootClass;

    public OntologyData() {}

    public OntologyData(String url) {
        super(url);
    }

    @Override
    public String toString() {
        return domainLabel + " (" + prefix + ")";
    }

    /*
    Setter
     */

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setDomainLabel(String domainLabel) {
        this.domainLabel = domainLabel;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setRootClass(String rootClass) {
        this.rootClass = rootClass;
    }

    /*
    Getter
     */

    public String getPrefix() {
        return prefix;
    }

    public String getDomainLabel() {
        return domainLabel;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getRootClass() {
        return rootClass;
    }
}
