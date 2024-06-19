from rdflib import Graph

# Load the RDF graph
g = Graph()
g.parse("/Users/ozgeerten/Downloads/vos-docker-bulkload-example/data/output-2-wikiPer.ttl", format="ttl")

# Define the SPARQL query
query = """
PREFIX wdt: <http://www.wikidata.org/prop/direct/>
PREFIX wd: <http://www.wikidata.org/entity/>
SELECT ?subject
WHERE {
  ?subject wdt:P31 wd:Q5 .
} limit 1
"""

# Execute the SPARQL query
results = g.query(query)

# Print the result
for row in results:
    print(f"Count of P31 Q5 instances: {row}")
