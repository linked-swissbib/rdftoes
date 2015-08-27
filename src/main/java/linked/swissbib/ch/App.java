package linked.swissbib.ch;


/**
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class App {

    public static void main (String[] args) {

        // Connection details for Virtuoso server
        String repoHost = "";
        String repoUser = "";
        String repoPwd = "";

        // Connection details for Elasticsearch cluster
        String[] esNodes = {""};
        String esClustername = "";
        String index = "";
        String[] types = {""};
        short esBulkSize = 100;

        // Set up pipe
        ESBulkIndexer esIndex = new ESBulkIndexer(esNodes, esClustername, esBulkSize);
        BulkJSONLDWriter jsonldWriter = new BulkJSONLDWriter(esIndex, index);
        GetRdfStatements rdfStatements = new GetRdfStatements(repoHost, repoUser, repoPwd, jsonldWriter);

        // Kick off workflow
        for (String type: types) rdfStatements.getSubjects(type);
        esIndex.close();
        rdfStatements.close();

    }
}
