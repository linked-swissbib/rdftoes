package linked.swissbib.ch;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class App {

    private static final Logger logger = Logger.getLogger("global");

    public static void main (String[] args) {

        Properties properties = new Properties();
        BufferedInputStream stream;
        try {
            if (args[0] == null) {
                logger.info("No setting file indicated.");
                properties = null;
            } else {
                stream = new BufferedInputStream(new FileInputStream(args[0]));
                properties.load(stream);
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up pipe
        ESBulkWritable esBulkDump;
        if (getSetting(properties, "writeToFile").equals("true")) {
            esBulkDump = new ESBulkWriter(
                    Short.parseShort(getSetting(properties, "esBulkSize")),
                    Short.parseShort(getSetting(properties, "outDirSize")),
                    getSetting(properties, "rootOutDir"),
                    getSetting(properties, "outFilePrefix"));
        } else {
            esBulkDump = new ESBulkIndexer(
                    getSetting(properties, "esNodes").split("#"),
                    getSetting(properties, "esClustername"),
                    Short.parseShort(getSetting(properties, "esBulkSize")));
        }
        esBulkDump.connect();
        BulkJSONLDWriter jsonldWriter = new BulkJSONLDWriter(
                esBulkDump,
                getSetting(properties, "index"));
        GetRdfStatements rdfStatements = new GetRdfStatements(
                getSetting(properties, "repoHost"),
                getSetting(properties, "repoUser"),
                getSetting(properties, "repoPwd"),
                jsonldWriter);

        // Kick off workflow
        for (String type: getSetting(properties, "types").split("#")) rdfStatements.getSubjects(type);
        esBulkDump.close();
        rdfStatements.close();

    }

    public static String getSetting(Properties properties, String val) {
        String result;
        if (properties == null || !(properties.containsKey(val))) {
            Scanner scanner = new Scanner(System.in);
            logger.info("Setting " + val + " is not set and has to be added manually.");
            System.out.println("Setting " + val + " is not set. Please define a temporary value.\n" + val + " = ");
            result = scanner.next();
        } else {
            result = properties.getProperty(val);
        }
        return result;
    }
}
