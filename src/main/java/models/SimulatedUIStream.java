package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import utils.jsonObjectModels.UserAction;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SimulatedUIStream {

    private static SimulatedUIStream INSTANCE = null;

    private static UserAction[] actionStream;

    private SimulatedUIStream() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.actionStream = objectMapper.readValue("configs/actionStream", UserAction[].class);
        } catch (IOException e) {
            System.out.println("ERROR READING FROM JSON. SIMULATED UI WONT WORK");
            e.printStackTrace();
        }
    }

    public static synchronized SimulatedUIStream getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SimulatedUIStream();

        return INSTANCE;
    }

    public static UserAction reportAction(int iteration) {
        for (UserAction act : actionStream) {
            if (act.getTimestamp() == iteration) {
                return (act);
            }
        }

        return null;
    }

    public static UserAction[] getActionStream() {
        return actionStream;
    }
}
