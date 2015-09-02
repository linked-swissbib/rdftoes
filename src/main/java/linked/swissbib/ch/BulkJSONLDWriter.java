package linked.swissbib.ch;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.openrdf.rio.jsonld.JSONLDWriter;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implements a JSON-LD Bulk writer
 * @author fxbensmann, Gesis, Köln
 * @author Sebastian Schüpbach, project swissbib. Basel
 */
public class BulkJSONLDWriter implements RDFWriter {

    private final Logger logger = Logger.getLogger("global");

    private JSONLDWriter jsonldWriter=null;
    private StringWriter stringWriter = null;
    private ESBulkWritable out = null;
    private String header = null;
    private String index;
    Map<String, String> m = new HashMap<>();

    public BulkJSONLDWriter(ESBulkWritable out, String index) {
        this.index = index;
        stringWriter = new StringWriter();
        jsonldWriter = new JSONLDWriter(stringWriter);
        this.out = out;
        m.put("bibo", "http://purl.org/ontology/bibo/");
        m.put("dbp", "http://dbpedia.org/ontology/");
        m.put("dc", "http://purl.org/dc/elements/1.1/");
        m.put("dct", "http://purl.org/dc/terms/");
        m.put("foaf", "http://xmlns.com/foaf/0.1/");
        m.put("gnd", "http://d-nb.info/standards/elementset/gnd#");
        m.put("owl", "http://www.w3.org/2002/07/owl#");
        m.put("rdac", "http://rdaregistry.info/Elements/c/");
        m.put("rdai", "http://rdaregistry.info/Elements/i/");
        m.put("rdam", "http://rdaregistry.info/Elements/m/");
        m.put("rdau", "http://rdaregistry.info/Elements/u/");
        m.put("rdaw", "http://rdaregistry.info/Elements/w/");
        m.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        m.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        m.put("schema", "https://schema.org/");
        m.put("skos", "http://www.w3.org/2004/02/skos/core#");
        m.put("void", "http://rdfs.org/ns/void#");
    }


    public void headerSettings(String type, String id) {
        this.header = "{\"index\":{\"_type\":\"" + type + "\",\"_index\":\"" + this.index + "\",\"_id\":\"" + id + "\"}}\n";
    }


    @Override
    public void startRDF() throws RDFHandlerException {
        jsonldWriter.startRDF();
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        jsonldWriter.endRDF();
        String str = stringWriter.getBuffer().toString();
        try {
            Object compact = JsonLdProcessor.compact(JsonUtils.fromString(str.substring(1, str.length() - 2)), m, new JsonLdOptions());
            out.write(this.header + JsonUtils.toString(compact) + "\n");
        } catch (JsonLdError | IOException e) {
            logger.error(e.getStackTrace());
        }
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        jsonldWriter.handleStatement(st);
    }


    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    

    @Override
    public RDFFormat getRDFFormat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWriterConfig(WriterConfig config) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WriterConfig getWriterConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

}
