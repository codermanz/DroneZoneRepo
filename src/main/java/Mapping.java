import org.apache.tinkerpop.gremlin.driver.ResultSet;
import java.util.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigList;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import java.io.File;


public class Mapping{
    /* Attributes */
    ArrayList<String> vertexLabels;                                     // e.g. ["mission", "action"]
    String edgeLabel;                                                   // e.g. "requires for completion"
    ArrayList<Object> edgeProperties;                                   // property-keys e.g. ["id", "name", "weight", "other-edge-property"]
    HashMap<String,HashMap<String,ArrayList<Object>>> mapping;          // e.g. {"mission1": {action1: [list of properties], action2: [list of properties]}, ....}

    /* Constructors */
    public Mapping(){
        // if instantiated without parameter -> load default config.
        readConfig("./configs/default-mission-action-mapping.conf");
    }
    public Mapping(String config_path){
        // if instantiated with path -> load this path instead
        readConfig(config_path);
    }

    /* Main methods */
    public void readConfig(String configPath){
        /* reads config and sets Mapping attributes based on the config content */

        Config config = ConfigFactory.parseFile(new File(configPath));
        ConfigObject configObject = config.root();

        // set this.mapping:
        ConfigObject myMappingObject =config.getObject("mapping");
        setMapping(convertConfigObjectToHashMap(myMappingObject));

        // set this.VertexLabels:
        ConfigList myVertexList = config.getList("vertexLabels");
        setVertexLabels(convertConfigListToArrayList(myVertexList));

        // set this.edgeLabel:
        String myEdgeLabel = config.getString("edgeLabel");
        setEdgeLabel(myEdgeLabel);

        // set this.edgeProperties:
        ConfigList myEdgeProps = config.getList("edgeProperties");
        setEdgeProperties(convertConfigListToArrayList(myEdgeProps));
    }
    public GraphTraversalSource createGraph(GraphTraversalSource graph) {
        /* create graph from this mapping-objects attributes */

        String parentVertexName = this.getVertexLabels().get(0);
        String childVertexName = this.getVertexLabels().get(1);
        for (String key : this.mapping.keySet()) {
            // create parent node
            Vertex parentVertex = graph.addV(parentVertexName).property("name", key).next();

            HashMap<String, ArrayList<Object>> innerMap = this.mapping.get(key);
            for (String innerKey : innerMap.keySet()) {

                // create child nodes if they don't already exist!
                Vertex childVertex;

                if (graph.V().has("name", innerKey).valueMap().toList().size() > 0){
                    childVertex = graph.V().has("name", innerKey).toList().get(0);
                }
                else{
                    childVertex = graph.addV(childVertexName).property("name", innerKey).next();
                }

                // create edge between parent and child
                GraphTraversal<Vertex, Edge> edge = graph.V(parentVertex).addE(this.edgeLabel).to(childVertex);

                // add properties to edge
                ArrayList<Object> propertyNames = this.edgeProperties;
                ArrayList<Object> propertyValues = innerMap.get(innerKey);
                for (int i = 0; i < propertyNames.size(); i++) {
                    edge.property((String) propertyNames.get(i), propertyValues.get(i));
                }
                edge.iterate();

            }
        }
        return graph;
    }

    /* Config-Datatypes conversion methods */
    public HashMap convertConfigObjectToHashMap(ConfigObject configObject){
        HashMap myHashMap = new HashMap<>();
        for (Map.Entry<String, ConfigValue> entry : configObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue().unwrapped();
            myHashMap.put(key, value);
        };
        return myHashMap;
    }
    public ArrayList convertConfigListToArrayList(ConfigList configList){
        ArrayList<String> myList = new ArrayList<>();

        for (ConfigValue value : configList) {
            String item = value.unwrapped().toString();
            myList.add(item);
        }
        return myList;
    }

    /* Setter&Getter methods */
    public HashMap getMapping() {
        return mapping;
    }
    public void setMapping(HashMap mapping) {
        this.mapping = mapping;
    }
    public ArrayList<String> getVertexLabels() {
        return vertexLabels;
    }
    public void setVertexLabels(ArrayList<String> vertexLabels) {
        this.vertexLabels = vertexLabels;
    }
    public String getEdgeLabel() {
        return edgeLabel;
    }
    public void setEdgeLabel(String edgeLabel) {
        this.edgeLabel = edgeLabel;
    }
    public ArrayList<Object> getEdgeProperties() {
        return edgeProperties;
    }
    public void setEdgeProperties(ArrayList<Object> edgeProperties) {
        this.edgeProperties = edgeProperties;
    }
    @Override
    public String toString() {
        return "Mapping{" +
                "mapping=" + mapping +
                ", vertex_labels=" + vertexLabels +
                ", edge_label='" + edgeLabel + '\'' +
                '}';
    }
}


