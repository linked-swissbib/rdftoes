package linked.swissbib.ch;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.rio.RDFHandlerException;

import java.util.*;

/**
 * Get rdf statements from a remote Virtuoso server.
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class GetRdfStatements {

    private final Logger logger = LogManager.getLogger("global");

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
            logger.info("Connecting to repository " + repoUrl);
        } catch (RepositoryException e) {
            logger.error(e.getStackTrace());
        }

    }


    public void getSubjects(String type) {
        this.type = type;
        try {
            TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SPARQL, GetRdfStatements.queryBuilder(type));
            TupleQueryResult r = tupleQuery.evaluate();
            while (r.hasNext()) {
                String subject = r.next().getValue("s").stringValue();
                String subjectTrimmed = subject.replace("/about", "");
                this.id = subjectTrimmed.substring(subjectTrimmed.lastIndexOf("/") + 1);
                this.getSubjectStatements(subject);
            }
            r.close();
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            logger.error(e.getStackTrace());
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
        TupleQuery tupleQuery;
        BindingSet bs;
        try {
            tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult r = tupleQuery.evaluate();
            this.jsonldWriter.headerSettings(this.type, this.id);
            this.jsonldWriter.startRDF();
            while (r.hasNext()) {
                bs = r.next();
                Resource s = new URIImpl(bs.getValue("s").stringValue());
                URI p = new URIImpl(bs.getValue("p").stringValue());
                Statement st = new StatementImpl(s, p, bs.getValue("o"));
                this.jsonldWriter.handleStatement(st);
            }
            this.jsonldWriter.endRDF();
            r.close();
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException | RDFHandlerException e) {
            logger.error(e.getMessage() + "\nQuery: " + query);
        }
    }


    public void close() {
        try {
            con.close();
            repo.shutDown();
        } catch (RepositoryException e) {
            logger.error(e.getStackTrace());
        }
    }

}
