PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>
PREFIX srmo: <https://www.bim-sis.de/ontology/damage/srmo#>

SELECT ?stone ?renovationMeasure
WHERE
{
    ?stone dot:hasDamage ?damage .
    ?damage srmo:recommendedRenovationMeasure ?renovationMeasure
}