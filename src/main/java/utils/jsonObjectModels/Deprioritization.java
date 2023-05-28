package utils.jsonObjectModels;

import java.util.List;

public class Deprioritization {

    private String deactivation_reason;
    private String deactivating_node_type;
    private List<String> deactivating_missions;
    private List<String> target_action;

    public Deprioritization() {

    }

    public Deprioritization(String deactivation_reason, String deactivating_node_type, List<String> deactivating_missions,
                            List<String> target_action) {
        this.deactivation_reason = deactivation_reason;
        this.deactivating_node_type = deactivating_node_type;
        this.deactivating_missions = deactivating_missions;
        this.target_action = target_action;
    }

    public String getDeactivation_reason() {
        return deactivation_reason;
    }

    public void setDeactivation_reason(String deactivation_reason) {
        this.deactivation_reason = deactivation_reason;
    }

    public String getDeactivating_node_type() {
        return deactivating_node_type;
    }

    public void setDeactivating_node_type(String deactivating_node_type) {
        this.deactivating_node_type = deactivating_node_type;
    }

    public List<String> getDeactivating_missions() {
        return deactivating_missions;
    }

    public void setDeactivating_missions(List<String> deactivating_missions) {
        this.deactivating_missions = deactivating_missions;
    }

    public List<String> getTarget_action() {
        return target_action;
    }

    public void setTarget_action(List<String> target_action) {
        this.target_action = target_action;
    }

    @Override
    public String toString() {
        return ("Deactivating reason: " + this.deactivation_reason + ", Deactivating Node: " +
                this.deactivating_node_type + ", Deactivating missions: " + this.deactivating_missions +
                ", Deactivating targets: " + this.target_action);
    }
}
