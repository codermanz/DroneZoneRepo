package models;

public class SimulatedDOMS {

    private static SimulatedDOMS INSTANCE = null;



    private SimulatedDOMS() {

    }

    public static synchronized SimulatedDOMS getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SimulatedDOMS();

        return INSTANCE;
    }

}
