package models;

import com.fasterxml.jackson.databind.ObjectMapper;
import utils.jsonObjectModels.TimeStep;
import java.io.File;
import java.io.IOException;

/**
 * SimulatedDOMS Singleton object. The only part of the DOMS which is simulated is the updating of observations
 * through time steps. For the purpose of the demo, we
 */
public class SimulatedDOMS {

    private static SimulatedDOMS INSTANCE = null;
    private static File scenarioScriptFile = null;

    private static TimeStep[] timeSteps;

    private SimulatedDOMS() {
        // Open simulated script to read
        String fileName;

        do {
            fileName = "configs/scenarioScript.json";
            scenarioScriptFile = new File(fileName);
        } while (!scenarioScriptFile.exists());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.timeSteps = objectMapper.readValue(scenarioScriptFile, TimeStep[].class);
        } catch (IOException e) {
            System.out.println("ERROR READING FROM JSON. SIMULATED JSON WILL NOT WORK");
            e.printStackTrace();
        }

        System.out.println("Successfully read from scenario file: " + fileName);

    }

    public static synchronized SimulatedDOMS getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SimulatedDOMS();

        return INSTANCE;
    }

    /**
     * For demo purposes, will return json object as part of an iteration
     *
     * @return
     */
    public static TimeStep reportFilteredStateToCCM(int iteration) {

        // Return null for no end of scenario
        SimulatedDOMS.getInstance();
        if (getTimeSteps().length <= iteration)
            return null;


        SimulatedDOMS.getInstance();
        return getTimeSteps()[iteration];
    }


    public static TimeStep[] getTimeSteps() {
        return timeSteps;
    }

}
