package de.tud.cib.bimsage.ontology.namespaces.tbox;

import de.tud.cib.bimsage.ontology.namespaces.OntologyNamespaces;

public final class DOT {

    private DOT() {
    }

    private final static String dotUri = OntologyNamespaces.DOT_URI;

    /*
    Classes
     */

    public final static String DAMAGE_ELEMENT = dotUri + "DamageElement";
    public final static String DAMAGE_AREA = dotUri + "DamageArea";

    /*
    Properties
     */

    public final static String HAS_DAMAGE_ELEMENT = dotUri + "hasDamageElement";
    public final static String HAS_DAMAGE_AREA = dotUri + "hasDamageArea";
    public final static String AGGREGATES_DAMAGE_ELEMENT = dotUri + "aggregatesDamageElement";

}
