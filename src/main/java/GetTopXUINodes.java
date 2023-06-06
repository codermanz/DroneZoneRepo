import org.apache.commons.lang.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import utils.ConfigClasses.Connection;

import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.desc;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inV;

public class GetTopXUINodes {

    public static final boolean ASC = true;
    public static final boolean DESC = false;

    public static void main(String[] args) {

        GraphTraversalSource graph;
        Connection connection = new Connection();
        graph = connection.createConnection();

        Transaction tx = graph.tx();
        GraphTraversalSource gtx = tx.begin();

        HashMap<Long, Double> listOfUINodeIDsToWeights = new HashMap<>();
        List<Long> topRelevantNodes = new ArrayList<>();

        try {
            GraphTraversal<Vertex, Vertex> uIComponents = gtx.V().hasLabel("ui-component");

            while (uIComponents.hasNext()) {

                Vertex uiComponent = uIComponents.next();

                List<Edge> incomingEdges = gtx.V(uiComponent).inE().toList();

                Double cumulativeWeight = 0.0;

                for (Edge edge : incomingEdges) {
                    cumulativeWeight += Double.parseDouble(gtx.E(edge.id()).valueMap().next().get("gewicht").toString().replaceAll("[a-zA-Z]", ""));
                }

                List<Vertex> temp = gtx.V(uiComponent).inE().order().by("gewicht", desc).limit(2).outV().toList();
                for (Vertex v : temp)
                    topRelevantNodes.add((Long) v.id());

                listOfUINodeIDsToWeights.put((Long) uiComponent.id(), cumulativeWeight);

            }
            tx.commit();
            gtx.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            tx.rollback();
        }

        Map<Long, Double> sortedList = sortByComparator(listOfUINodeIDsToWeights, DESC);

        System.out.println("g.V(" + (Arrays.toString(ArrayUtils.addAll(sortedList.keySet().stream().limit(5).toArray(), topRelevantNodes.toArray())) + ")"));
        System.out.println("\n\n\n\n");
        System.out.println(sortedList);


    }


    private static Map<Long, Double> sortByComparator(Map<Long, Double> unsortMap, final boolean order)
    {

        List<Map.Entry<Long, Double>> list = new LinkedList<Map.Entry<Long, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Long, Double>>()
        {
            public int compare(Map.Entry<Long, Double> o1,
                               Map.Entry<Long, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<Long, Double> sortedMap = new LinkedHashMap<Long, Double>();
        for (Map.Entry<Long, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
