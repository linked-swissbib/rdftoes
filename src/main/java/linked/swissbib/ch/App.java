package linked.swissbib.ch;


/**
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class App {

    public static void main (String[] args) {

        // Connection details for Virtuoso server
        String repoHost = "jdbc:virtuoso://sb-ls1.swissbib.unibas.ch:1111";
        String repoUser = "swissbib";
        String repoPwd = "12swissbib34";

        // Connection details for Elasticsearch cluster
        String[] esNodes =
                {"localhost:9300", "localhost:9301", "localhost:9302"};
        String esClustername = "linked-swissbib";
        String index = "testsb3";
        String[] types = {"bibliographicResource"};
        short esBulkSize = 1;

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
