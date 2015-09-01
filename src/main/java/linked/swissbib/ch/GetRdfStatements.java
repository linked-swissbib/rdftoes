package linked.swissbib.ch;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.rio.RDFHandlerException;

import java.util.HashMap;
import java.util.Map;

/**
 * Get rdf statements from a remote Virtuoso server.
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class GetRdfStatements {

    SPARQLRepository repo;
    RepositoryConnection con;
    String type;
    String id = null;
    BulkJSONLDWriter jsonldWriter;


    GetRdfStatements(String repoUrl, String repoUser, String repoPwd, BulkJSONLDWriter jsonldWriter) {
        this.jsonldWriter = jsonldWriter;
        repo = new SPARQLRepository(repoUrl);
        repo.setUsernameAndPassword(repoUser, repoPwd);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        repo.setHttpClient(httpClient);
        try {
            repo.initialize();
            con = repo.getConnection();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }


    public void getSubjects(String type) {
        this.type = type;
        try {
            TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SPARQL, GetRdfStatements.queryBuilder(type));
            TupleQueryResult r = tupleQuery.evaluate();
            while (r.hasNext()) {
                String subject = r.next().getValue("s").stringValue();
                this.id = subject.substring(subject.lastIndexOf("/") + 1);
                this.getSubjectStatements(subject);
            }
            r.close();
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
        }
    }


    public static String queryBuilder(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("bibliographicResource", "http://purl.org/dc/terms/BibliographicResource");
        m.put("document", "http://purl.org/ontology/bibo/Document");
        m.put("person", "http://xmlns.com/foaf/0.1/Person");
        m.put("organization", "http://xmlns.com/foaf/0.1/Organization");
        m.put("work", "http://bibframe.org/vocab/Work");
        m.put("item", "http://bibframe.org/vocab/HeldItem");
        return "SELECT DISTINCT ?s WHERE {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + m.get(type) + "> }";
    }


    public void getSubjectStatements(String subject) {
        String query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o. FILTER (?s = <" + subject + ">)}";
        GraphQuery graphQuery;
        try {
            graphQuery = this.con.prepareGraphQuery(QueryLanguage.SPARQL, query);
            GraphQueryResult r = graphQuery.evaluate();
            this.jsonldWriter.headerSettings(this.type, this.id);
            this.jsonldWriter.startRDF();
            while (r.hasNext()) this.jsonldWriter.handleStatement(r.next());
            this.jsonldWriter.endRDF();
            r.close();
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException | RDFHandlerException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            con.close();
            repo.shutDown();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

}
