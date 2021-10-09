PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>

SELECT ?damage
WHERE {
	?damage rdf:type dot:Damage .
}
