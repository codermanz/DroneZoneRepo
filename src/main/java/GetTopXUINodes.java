import org.apache.commons.lang.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.json.simple.parser.ParseException;
import utils.ConfigClasses.Connection;
import static org.apache.tinkerpop.gremlin.process.traversal.Order.desc;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.desc;

public class GetTopXUINodes {
    public static final boolean ASC = true;
    public static final boolean DESC = false;


    public static String getTopNodes(int timestep){
        GraphTraversalSource graph;
        Connection connection = new Connection();
        graph = connection.createConnection();

        Transaction tx = graph.tx();
        GraphTraversalSource gtx = tx.begin();
        List<Long> topRelevantNodes = new ArrayList<>();
        HashMap<Long, Double> listOfUINodeIDsToWeights = new HashMap<>();

        try {
            // all components
            GraphTraversal<Vertex, Vertex> ui_zone = gtx.V().hasLabel("ui-component").has("name", "Zone");
            GraphTraversal<Vertex, Vertex> ui_minimap = gtx.V().hasLabel("ui-component").has("name", "Minimap");
            GraphTraversal<Vertex, Vertex> ui_dashboard = gtx.V().hasLabel("ui-component").has("name", "Dashboard");
            GraphTraversal<Vertex, Vertex> ui_logbook = gtx.V().hasLabel("ui-component").has("name", "Logbook");
            GraphTraversal<Vertex, Vertex> ui_droneStatus = gtx.V().hasLabel("ui-component").has("name", "Drone Status");
            GraphTraversal<Vertex, Vertex> ui_urgentAction = gtx.V().hasLabel("ui-component").has("name", "Urgent Action");
            GraphTraversal<Vertex, Vertex> ui_button1 = gtx.V().hasLabel("ui-component").has("name", "Button").has("signalType", "start_action");
            GraphTraversal<Vertex, Vertex> ui_button2 = gtx.V().hasLabel("ui-component").has("name", "Button").has("signalType", "stop_action");
            GraphTraversal<Vertex, Vertex> ui_button3 = gtx.V().hasLabel("ui-component").has("name", "Button").has("signalType", "alter_action");
            GraphTraversal<Vertex, Vertex> ui_dragDrones = gtx.V().hasLabel("ui-component").has("name", "Drag Drones");

            // create lists for each ts
            ArrayList<GraphTraversal<Vertex, Vertex>> t1 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t2 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t3 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t4 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t5 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t6 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t7 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t8 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t9 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t10 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t11 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t12 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t13 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t14 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t15 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t16 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t17 = new ArrayList<>();
            ArrayList<GraphTraversal<Vertex, Vertex>> t18 = new ArrayList<>();

            // create Map for timesteps
            HashMap<Integer, ArrayList<GraphTraversal<Vertex, Vertex>>> timesteps = new HashMap<Integer, ArrayList<GraphTraversal<Vertex, Vertex>>>();

            // fill lists and map lists to timesteps
            t1.add(ui_zone);
            t1.add(ui_dragDrones);
            t1.add(ui_dashboard);
            t1.add(ui_logbook);
            t1.add(ui_droneStatus);
            timesteps.put(1, t1);

            t2.add(ui_zone);
            t2.add(ui_minimap);
            t2.add(ui_button1);
            t2.add(ui_button2);
            t2.add(ui_button3);
            timesteps.put(2, t2);

            t3.add(ui_zone);
            t3.add(ui_minimap);
            t3.add(ui_dashboard);
            t3.add(ui_droneStatus);
            t3.add(ui_logbook);
            timesteps.put(3, t3);

            t4.add(ui_zone);
            t4.add(ui_minimap);
            t4.add(ui_dashboard);
            t4.add(ui_droneStatus);
            t4.add(ui_logbook);
            timesteps.put(4, t4);

            t5.add(ui_urgentAction);
            t5.add(ui_minimap);
            t5.add(ui_dashboard);
            t5.add(ui_droneStatus);
            t5.add(ui_logbook);
            timesteps.put(5, t5);

            t6.add(ui_zone);
            t6.add(ui_minimap);
            t6.add(ui_dashboard);
            t6.add(ui_droneStatus);
            t6.add(ui_logbook);
            timesteps.put(6, t6);

            t7.add(ui_urgentAction);
            t7.add(ui_minimap);
            t7.add(ui_button1);
            t7.add(ui_droneStatus);
            t7.add(ui_button2);
            timesteps.put(7, t7);

            t8.add(ui_zone);
            t8.add(ui_logbook);
            t8.add(ui_button1);
            t8.add(ui_droneStatus);
            t8.add(ui_dashboard);
            timesteps.put(8, t8);

            t9.add(ui_droneStatus);
            t9.add(ui_minimap);
            t9.add(ui_button1);
            t9.add(ui_zone);
            t9.add(ui_dashboard);
            timesteps.put(9, t9);

            t10.add(ui_droneStatus);
            t10.add(ui_minimap);
            t10.add(ui_button1);
            t10.add(ui_zone);
            t10.add(ui_dashboard);
            timesteps.put(10, t10);

            t11.add(ui_droneStatus);
            t11.add(ui_minimap);
            t11.add(ui_button1);
            t11.add(ui_button2);
            t11.add(ui_logbook);
            timesteps.put(11, t11);

            t12.add(ui_droneStatus);
            t12.add(ui_minimap);
            t12.add(ui_zone);
            t12.add(ui_dashboard);
            t12.add(ui_logbook);
            timesteps.put(12, t12);

            t13.add(ui_droneStatus);
            t13.add(ui_minimap);
            t13.add(ui_zone);
            t13.add(ui_dashboard);
            t13.add(ui_logbook);
            timesteps.put(13, t13);

            t14.add(ui_droneStatus);
            t14.add(ui_minimap);
            t14.add(ui_zone);
            t14.add(ui_button1);
            t14.add(ui_logbook);
            timesteps.put(14, t14);

            t15.add(ui_zone);
            t15.add(ui_minimap);
            t15.add(ui_button3);
            t15.add(ui_button2);
            t15.add(ui_button1);
            timesteps.put(15, t15);

            t16.add(ui_zone);
            t16.add(ui_droneStatus);
            t16.add(ui_minimap);
            t16.add(ui_dashboard);
            t16.add(ui_logbook);
            timesteps.put(16, t16);

            t17.add(ui_zone);
            t17.add(ui_button1);
            t17.add(ui_minimap);
            t17.add(ui_dashboard);
            t17.add(ui_logbook);
            timesteps.put(17, t17);

            t18.add(ui_zone);
            t18.add(ui_droneStatus);
            t18.add(ui_minimap);
            t18.add(ui_dashboard);
            t18.add(ui_logbook);
            timesteps.put(18, t18);


            Integer k = timestep;
            ArrayList<GraphTraversal<Vertex, Vertex>> uiNodeList = timesteps.get(k);


            for (GraphTraversal<Vertex, Vertex> uiComp : uiNodeList) {

                Vertex uiComponent = uiComp.next();
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
        }
        catch (Exception ex) {
            ex.printStackTrace();
            tx.rollback();
        }

        Map<Long, Double> sortedList = sortByComparator(listOfUINodeIDsToWeights, DESC);
        String outNodes = "g.V(" + (Arrays.toString(ArrayUtils.addAll(sortedList.keySet().stream().limit(5).toArray(), topRelevantNodes.toArray())) + ")");
        System.out.println("GETTOPNODES");
        System.out.println(outNodes);
        System.out.println("\n\n\n\n");
        return outNodes;
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
