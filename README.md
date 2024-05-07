# RuDiK Rule Miner Docker Setup
This repository contains the Docker Compose setup necessary to run the RuDiK rule miner. 
[RuDiK](https://github.com/stefano-ortona/rudik/tree/master) is an rule mining system that uses a knowledge graph to discover positive and negative rules from RDF data. 

This Docker setup simplifies the process of deploying and running RuDiK in a containerized environment.

## Prerequisites
Before you begin, ensure you have the following installed on your system:
- Docker
- Docker Compose

## Installation
Run the following command to build the Docker images specified in the docker-compose.yml file:
docker-compose build

## Running
docker-compose up
