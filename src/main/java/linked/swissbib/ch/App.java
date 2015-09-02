package linked.swissbib.ch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class App {

    public static void main (String[] args) {

        Properties properties = new Properties();
        BufferedInputStream stream;
        try {
            stream = new BufferedInputStream(new FileInputStream(args[0]));
            properties.load(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up pipe
        ESBulkWritable esBulkDump;
        if (properties.getProperty("writeToFile").equals("true")) {
            esBulkDump = new ESBulkWriter(
                    Short.parseShort(properties.getProperty("esBulkSize")),
                    Short.parseShort(properties.getProperty("outDirSize")),
                    properties.getProperty("rootOutDir"),
                    properties.getProperty("outFilePrefix"));
        } else {
            esBulkDump = new ESBulkIndexer(
                    properties.getProperty("esNodes").split("#"),
                    properties.getProperty("esClustername"),
                    Short.parseShort(properties.getProperty("esBulkSize")));
        }
        esBulkDump.connect();
        BulkJSONLDWriter jsonldWriter = new BulkJSONLDWriter(
                esBulkDump,
                properties.getProperty("index"));
        GetRdfStatements rdfStatements = new GetRdfStatements(
                properties.getProperty("repoHost"),
                properties.getProperty("repoUser"),
                properties.getProperty("repoPwd"),
                jsonldWriter);

        // Kick off workflow
        for (String type: properties.getProperty("types").split("#")) rdfStatements.getSubjects(type);
        esBulkDump.close();
        rdfStatements.close();

    }
}
