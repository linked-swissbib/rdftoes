package linked.swissbib.ch;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import virtuoso.sesame2.driver.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Get rdf statements from a remote Virtuoso server.
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class GetRdfStatements {

    public static void getData(String repoUrl, String repoUser, String repoPwd, short objectsNo) {

        Graph g;
        Repository repo = new VirtuosoRepository(repoUrl, repoUser, repoPwd);

        try {
            repo.initialize();
            RepositoryConnection con = repo.getConnection();

            try {

                String querySubjects = "SELECT DISTINCT ?s " +
                        "WHERE {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " +
                        "<http://purl.org/dc/terms/BibliographicResource> }";
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, querySubjects);
                TupleQueryResult r = tupleQuery.evaluate();
                Set<String> subjects = new HashSet<>();
                short i = 0;
                try {
                    while (r.hasNext()) {
                        if (i < objectsNo) {
                            subjects.add(r.next().getValue("s").stringValue());
                            i += 1;
                        } else {
                            g = GetRdfStatements.getStatements(subjects, con);
                            GetRdfStatements.printJsonLdBulk(g);
                            subjects.clear();
                            i = 0;
                        }
                    }
                }
                finally {
                    g = GetRdfStatements.getStatements(subjects, con);
                    GetRdfStatements.printJsonLdBulk(g);
                    r.close();
                }
            } catch (MalformedQueryException | QueryEvaluationException e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }


    public static Graph getStatements(Set<String> subjects, RepositoryConnection con) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        Graph g = new TreeModel();
        for (String s: subjects) {
            String query = "SELECT ?s ?p ?o WHERE { ?s ?p ?o. FILTER (?s = <" + s + ">)}";
            GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, query);
            GraphQueryResult r = graphQuery.evaluate();
            while (r.hasNext()) {
                Statement st = r.next();
                g.add(st);
            }
            r.close();
        }
        return g;
    }


    public static void printJsonLdBulk(Graph g) {
        RDFWriter writer = new BulkJSONLDWriter(System.out);
        try {
            writer.startRDF();
            for (Statement st : g) {
                writer.handleStatement(st);
            }
            writer.endRDF();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        }
        writer.toString();
    }

}
