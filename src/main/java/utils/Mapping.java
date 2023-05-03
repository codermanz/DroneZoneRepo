package utils;

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

    /* Main methods */
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
    public GraphTraversalSource updateGraph(GraphTraversalSource graph, boolean edgeOnly) {
        /* update graph based on the mapping attributes and new vertices in graph
        *
        * Args:
        *   graph [GraphTraversalSource]:       the graph that the vertices and edges are created into
        *   edgeOnly [boolean]:                 if yes, assumes that vertices already exist in graph and only creates edges between them. If no, creates the vertexes and edges.
        */

        if (edgeOnly){
            graph = createEdges(graph);
        }
        else{
            graph = createFullMapping(graph);
        }
        return graph;
    }
    public GraphTraversalSource createFullMapping(GraphTraversalSource graph) {
        /* create the full graph (vertices and edges) from mapping the attributes.
        * Useful if the vertices of the mapping are not inside the graph yet.
        * e.g. creating the initial mission to action mapping.
        * Args:
        *   graph [GraphTraversalSource]:      the graph that the vertices and edges are created into
        */

        String parentVertexLabel = this.getVertexLabels().get(0);
        String childVertexLabel = this.getVertexLabels().get(1);
        String parentVertexMainProperty = this.getMappingProperties().get(0);
        String childVertexMainProperty = this.getMappingProperties().get(1);

        for (String key : this.mapping.keySet()) {
            // create parent node
            Vertex parentVertex = graph.addV(parentVertexLabel).property(parentVertexMainProperty, key).next();

            HashMap<String, ArrayList<Object>> innerMap = this.mapping.get(key);
            for (String innerKey : innerMap.keySet()) {
                // create child nodes
                Vertex childVertex = graph.addV(childVertexLabel).property(childVertexMainProperty, innerKey).next();

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
    public GraphTraversalSource createEdges(GraphTraversalSource graph) {
        /* create edges based on the mapping attributes.
         * assumes that the vertices that need to be connected are already existing in the graph without the edges between them.
         *
         * !! IMPORTANT NOTE !!
         * this method searches for standalone children nodes in the graph.
         * this means that observations need to be defined as children of actions in and ui components in the .conf files.
         * this may be not as flexible, but for our purposes will be way faster with querying.
         *
         * Args:
         *   graph [GraphTraversalSource]:      the graph that the vertices and edges are created into
         */

        String parentVertexLabel = this.getVertexLabels().get(0);
        String childVertexLabel = this.getVertexLabels().get(1);
        String parentVertexMainProperty = this.getMappingProperties().get(0);
        String childVertexMainProperty = this.getMappingProperties().get(1);

        // get all childNodes with label this.vertexLabels.get(0) that don't have incoming edges yet.
        List<Vertex> standAloneChildVertices = graph.V().hasLabel(childVertexLabel).has(childVertexMainProperty).not(__.inE(this.edgeLabel)).toList();
        // iterate this.mapping
        for (String key : this.mapping.keySet()) {
            // find parent node in the graph
            Vertex parentVertex = graph.V().hasLabel(parentVertexLabel).has(parentVertexMainProperty, key).next();

            HashMap<String, ArrayList<Object>> innerMap = this.mapping.get(key);

            for (String innerKey : innerMap.keySet()) {
                for (Vertex childVertex : standAloneChildVertices) {
                    ArrayList obv_type_values = (ArrayList) graph.V(childVertex).valueMap().next().get(childVertexMainProperty);
                    if (innerKey.equals(obv_type_values.get(0))){
                        // then create the edge between parent and child
                        GraphTraversal<Vertex, Edge> edge = graph.V(parentVertex).addE(this.edgeLabel).to(childVertex);
                        // add properties to the edge
                        ArrayList<Object> propertyNames = this.edgeProperties;
                        ArrayList<Object> propertyValues = innerMap.get(innerKey);
                        for (int i = 0; i < propertyNames.size(); i++) {
                            edge.property((String) propertyNames.get(i), propertyValues.get(i));
                        }
                        edge.iterate();
                    }
                    else{
                        continue;
                    }
                }
            }}
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


