package utils;

import org.apache.tinkerpop.gremlin.driver.ResultSet;
import java.util.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigList;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import java.io.File;


public class Mapping{
    /* Attributes */
    ArrayList<String> vertexLabels;                                     // ["parent", "child"]
    ArrayList<String> mappingProperties;
    String edgeLabel;                                                   // "edge label"
    ArrayList<Object> edgeProperties;                                   // property-names e.g. ["id", "name", "weight", "other-edge-property"]
    HashMap<String,HashMap<String,ArrayList<Object>>> mapping;          // e.g. {"parent1": {child1: [list of properties], child2: [list of properties]}, ....}

    /* Constructors */
    public Mapping(){
        // if instantiated without parameter -> load default-mission-action config.
        readConfig("./configs/default-mission-action-mapping.conf");
    }
    public Mapping(String config_path){
        // if instantiated with path -> load this path instead
        readConfig(config_path);
    }

    public void readConfig(String configPath){
        /* reads config and sets utils.Mapping attributes based on the config content */

        Config config = ConfigFactory.parseFile(new File(configPath));
        ConfigObject configObject = config.root();

        // set this.mapping:
        ConfigObject myMappingObject =config.getObject("mapping");
        setMapping(convertConfigObjectToHashMap(myMappingObject));

        // set this.mappingProperties:
        ConfigList myMappingPropertyList = config.getList("mappingProperties");
        setMappingProperties(convertConfigListToArrayList(myMappingPropertyList));

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

    /* Main update function */
    public GraphTraversalSource updateGraph(GraphTraversalSource graph, List<Vertex> vertices) {
        /* creates edges based on the .conf file.
         * assumes that the vertices that need to be connected are already existing in the graph without the edges between them.
         * edge direction is determined by the order of vertexLabels in the .conf file:
         *      - vertexLabels[0] -> parentNodes
         *      - vertexLabels[1] -> childNodes
         *
         * Args:
         *   graph [GraphTraversalSource]:      the graph that the edges are created into
         *   vertices [List<Vertex>]:           vertices that edges are created with. Instead of querying the whole graph, we pass them after each timestep
         */

        // get relevant mapping attributes
        String parentVertexLabel = this.getVertexLabels().get(0);
        String childVertexLabel = this.getVertexLabels().get(1);
        String parentVertexMainProperty = this.getMappingProperties().get(0);
        String childVertexMainProperty = this.getMappingProperties().get(1);

        // find out if the passed vertices are child or parent nodes
        String vertexLabel = graph.V().hasId(vertices.get(0).id()).label().next();

        // create mapping based on if passed vertices are parent or children nodes
        if (vertexLabel.equals(parentVertexLabel)){
            // passed vertices are parent nodes -> edges go out of them

            // 0. get each parentVertex mapping property to find its mapped children in this.mapping
            for (Vertex parent: vertices){
                LinkedList parentMatchingKeyList = (LinkedList) graph.V(parent.id()).valueMap().next().get(parentVertexMainProperty);
                String parentMatchingKey = (String) parentMatchingKeyList.get(0);
                HashMap<String, ArrayList<Object>> childrenMap = this.mapping.get(parentMatchingKey);
                /*  1. iterate over all children of the current parent in this.mapping
                *   2. find each child in the graph by looking for nodes that match the following criteria:
                *       - nodes that have label = childLabel
                *       - nodes that have childMainProperty = childMainProperty from this.mapping
                *       - (nodes that have no edges to the current parentVertex yet) --> not implemented yet but open for discussion
                *       --> this could result in multiple nodes per child from the mapping structure!
                *   3. iterate over all found child vertices and create edge to parent for each
                */
                for (String childKey : childrenMap.keySet()) {
                    List<Vertex> childVertices = graph.V().hasLabel(childVertexLabel).has(childVertexMainProperty, childKey).toList();
                    for (Vertex childVertex: childVertices){
                        // create edge
                        GraphTraversal<Vertex, Edge> edge = graph.V(parent).addE(this.edgeLabel).to(childVertex);

                        // add properties to the edge
                        ArrayList<Object> propertyNames = this.edgeProperties;
                        ArrayList<Object> propertyValues = childrenMap.get(childKey);
                        for (int i = 0; i < propertyNames.size(); i++) {
//                            edge.property((String) propertyNames.get(i), propertyValues.get(i).toString().replaceAll("[a-zA-Z]", ""));

                            String test = propertyValues.get(i).toString().replaceAll("[a-zA-Z]", "");
                            edge.property((String) propertyNames.get(i), test);                        }

                        // finalize changes
                        edge.iterate();
                    }
                }
            }
        }
        else if (vertexLabel.equals(childVertexLabel)){
            // passed vertices are child nodes -> edges go into them

            /*  0. iterate over the passed children
            *   1. iterate over this.mapping (also innerMap)
            *   2. if current childrenMainProperty matches current this.mapping child property:
            *       - get current parentMainProperty
            *       - query graph for all nodes that match:
            *           - label = parentVertexLabel
            *           - has property parentVertexMainProperty = current parentVertexMainProperty from this.mapping
            *           - (has no outgoing edge to current childVertex yet) -> currently not implemented, open for discussion)
            *       - create edge between all found nodes and current child
            */

            for (Vertex child: vertices){
                for (String parentKey : this.mapping.keySet()) {
                    HashMap<String, ArrayList<Object>> childrenMap = this.mapping.get(parentKey);
                    for (String childKey : childrenMap.keySet()) {
                        LinkedList childMatchingKeyList = (LinkedList) graph.V(child.id()).valueMap().next().get(childVertexMainProperty);
                        String childMatchingKey = (String) childMatchingKeyList.get(0);
                        if (childKey.equals(childMatchingKey)){
                            List<Vertex> parentVertices = graph.V().hasLabel(parentVertexLabel).has(parentVertexMainProperty, parentKey).toList();
                            for (Vertex parent: parentVertices){
                                // then create the edge between parent and child
                                GraphTraversal<Vertex, Edge> edge = graph.V(parent).addE(this.edgeLabel).to(child);

                                // add properties to the edge
                                ArrayList<Object> propertyNames = this.edgeProperties;
                                ArrayList<Object> propertyValues = childrenMap.get(childKey);
                                for (int i = 0; i < propertyNames.size(); i++) {
                                    String test = propertyValues.get(i).toString().replaceAll("[a-zA-Z]", "");
                                    edge.property((String) propertyNames.get(i), test);
                                }
                                edge.iterate();
                            }
                        }
                    }
                }
            }
        }
        else {
            System.out.println("Could not match passed vertices with mapping.vertexLabels.");
        };
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
    public ArrayList<String> getMappingProperties() {
        return mappingProperties;
    }
    public void setMappingProperties(ArrayList<String> mappingProperties) {
        this.mappingProperties = mappingProperties;
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
        return "utils.Mapping{" +
                "mapping=" + mapping +
                ", vertex_labels=" + vertexLabels +
                ", edge_label='" + edgeLabel + '\'' +
                '}';
    }
}


