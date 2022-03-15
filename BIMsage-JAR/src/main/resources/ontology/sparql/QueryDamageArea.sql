PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>
PREFIX nsd: <https://w3id.org/nsd#>
PREFIX props: <https://w3id.org/product/props#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX schema: <https://schema.org/>

SELECT ?damageArea ?damageAreaValue ?stoneAreaValue
WHERE
{
    ?damageArea rdf:type dot:DamageArea .
    ?damageArea dot:aggregatesDamageElement ?damage .
	?damage rdf:type dot:Damage .
    ?damageArea props:area ?damageAreaValue .
    ?damageArea props:stoneArea ?stoneAreaValue .
    FILTER (regex(str(?damageArea), "damageAreaID")) .
}