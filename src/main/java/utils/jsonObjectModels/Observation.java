package utils.jsonObjectModels;

import utils.jsonObjectModels.Attribute;

import java.util.List;

public class Observation {

    private String agent_id;
    private String obv_type;
    private List<Attribute> attributes;

    private Edge edge;


    public Observation() {

    }

    public Observation(String agent_id, String obv_type, List<Attribute> attributes, Edge edge) {
        this.agent_id = agent_id;
        this.obv_type = obv_type;
        this.attributes = attributes;
        this.edge = edge;
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

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    @Override
    public String toString() {
        return ("\n\t\tAgent ID: " + this.agent_id + "\n\t\tObservation Type: " + this.obv_type +
                "\n\t\tEdge: " + this.edge + "\n\t\tNode Attributes: " + this.attributes);
    }
}
