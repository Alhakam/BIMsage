package de.tud.dotknow.ontology.namespaces.tbox;

import de.tud.dotknow.ontology.namespaces.OntologyNamespaces;

public final class BOT {

    private BOT() {}

    private final static String botUri = OntologyNamespaces.BOT_URI;

    /*
    Classes
     */

    public final static String ELEMENT = botUri + "Element";
    public final static String BUILDING = botUri + "Building";
    public final static String SITE = botUri + "Site";

    /*
    Properties
     */

    public final static String HAS_ELEMENT = botUri + "hasElement";
    public final static String HAS_SUB_ELEMENT = botUri + "hasSubElement";
    public final static String CONTAINS_ZONE = botUri + "containsZone";

}
