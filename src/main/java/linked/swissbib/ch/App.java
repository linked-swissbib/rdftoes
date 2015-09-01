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
        // String[] esNodes = {""};
        // String esClustername = "";
        String index = "";
        String[] types = {"bibliographicResource", "document", "person", "organization", "work", "item"};
        short esBulkSize = 100;
        short outDirSize = 300;
        String rootOutDir = "";
        String outFilePrefix = "esbulk";


        // Set up pipe
        // ESBulkWritable esIndex = new ESBulkIndexer(esNodes, esClustername, esBulkSize);
        ESBulkWritable esIndex = new ESBulkWriter(esBulkSize, outDirSize, rootOutDir, outFilePrefix);
        esIndex.connect();
        BulkJSONLDWriter jsonldWriter = new BulkJSONLDWriter(esIndex, index);
        GetRdfStatements rdfStatements = new GetRdfStatements(repoHost, repoUser, repoPwd, jsonldWriter);

        // Kick off workflow
        for (String type: types) rdfStatements.getSubjects(type);
        esIndex.close();
        rdfStatements.close();

    }
}
