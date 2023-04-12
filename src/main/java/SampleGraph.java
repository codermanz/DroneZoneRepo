import java.util.ArrayList;
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
        Operator u = new Operator(0,1);
        System.out.println('\n');

        GraphTraversalSource g = traversal().
                withRemote(DriverRemoteConnection.using("localhost",8182,"g"));
        ArrayList<Vertex> v = new ArrayList<>();

        // Create agents
        for(int i = 0; i < 4; i++){
            v.add(g.addV("drone").property("id", i+1).next());
        }
        // Define actions and their corresponding missions
        v.add(g.addV("action").property("name", "drop pack").
                property("mission", "identify").next());
        v.add(g.addV("action").property("name", "search").
                property("mission", "scan").next());

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

        // Add edge to the action nodes that observations are relevant to
        g.addE("important for").from( v.get(v.size()-1)).to( v.get(v.size()-3)).iterate();
        g.addE("important for").from( v.get(v.size()-2)).to( v.get(v.size()-4)).iterate();

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
