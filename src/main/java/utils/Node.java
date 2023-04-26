package utils;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;

import java.util.Iterator;
import java.util.Map;

// Custom Wrapper for Vertex. Not currently relevant to any use cases. Mostly Saad toying with something :)
public class Node implements Vertex {

    // Node's IDs
    private long id;
    // Node's label
    private String label;
    private Map<String, String> properties;
    // Keeps track of whether the node has been written to graph. Only updated once when node is
    // initially written to graph.
    private boolean existsInGraph;


    /**
     * Constructor
     *
     * @param label - Label of node
     * @param properties - Property map
     */
    public Node(String label, Map<String, String> properties) {
        this.id = -1;
        // TODO: Add labels value checking. Labels must be consistent with labels defined in set up
        this.label = label;
        // TODO: Add properties checking. Properties must be consistent with properties per label as defined in setup
        // TODO: Impose requirement of having a name or other properties?
        this.properties = properties;
        this.existsInGraph = false;
    }
//
    public Node(Vertex v, utils.Mapping m) {



    }

    /**
     * Write node to graph. If node already written to the graph at least once, update within graph, else, create a
     * new vertex within the remote graph.
     * @param g
     * @return
     */
    public Vertex writeNodeToGraph(GraphTraversalSource g) throws Exception {
        // TODO: Add error checking for if node already exists or doesnt exist in the graph. Using existsInGraph is iffy
        // If node not written to graph already, write to graph
        if (!existsInGraph) {
            GraphTraversal<Vertex, Vertex> temp = g.addV(this.label);
            this.properties.forEach(temp::property);
            Vertex v = temp.next();
            this.id = (Long) v.id();
            this.existsInGraph = true;
            return v;
        }
        // If node already written to graph (thus exists in the graph), update it
        else {
            GraphTraversal<Vertex, Vertex> temp = g.V(this.id);
            this.properties.forEach(temp::property);
            return temp.next();
        }
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Add or update key value pair in a node's properties
     * @param key
     * @param value
     */
    public void setProperties(String key, String value) {
        this.properties.put(key, value);
    }

    public boolean isExistsInGraph() {
        return existsInGraph;
    }

    @Override
    public String toString() {
        // TODO: Perhaps impose a requirement to add name to properties and display names in toString
        return (this.label + ": " + this.id);
    }



    //*************IMPLEMENT ABSTRACT METHODS FOR VERTEX****************
    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        return null;
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        return null;
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        return null;
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        return null;
    }

    @Override
    public Object id() {
        return null;
    }

    @Override
    public String label() {
        return null;
    }

    @Override
    public Graph graph() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        return null;
    }
}
