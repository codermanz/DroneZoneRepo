package utils.jsonObjectModels;

import java.util.List;

public class UserAction {

    private List<String> actions_taken;
    private int timestamp;

    public UserAction() {}

    public UserAction(List<String> actions_taken, int timestamp) {
        this.actions_taken = actions_taken;
        this.timestamp = timestamp;
    }

    public List<String> getActions_taken() {
        return actions_taken;
    }

    public void setActions_taken(List<String> actions_taken) {
        this.actions_taken = actions_taken;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
