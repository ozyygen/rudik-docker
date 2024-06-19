# Rudik Dockerisation
This guide is to help you set up and run the Rudik project (https://github.com/stefano-ortona/rudik) using Docker Compose. The setup involves:

**ontotext**: A GraphDB service.

**rudik**: The Rudik service that runs the rule mining application.

## Prerequisites
Docker installed on your machine.

Docker Compose installed on your machine.

## Running the Setup
1- Build the rudik image:

```
docker-compose build rudik
```

2- Start the services:

```
docker-compose up
```

## Explanation
**ontotext**: Runs GraphDB and exposes it on port 7200. After the build, you should create a new repository on GraphDB and upload your RDF graph. Afterwards, you should provide it's SPAQRL endpoint to the related field in rudik/Configuration.xml. For SPAQRL endpoint URI, you can inspect GRAPHDB SPAQRL query page after you run a query. You may find it during the post request.

**rudik**: The main Rudik service.

## Additional Notes
- Rudik runs manually in Docker. To discover the rules you should run below code in the rudik image:

```
java -cp target/rule_miner-0.0.1-SNAPSHOT.jar asu.edu.rule_miner.rudik.App
```

- Target relation and subject & object types are hard-coded in App.java. Please change them accordingly.



