PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dot: <https://w3id.org/dot#>
PREFIX nsd: <https://w3id.org/nsd#>
PREFIX props: <https://w3id.org/product/props#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX schema: <https://schema.org/>

SELECT ?damageArea ?damageAreaValue ?damageImpact
WHERE
{
    ?damageArea rdf:type dot:DamageArea .
    ?damageArea dot:aggregatesDamageElement ?damageElement .
    ?damageElement rdf:type dot:Damage .    # dot:Damage is exchangable with specific damage type
    ?damageArea props:area ?damageAreaValue .
    ?damageElement nsd:damageImpact ?damageImpact
}