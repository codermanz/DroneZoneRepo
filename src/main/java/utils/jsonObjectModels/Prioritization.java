package utils.jsonObjectModels;

import java.util.List;

public class Prioritization {

    private String activation_reason;
    private String activating_node_type;
    private List<String> activating_missions;
    private List<String> target_action;
    private int importance;

    public Prioritization() {

    }

    public Prioritization(String activation_reason, String activating_node_type, List<String> missions,
                          List<String> target_action, int importance) {
        this.activation_reason = activation_reason;
        this.activating_node_type = activating_node_type;
        this.activating_missions = missions;
        this.target_action = target_action;
        this.importance = importance;
    }

    public String getActivation_reason() {
        return activation_reason;
    }

    public void setActivation_reason(String activation_reason) {
        this.activation_reason = activation_reason;
    }

    public String getActivating_node_type() {
        return activating_node_type;
    }

    public void setActivating_node_type(String activating_node_type) {
        this.activating_node_type = activating_node_type;
    }

    public List<String> getActivating_missions() {
        return activating_missions;
    }

    public void setActivating_missions(List<String> activating_missions) {
        this.activating_missions = activating_missions;
    }

    public List<String> getTarget_action() {
        return target_action;
    }

    public void setTarget_action(List<String> target_action) {
        this.target_action = target_action;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    @Override
    public String toString() {
        return ("Activation reason: " + this.activation_reason + ", Activating Node: " + this.activating_node_type +
                ", Importance: " + this.importance + ", Activating missions: " + this.activating_missions +
                ", Activating targets: " + this.target_action);
    }
}
