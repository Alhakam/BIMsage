PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX srmo: <https://www.bim-sis.de/ontology/damage/srmo#>

SELECT ?renovationClass
WHERE {
	srmo:renovationName rdfs:subClassOf ?renovationClass .
}
