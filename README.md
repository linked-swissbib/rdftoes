# rdftoes
Queries a SPARQL endpoint and indexes results in an Elasticsearch cluster

## Getting started
1. Clone repository
2. Set your connection settings (among others) in a properties file. You find an example in src/main/resources
3. Set a path to the log file in src/main/resources/log4j2.xml
3. Execute maven package goal:

        mvn package
4. Start jar. In order to load the properties file indicate the path in the command line. If you don't set a properties
file you have to define temporary settings.

CAVEAT
In order to work properly you must disable a possible query result row limit in your SPARQL endpoint (e.g. in Virtuoso:
ResultSetMaxRows = 0).
