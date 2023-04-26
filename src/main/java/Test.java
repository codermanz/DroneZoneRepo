/**
 * Sample code for connecting to gremlin server. Is used in tandem with air routes dataset from the book.
 * Please ignore.
 */

import models.Node;
import models.Operator;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import java.util.*;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

//class Test {
//    public static void main(String[] args) throws Exception {
//        Operator u = new Operator(0, 1);
//
//        // Create Graph traversal object
//        DriverRemoteConnection connection = DriverRemoteConnection.using("localhost", 8182, "g");
//        GraphTraversalSource g = traversal().withRemote(connection);
//
//        // Lists to store things into
//        ArrayList<Vertex> nodesList = new ArrayList<>();
//
//        // Transaction
//        Transaction tx = g.tx();
//        GraphTraversalSource gtx = tx.begin();
//        try {
//            // Create agents for the first time
//            for (int i = 0; i < 4; i++) {
//                // Create node and set properties map
//                Node node = new Node("drone", new HashMap<String, String>());
//                nodesList.add(node);
//                node.setProperties("name", "drone" + Integer.toString(i + 1));
//                // Sanity check
//                System.out.println("For node " + i + " label: " + node.getLabel());
//                System.out.println("\tFor node " + i + " ID: " + node.getId());
//                System.out.println("\tFor node " + i + " properties:" + node.getProperties());
//                System.out.println("\tFor node " + i + " exists in graph: " + node.isExistsInGraph());
//                // Write nodes to graph and add them to vertex list
//                nodesList.add(node.writeNodeToGraph(gtx));
//                System.out.println("\tWritten node to graph: " + node.isExistsInGraph() + " id: " + node.getId());
//            }
//            // Close transaction
//            tx.commit();
//            gtx.close();
//
//        } catch (Exception ex) {
//            tx.rollback();
//            ex.printStackTrace();
//        }
//
//        g.close();
//        connection.close();
//        System.out.println("Finished Running the script");
//    }
//}

class Test{
    public static void main(String[] args) throws Exception {
        GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using("localhost",8182,"g"));
//        GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using("conf/remote-graph.properties"));

        List <Map<Object,Object>> vmaps = g.V().valueMap().toList();

        System.out.println("\n\nThe following airports were found " + vmaps.size() + "\n");
        for (Map <Object,Object> m : vmaps)
        {
            ArrayList code = (ArrayList) m.get("code");
            ArrayList desc = (ArrayList) m.get("desc");
            System.out.println(m);
        }
        System.out.println("Done, exiting");
        g.close();

    }
}