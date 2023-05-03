import java.util.ArrayList;
import models.Operator;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import utils.Mapping;


/**
 *
 * @author zayd & saad & philip
 */
public class SampleGraph {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Operator u = Operator.getInstance();
        System.out.println('\n');

        // Create Graph traversal object
        GraphTraversalSource g = traversal().
                withRemote(DriverRemoteConnection.using("localhost",8182,"g"));
        ArrayList<Vertex> v = new ArrayList<>();

        // load mapping configs
        Mapping missionActionMapping = new Mapping("./configs/default-mission-action-mapping.conf");
        Mapping actionObservationMapping = new Mapping("./configs/default-action-observation-mapping.conf");

        // update graph based on static mission action mapping, edgeOnly = false to create the whole graph instead of only edges
        g = missionActionMapping.updateGraph(g, false);

        // TODO: Implement everything below has a single transaction
        // Create agents
        for(int i = 0; i < 4; i++) {
            v.add(g.addV("drone").property("id", i+1).next());
        }


        // Add a couple of observations with the operator state when the observation occurred
        v.add(g.addV("observation").property("obv_type", "soldier_found").property("Operator Status", u.toString()).next());
        v.add(g.addV("observation").property("obv_type", "zone_scanning").property("Operator Status", u.toString()).next());

        // Add edge from observation to agent that observed it
        g.addE("observed").from( v.get(0)).to( v.get(v.size()-1)).iterate();
        g.addE("observed").from( v.get(2)).to( v.get(v.size()-1)).iterate();
        g.addE("observed").from( v.get(3)).to( v.get(v.size()-2)).iterate();

        // update graph to create edges between new observations and their mapped actions
        g = actionObservationMapping.updateGraph(g, true);



//        // Add UI component nodes
//        v.add(g.addV("component").property("name", "Identify Human as Map").
//                property("type", "mode a").next());
//        v.add(g.addV("component").property("name", "Identify Human as voice Alert").
//                property("type", "mode b").next());
//        v.add(g.addV("component").property("name", "Report Green Zone as Map").
//                property("type", "mode a").next());
//        v.add(g.addV("component").property("name", "Report Green Zone as Vibration").
//                property("type", "mode b").next());
//
//        // Edge from observation to corresponding UI component
//        g.addE("solved with").from( v.get(6)).to( v.get(v.size()-3)).iterate();
//        g.addE("solved with").from( v.get(6)).to( v.get(v.size()-4)).iterate();
//        g.addE("solved with").from( v.get(7)).to( v.get(v.size()-1)).iterate();
//        g.addE("solved with").from( v.get(7)).to( v.get(v.size()-2)).iterate();
//        g.close();
    }

}

/**
 * {
 *     "observations": [
 *          "observation": {
 *              "agent_id": "saturn",
 *              "obv_type": "zone_reporting",
 *              "report": {
 *                  "weight": 10,
 *                  "timestamp": "2023-04-25T03:31:04+0000"
 *              }
 *          },
 *          "observation": {
 *  *              "agent_id": "mercury",
 *  *              "obv_type": "zone_reporting",
 *                 "obv_description": "zone 2 clear",
 *  *              "report": {
 *  *                  "weight": 5,
 *  *                  "timestamp": "2023-04-25T03:31:04+0000"
 *  *              }
 *  *          }
 *     ]
 * }
 */
