package utils.jsonObjectModels;

import java.util.List;

public class Prioritization {

    private String prioritization_reason;
    private String prioritizing_node_type;
    private List<String> prioritizing_missions;
    private List<String> target_actions;
    private double scalar_multiplier;

    public Prioritization() {}

    public Prioritization(String prioritization_reason, String prioritizing_node_type, List<String> prioritizing_missions, List<String> target_actions, double scalar_multiplier) {
        this.prioritization_reason = prioritization_reason;
        this.prioritizing_node_type = prioritizing_node_type;
        this.prioritizing_missions = prioritizing_missions;
        this.target_actions = target_actions;
        this.scalar_multiplier = scalar_multiplier;
    }

    public String getPrioritization_reason() {
        return prioritization_reason;
    }

    public void setPrioritization_reason(String prioritization_reason) {
        this.prioritization_reason = prioritization_reason;
    }

    public String getPrioritizing_node_type() {
        return prioritizing_node_type;
    }

    public void setPrioritizing_node_type(String prioritizing_node_type) {
        this.prioritizing_node_type = prioritizing_node_type;
    }

    public List<String> getPrioritizing_missions() {
        return prioritizing_missions;
    }

    public void setPrioritizing_missions(List<String> prioritizing_missions) {
        this.prioritizing_missions = prioritizing_missions;
    }

    public List<String> getTarget_actions() {
        return target_actions;
    }

    public void setTarget_actions(List<String> target_actions) {
        this.target_actions = target_actions;
    }

    public double getScalar_multiplier() {
        return scalar_multiplier;
    }

    public void setScalar_multiplier(double scalar_multiplier) {
        this.scalar_multiplier = scalar_multiplier;
    }

    @Override
    public String toString() {
        return ("Prioritization reason: " + this.prioritization_reason + ", Prioritization Node: " + this.prioritizing_node_type +
                ", multiple: " + this.scalar_multiplier + ", Prioritizing missions: " + this.prioritizing_missions +
                ", Prioritizing targets: " + this.target_actions);
    }
}

/*
 OBJECT FOR JSON FOR PRIORITIZATION

     {
        "prioritization_reason": "observation",
        "prioritizing_node_type": "soldier_reported",
        "prioritizing_missions": ["rescue_soldier"],
        "target_actions": ["confirm_soldier", "decline_soldier", "monitor_drone", "check_drone_report"],
        "scalar_multiplier": 2.0
    }

 */