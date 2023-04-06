import java.util.ArrayList;
import java.util.Scanner;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import static org.apache.tinkerpop.gremlin.process.traversal.P.neq;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import static org.apache.tinkerpop.gremlin.structure.T.*;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.javatuples.Tuple;

/**
 *
 * @author zayd
 */

public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        User u = new User(0,1,0,0);
        System.out.println('\n');
        
        
        Graph graph = TinkerGraph.open();
        GraphTraversalSource g = traversal().withEmbedded(graph);
        ArrayList v = new ArrayList<Vertex>();
        for(int i = 0; i < 4; i++){
            v.add(g.addV("drone").property(id, i+1).next());
        }
        String[] actions = {"Drop Pack", "Search", "Prompt"};
        v.add(g.addV("mission").property("name", "identify").property("actions", actions).next());
        String[] actions2 = {"report", "identify", "scan"};
        v.add(g.addV("mission").property("name", "scan").property("actions", actions2).next());
        
        v.add(g.addV("observation").property("user", new User(u.stress, u.attentiveness, u.x, u.y)).next());
        v.add(g.addV("observation").property("user", new User(u.stress, u.attentiveness, u.x, u.y)).next());
        
        g.addE("observed").from((Vertex) v.get(0)).to((Vertex) v.get(v.size()-1)).iterate();
        g.addE("observed").from((Vertex) v.get(2)).to((Vertex) v.get(v.size()-1)).iterate();
        g.addE("observed").from((Vertex) v.get(3)).to((Vertex) v.get(v.size()-2)).iterate();
        
        g.addE("important for").from((Vertex) v.get(v.size()-1)).to((Vertex) v.get(v.size()-3)).iterate();
        g.addE("important for").from((Vertex) v.get(v.size()-1)).to((Vertex) v.get(v.size()-4)).iterate();
        g.addE("important for").from((Vertex) v.get(v.size()-2)).to((Vertex) v.get(v.size()-4)).iterate();
        
        g.addV("component").property("interface", "time").property("name", "analog clock").next();
        g.addV("component").property("interface", "time").property("name", "digital clock").next();
        
        g.addE("solved with").from((Vertex) v.get(4)).to((Vertex) v.get(v.size()-1)).iterate();
        g.addE("solved with").from((Vertex) v.get(4)).to((Vertex) v.get(v.size()-2)).iterate();
        
        g.addV("component").property("interface", "health").property("name", "bar").next();
        g.addV("component").property("interface", "health").property("name", "percent").next();
        
        g.addE("solved with").from((Vertex) v.get(4)).to((Vertex) v.get(v.size()-1)).iterate();
        g.addE("solved with").from((Vertex) v.get(4)).to((Vertex) v.get(v.size()-2)).iterate();
        g.addE("solved with").from((Vertex) v.get(5)).to((Vertex) v.get(v.size()-1)).iterate();
        g.addE("solved with").from((Vertex) v.get(5)).to((Vertex) v.get(v.size()-2)).iterate();

        System.out.println(graph);
        
//        String[] s = {"Jon Pearce", "Grit Denker", "Rukman", "Zayd", "Pooja", "Saad", "Rahul", "Bianca", "Simon", "Lucia", "Philip", "software"};
//        for(int i = 0; i < 11; i++)
//            v[i] = g.addV("person").property(id, i).property("Name", s[i]).next();
//        v[11] = g.addV("software").property(id, 11).next();
//        
//        //iterate is used when the result doesn't matter
//        g.addE("works with").from(v[0]).to(v[1]).iterate();
//        g.addE("works with").from(v[0]).to(v[2]).iterate();
//        g.addE("supervises").from(v[1]).to(v[2]).iterate();
//        
//        for(int i = 3; i < 11; i++)
//            g.addE("leads").from(v[2]).to(v[i]).iterate();
//        for(int i = 3; i < 11; i++)
//            g.addE("develops").from(v[i]).to(v[11]).iterate();
//        
//        //traversing the graph
//        
//        System.out.println(g.V(2).outE().inV().values("Name").toList());
//        System.out.println(g.V(2).out("leads").values("Name").toList());
//        
//        System.out.println(g.V().has("person", "Name", "Zayd").as("me").out("develops").in("develops").where(neq("me")).values("Name").toList());
//        System.out.println(g.V().hasLabel("software").next());
//        System.out.println('\n');
    }
    
}
class User{
    int stress = 0;
    int attentiveness = 0;
    int x = 0;
    int y = 0;
    int timestamp = 0;

    public User(int stress, int attentiveness, int x, int y) {
        this.stress = stress;
        this.attentiveness = attentiveness;
        this.x = x;
        this.y = y;
    }
    
}
