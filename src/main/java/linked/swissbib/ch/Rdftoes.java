package linked.swissbib.ch;

import org.openrdf.model.Graph;
import org.gesis.esbulktest.BulkJSONLDWriter;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

public class Rdftoes {


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

        Graph rdfGraph = Getrdf.getData(repoHost, repoUser, repoPwd, bulkSize);

        RDFWriter writer = new BulkJSONLDWriter(System.out);
        try {
            writer.startRDF();
            for (Statement st : rdfGraph) {
                writer.handleStatement(st);
            }
            writer.endRDF();
        } catch (RDFHandlerException e) {
            System.err.println(e);
        }

        writer.toString();
    }
}
