package linked.swissbib.ch;

import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import virtuoso.sesame2.driver.*;


public class Getrdf {

    public static void getData(String repoUrl, String repoUser, String repoPwd, short objectsNo) {

        Repository repo = new VirtuosoRepository(repoUrl, repoUser, repoPwd);

        try {
            repo.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        try {
            RepositoryConnection con = repo.getConnection();

            try {

                String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o } ORDER BY ?s LIMIT 10";

                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = tupleQuery.evaluate();

                try {
                    while (result.hasNext()) {
                        BindingSet bindingSet = result.next();
                        // Just for testing
                        Value valueOfX = bindingSet.getValue("s");
                        Value valueOfY = bindingSet.getValue("p");
                        Value valueOfZ = bindingSet.getValue("o");
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

    }

}
