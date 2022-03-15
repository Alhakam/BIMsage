PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>
PREFIX nsd: <https://w3id.org/nsd#>
PREFIX sco: <https://www.bim-sis.de/ontology/damage/sco#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

SELECT ?stone ?damageImpactValue ?materialLossValue
WHERE {
    ?stone rdf:type sco:StoneComponent .
	?stone dot:hasDamageArea ?damageArea .
	?stone dot:hasDamage ?damage .
	?damage rdf:type dot:Damage .
    ?damage nsd:damageImpact ?damageImpactValue .
    ?damage nsd:materialLoss ?materialLossValue .
    FILTER (regex(str(?damageArea), "damageAreaID")) .
}
