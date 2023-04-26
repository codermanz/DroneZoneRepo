import models.ComputationalCognitiveModel;
import models.SimulatedDOMS;

public class MainClass {

    private static int iteration;
    private ComputationalCognitiveModel ccm = ComputationalCognitiveModel.getInstance();
    private SimulatedDOMS simulatedDOMS = SimulatedDOMS.getInstance();

    public static void main(String[] args) {

        // TODO: For each time step, call simulatedDOM's nextTimeStep function, then pass the returned Json
        // object to CCM's update model. Continue doing this until reportFilteredStateToCCM returns a null

    }

}
