package models;

public class ComputationalCognitiveModel {

    private static ComputationalCognitiveModel INSTANCE = null;


    /**
     * Constructor of Cognitive Model. Private to ensure Singletonness
     * Constructor will instantiate graph model and go through set up for defining
     */
    private ComputationalCognitiveModel() {
        // TODO: Define obv space
        // TODO: Define action space
        // TODO: Define mapping between two classes

    }

    /**
     * Implements singletonness of this class. Will return a
     * @return
     */
    public static synchronized ComputationalCognitiveModel getInstance() {

        if (INSTANCE == null)
            INSTANCE = new ComputationalCognitiveModel();

        return INSTANCE;
    }

    protected static void updateModel() {
        System.out.println("Update the model - invoke this function when ");
    }

}
