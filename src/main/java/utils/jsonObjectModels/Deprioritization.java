package utils.jsonObjectModels;

import java.util.List;


public class Deprioritization {

    private String deprioritization_reason;
    private String deprioritizing_node_type;
    private List<String> deprioritizing_missions;
    private List<String> target_actions;
    private double scalar_multiplier;

    public Deprioritization() {}

    public Deprioritization(String deprioritization_reason, String deprioritizing_node_type, List<String> deprioritizing_missions, List<String> target_actions, double scalar_multiplier) {
        this.deprioritization_reason = deprioritization_reason;
        this.deprioritizing_node_type = deprioritizing_node_type;
        this.deprioritizing_missions = deprioritizing_missions;
        this.target_actions = target_actions;
        this.scalar_multiplier = scalar_multiplier;
    }

    public String getDeprioritization_reason() {
        return deprioritization_reason;
    }

    public void setDeprioritization_reason(String deprioritization_reason) {
        this.deprioritization_reason = deprioritization_reason;
    }

    public String getDeprioritizing_node_type() {
        return deprioritizing_node_type;
    }

    public void setDeprioritizing_node_type(String deprioritizing_node_type) {
        this.deprioritizing_node_type = deprioritizing_node_type;
    }

    public List<String> getDeprioritizing_missions() {
        return deprioritizing_missions;
    }

    public void setDeprioritizing_missions(List<String> deprioritizing_missions) {
        this.deprioritizing_missions = deprioritizing_missions;
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
        return ("Deprioritization reason: " + this.deprioritization_reason + ", deprioritizing Node: " +
                this.deprioritizing_node_type + ", deprioritizing missions: " + this.deprioritizing_missions +
                ", deprioritzation targets: " + this.target_actions + ", Multiplier: " + this.scalar_multiplier);
    }
}
