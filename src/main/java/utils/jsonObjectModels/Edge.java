package utils.jsonObjectModels;

import java.util.List;

public class Edge {

    private String edge_name;
    private List<Attribute> attributes;

    public Edge() {
    }

    public Edge(String edge_name, List<Attribute> attributes) {
        this.edge_name = edge_name;
        this.attributes = attributes;
    }

    public String getEdge_name() {
        return edge_name;
    }

    public void setEdge_name(String edge_name) {
        this.edge_name = edge_name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        StringBuilder forReturning = new StringBuilder("\n\t\t\tEdge Name: " + this.edge_name);

        for (Attribute attr: this.attributes)
            forReturning.append("\n\t\t\t").append(attr.getAttribute_Name()).append(": ").
                    append(attr.getAttribute_Value());

        return (forReturning.toString());
    }
}
