package linked.swissbib.ch;

/**
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class App {


    // 1. Get x subjects with all attributed statements
    // (we should probably define a count in order to know when to stop...)
    // 2. Serialize them to JSON-LD
    // 3. Wrap JSON-LD to be compliant with Bulk API
    // 4. Index the String in ES

    public static void main (String[] args) {

        // Add required connection details
        //String repoHost = "";
        String repoHost = "";
        String repoUser = "";
        String repoPwd = "";

        short bulkSize = 2000;

        GetRdfStatements.getData(repoHost, repoUser, repoPwd, bulkSize);

    }
}
