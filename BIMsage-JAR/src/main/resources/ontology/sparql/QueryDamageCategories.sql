PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>
PREFIX nsd: <https://w3id.org/nsd#>
PREFIX sco: <https://www.bim-sis.de/ontology/damage/sco#>

SELECT ?damageClass
WHERE {
	?stone rdf:type sco:StoneComponent .
	?stone dot:hasDamage ?damage .
	?damage rdf:type ?damageClass .
}
