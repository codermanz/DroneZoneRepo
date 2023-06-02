package utils.ConfigClasses;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import java.util.HashMap;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;


// Setup Class that handles reading the .json files from /configs/ and adds the vertices based on that files into our graph
public class Setup {
    String path;
    String label;
    List<Vertex> createdVertices;

    public Setup(String path, String label){
        this.path = path;
        this.label = label;
    }

    public GraphTraversalSource createNodes(GraphTraversalSource graph){
        List<Vertex> vertexList = new ArrayList<>();;

        // Read the contents of the file into a string
        String jsonStr = null;
        try {
            jsonStr = new String(Files.readAllBytes(Paths.get(this.path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Parse the JSON string into a JsonNode object
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Traverse the tree to extract the data and create HashMaps for each object
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                Iterator<String> fieldNames = node.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode fieldValue = node.get(fieldName);
                    map.put(fieldName, fieldValue.asText());
                }
                // Create vertex + properties
                GraphTraversal<Vertex, Vertex> vertex = graph.addV(this.label);
                Vertex vert;
                map.keySet().forEach(x -> vertex.property(x, map.get(x)));
                vert = vertex.next();
                vertexList.add(vert);
            }
        }
        setCreatedVertices(vertexList);
        return graph;
    }

    public List<Vertex> getCreatedVertices() {
        return createdVertices;
    }

    public void setCreatedVertices(List<Vertex> createdVertices) {
        this.createdVertices = createdVertices;
    }
}
