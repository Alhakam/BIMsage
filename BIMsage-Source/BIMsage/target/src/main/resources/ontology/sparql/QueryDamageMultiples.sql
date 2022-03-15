PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>
PREFIX sco: <https://www.bim-sis.de/ontology/damage/sco#>

SELECT ?stone
WHERE {
	?stone rdf:type sco:StoneComponent .
	?stone dot:hasDamage ?damage
}
GROUP BY ?stone
HAVING (COUNT(DISTINCT ?damage) >= 2)