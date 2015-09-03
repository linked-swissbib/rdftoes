# rdftoes
Queries a SPARQL endpoint and indexes results in an Elasticsearch cluster

## Getting started
1. Clone repository
2. Set your connection settings (among others) in a properties file. You find an example in src/main/resources
3. Execute maven package goal:

        mvn package
4. Start jar. In order to load the properties file indicate the path in the command line. If you don't set a properties
file you have to define temporary settings.
