package linked.swissbib.ch;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import virtuoso.sesame2.driver.*;


public class Getrdf {

    public static Graph getData(String repoUrl, String repoUser, String repoPwd, short objectsNo) {

        Repository repo = new VirtuosoRepository(repoUrl, repoUser, repoPwd);

        Graph g = new TreeModel();

        try {
            repo.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        try {
            RepositoryConnection con = repo.getConnection();

            try {

                // Todo: ORDER BY doesn't work (neither on the web console)...
                String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 5";

                // TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                // TupleQueryResult result = tupleQuery.evaluate();
                GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
                GraphQueryResult result = graphQuery.evaluate();

                try {
                    while (result.hasNext()) {
                        Statement s = result.next();
                        // BindingSet bindingSet = result.next();
                        // Just for testing
                        g.add(s);
                    }
                }
                finally {
                    result.close();
                }

            } catch (MalformedQueryException e) {
                e.printStackTrace();
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return g;

    }

}
