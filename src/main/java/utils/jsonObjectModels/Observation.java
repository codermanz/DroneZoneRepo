package utils.jsonObjectModels;

import utils.jsonObjectModels.Attribute;

import java.util.List;

public class Observation {

    private String agent_id;
    private String obv_type;
    private List<Attribute> attributes;
    private int edge_weight;
    private String edge_timestamp;

    public Observation() {

    }

    public Observation(String agent_id, String obv_type, List<Attribute> attributes, int edge_weight, String edge_timestamp) {
        this.agent_id = agent_id;
        this.obv_type = obv_type;
        this.attributes = attributes;
        this.edge_weight = edge_weight;
        this.edge_timestamp = edge_timestamp;
    }

    public Observation(String agent_id, String obv_type, int edge_weight, String edge_timestamp) {
        this.agent_id = agent_id;
        this.obv_type = obv_type;
        this.edge_weight = edge_weight;
        this.edge_timestamp = edge_timestamp;
    }


    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getObv_type() {
        return obv_type;
    }

    public void setObv_type(String obv_type) {
        this.obv_type = obv_type;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public int getEdge_weight() {
        return edge_weight;
    }

    public void setEdge_weight(int edge_weight) {
        this.edge_weight = edge_weight;
    }

    public String getEdge_timestamp() {
        return edge_timestamp;
    }

    public void setEdge_timestamp(String edge_timestamp) {
        this.edge_timestamp = edge_timestamp;
    }

    @Override
    public String toString() {
        return ("\n\t\tAgent ID: " + this.agent_id + "\n\t\tObservation Type: " + this.obv_type +
                "\n\t\tEdge weight: " + this.edge_weight + "\n\t\tEdge Timestamp: " + this.edge_timestamp +
                "\n\t\tNode Attributes: " + this.attributes);
    }
}
