package linked.swissbib.ch;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.rio.*;
import org.openrdf.rio.jsonld.JSONLDWriter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Collection;

/**
 * Class implements a JSON-LD Bulk writer
 * @author fxbensmann, Gesis, Köln
 */
public class BulkJSONLDWriter implements RDFWriter {

    private JSONLDWriter jsonldWriter=null;
    private StringWriter stringWriter = null;
    private Resource lastSubject = null;
    private PrintStream out = null;
    private Graph graph  = null;

    public BulkJSONLDWriter(OutputStream out) {
        stringWriter = new StringWriter();
        jsonldWriter = new JSONLDWriter(stringWriter); 
        this.out = new PrintStream(out);
        graph = new TreeModel();
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        out.println("[");
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        if(!graph.isEmpty()){
            out.println("{ This is an additional line }");
            out.println(serializeResource(graph));
        }
        out.println("]");
        graph.clear();
        lastSubject=null;
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        if (lastSubject == null) {
            lastSubject = st.getSubject();
        }
        if (lastSubject.equals(st.getSubject())) {
            graph.add(st);
            lastSubject=st.getSubject();
        }
        else {
            out.println("{ This is an additional line }");
            out.println( serializeResource(graph) );
            graph.clear();
            graph.add(st);
            lastSubject=st.getSubject();
        }

    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    
    
    public String serializeResource(Graph graph) throws RDFHandlerException{
        jsonldWriter.startRDF();
        for(Statement st : graph){
            jsonldWriter.handleStatement(st);
        }
        jsonldWriter.endRDF();
        String str = stringWriter.getBuffer().toString();
        stringWriter.getBuffer().setLength(0);
        return str.substring(1, str.length()-2);
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
