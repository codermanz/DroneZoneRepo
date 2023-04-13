import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;


/**
 *
 * @author zayd & saad & philip
 */

public class SampleGraph {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        Operator u = new Operator(0,1);
        System.out.println('\n');
        GraphTraversalSource g = traversal().
                withRemote(DriverRemoteConnection.using("localhost",8182,"g"));
        ArrayList<Vertex> v = new ArrayList<>();

        // Create Mapping
        Mapping setup = new Mapping(g);
        setup.createMissionActionGraph();
        g = setup.getGraph();

        // Create agents
        for(int i = 0; i < 4; i++){
            v.add(g.addV("drone").property("id", i+1).next());
        }


        // Commented out and replaced operator object with string as we have problem with serialization if it's an object
//        v.add(g.addV("observation").property("operator", new Operator(u.stress, u.attentiveness, u.x, u.y)).next());
//        v.add(g.addV("observation").property("operator", new Operator(u.stress, u.attentiveness, u.x, u.y)).next());

        // Add a couple of observations with the operator state when the observation occurred
        v.add(g.addV("observation").property("name", "Soldier found").
                property("Operator Status", u.toString()).next());
        v.add(g.addV("observation").property("name", "Zone clear").
                property("Operator Status", u.toString()).next());

        // Add edge from observation to agent that observed it
        g.addE("observed").from( v.get(0)).to( v.get(v.size()-1)).iterate();
        g.addE("observed").from( v.get(2)).to( v.get(v.size()-1)).iterate();
        g.addE("observed").from( v.get(3)).to( v.get(v.size()-2)).iterate();



        // Add UI component nodes
        v.add(g.addV("component").property("name", "Identify Human as Map").
                property("type", "mode a").next());
        v.add(g.addV("component").property("name", "Identify Human as voice Alert").
                property("type", "mode b").next());
        v.add(g.addV("component").property("name", "Report Green Zone as Map").
                property("type", "mode a").next());
        v.add(g.addV("component").property("name", "Report Green Zone as Vibration").
                property("type", "mode b").next());

        // Edge from observation to corresponding UI component
        g.addE("solved with").from( v.get(6)).to( v.get(v.size()-3)).iterate();
        g.addE("solved with").from( v.get(6)).to( v.get(v.size()-4)).iterate();
        g.addE("solved with").from( v.get(7)).to( v.get(v.size()-1)).iterate();
        g.addE("solved with").from( v.get(7)).to( v.get(v.size()-2)).iterate();

        System.out.println(g.V().valueMap().toList());
        System.out.println(g.E().valueMap().toList());
        System.out.println("Finished running the script");

    }

}

class Operator{
    int stress;
    int attentiveness;
    int timestamp;

    public Operator(int stress, int attentiveness) {
        this.stress = stress;
        this.attentiveness = attentiveness;
        // To convert to a date object: date = new Date(((long)this.timestamp)*1000L);
        this.timestamp = (int) (new Date().getTime()/1000);
    }

    @Override
    public String toString() {
        return ("S: " + this.stress + "--A: " + this.attentiveness + "--T: " + this.timestamp);
    }
    
}


/*
Mapping Class to store mapping information and create vertexes and edges.
Information about mapping between observations to actions && missions to actions.

How you would use this:

1. create your graph in the main script.        (GraphTraversalSource g = traversal() .....)
2. create Mapping Object:                       (Mapping setup = new Mapping(g);)
3. create Mission -> Action mapping graph       (setup.createMissionActionGraph();)
4. set graph in main script to setup graph      (g = setup.getGraph();)
*/
class Mapping{
    GraphTraversalSource graph;
    // -> observationActionMap is currently not implemented
    // make this after zayd finished with his action item and base the data structure on his results
    HashMap<String, HashMap<String, Integer>> observationActionMap; // 'observation-type': {action1: weight1, action2: weight2, ...}
    HashMap<String, HashMap<String, Integer>> missionActionMap; // 'mission': {action1: weight1, action2: weight2, ...}

    public Mapping(GraphTraversalSource g) {
        this.graph = g;
        this.observationActionMap = null;
        this.missionActionMap = null;
    }
    // Function that creates a HARDCODED mission action mapping.
    public void createMissionActionMapping(){
        this.setMissionActionMap(new HashMap<String, HashMap<String, Integer>>() {{
            // mission1
            put("rescue soldier", new HashMap<String, Integer>() {{
                // actions to mission1 with weights
                put("drop medipack", 3);
                put("send helicopter", 4);
                put("create rescue path", 5);
            }});
            // mission2
            put("scan zone", new HashMap<String, Integer> (){{
                // actions to mission2 with weights
                put("send drone", 5);
                put("monitor drone", 3);
            }});
            // mission3
            put("identify agent", new HashMap<String, Integer> (){{
                // actions to mission 3 with weights
                put("check drone report", 3);
                put("classify agent", 5);
            }});
        }});
    };
    /*
    Function that creates vertexes and edges based on the missionActionMap and adds them to our GraphTraversalSource object.
    If this.missionActionMap = null, a hardcoded map will be generated by the method 'createMissionActionMapping()'.
    */
    public void createMissionActionGraph() {
        GraphTraversalSource g =  this.graph;
        // call method to generate the hardcoded mission and action map
        if (this.missionActionMap == null){
            createMissionActionMapping();
        };
        // in case this function is called multiple times during the mission, do we want to delete older mapping graphs?

        // iterate the missionActionMap and create nodes + edges
        for (String mission : this.missionActionMap.keySet()) {
            // create mission vertex
            Vertex missionVertex = g.addV("mission").property("name", mission).next();
            HashMap<String, Integer> innerMap = this.missionActionMap.get(mission);
            for (String action : innerMap.keySet()) {
                // get the mission to action weight from the innerMap
                Integer weight = innerMap.get(action);
                // Create action vertex
                Vertex actionVertex = g.addV("action").property("name", action).next();
                // Create edge from mission vertex to action vertex with weight and label
                g.V(missionVertex).addE("is composed of").to(actionVertex).property("weight", weight).iterate();
            }};
        this.graph = g;
    }
    public void setMissionActionMap(HashMap<String, HashMap<String, Integer>> missionActionMap) {
        this.missionActionMap = missionActionMap;
    }
    public HashMap getMissionActionMap() {
        return this.missionActionMap;
    };
    public void setObservationActionMap(HashMap<String, HashMap<String, Integer>> observationActionMap) {
        this.observationActionMap = observationActionMap;
    }
    public HashMap getObservationActionMap() {
        return observationActionMap;
    }
    public void setGraph(GraphTraversalSource graph) {
        this.graph = graph;
    }
    public GraphTraversalSource getGraph() {
        return graph;
    }
    @Override
    public String toString() {
        return ("Observation-Types to Action Mapping: " + this.observationActionMap + "\n" + "Mission to Action Mapping: " + this.missionActionMap);
    }

}
