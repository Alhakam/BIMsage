PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX nsd: <https://w3id.org/nsd#>
PREFIX sco: <https://www.bim-sis.de/ontology/damage/sco#>
PREFIX props: <https://w3id.org/product/props#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX schema: <https://schema.org/>

SELECT (SUM(?stoneAreaValue) AS ?stoneAreaSum)
WHERE
{
    ?stone rdf:type sco:Stone .
    ?stone props:area ?stoneAreaValue .
}
