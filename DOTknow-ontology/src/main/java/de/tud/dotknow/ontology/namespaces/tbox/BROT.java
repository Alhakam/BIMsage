package de.tud.dotknow.ontology.namespaces.tbox;

import de.tud.dotknow.ontology.namespaces.OntologyNamespaces;

public final class BROT {

    private BROT() {}

    private final static String brotUri = OntologyNamespaces.BROT_URI;

    /*
    Classes
     */

    public final static String COMPONENT = brotUri + "Component";
    public final static String BRIDGE = brotUri + "Bridge";
    public final static String SITE = brotUri + "Site";

    /*
    Properties
     */

    public final static String AGGREGATES = brotUri + "aggregates";
    public final static String CONTAINS_COMPONENT = brotUri + "containsComponent";
    public final static String CONTAINS_ZONE = brotUri + "containsZone";
}
