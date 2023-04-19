import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;


/**
 *
 * @author zayd & saad & philip
 */

public class SampleGraph {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        ArrayList<Vertex> v = new ArrayList<>();

        GraphTraversalSource g = traversal().
                withRemote(DriverRemoteConnection.using("localhost",8182,"g"));

        // create mission - action graph
        Mapping missionActionMapping = new Mapping();
        g = missionActionMapping.createGraph(g);

        // Create agents
        for(int i = 0; i < 4; i++){
            v.add(g.addV("drone").property("id", i+1).next());
        }

        // Add a couple of observations with the operator state when the observation occurred
        Operator u = new Operator(0,1);

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

    }

}








