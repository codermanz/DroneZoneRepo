package models;

import javax.json.JsonObject;
import java.io.File;
import java.util.Scanner;

/**
 * SimulatedDOMS Singleton object. The only part of the DOMS which is simulated is the updating of observations
 * through time steps. For the purpose of the demo, we
 */
public class SimulatedDOMS {

    private static SimulatedDOMS INSTANCE = null;

    private static File scenarioScriptFile = null;



    private SimulatedDOMS() {
        // Open simulated script to read
        Scanner scanner = new Scanner(System.in);
        String fileName;

        do {
            System.out.println("Enter the file path of scenario Script to read from relative to the following " +
                    "directory: " + System.getProperty("user.dir"));
            fileName = scanner.nextLine();
            scenarioScriptFile = new File(fileName);
        } while (!scenarioScriptFile.exists());

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
    protected static JsonObject reportFilteredStateToCCM() {
        // TODO: Read a time step from scnearioScriptFile then return that JSON file

        // Return null for no end of scenario
        return null;
    }

}
