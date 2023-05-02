import models.ComputationalCognitiveModel;
import models.SimulatedDOMS;
import utils.jsonObjectModels.TimeStep;

import java.io.IOException;
import java.util.Scanner;

public class MainClass {

//    private static int iteration;
//    private ComputationalCognitiveModel ccm = ComputationalCognitiveModel.getInstance();
//    private SimulatedDOMS simulatedDOMS = SimulatedDOMS.getInstance();

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        ComputationalCognitiveModel ccm = ComputationalCognitiveModel.getInstance();
        SimulatedDOMS simulatedDOMS = SimulatedDOMS.getInstance();
        int iteration = 0;
        String input = "";

        // TODO: For each time step, call simulatedDOM's nextTimeStep function, then pass the returned Json
        // object to CCM's update model. Continue doing this until reportFilteredStateToCCM returns a null
        while (input != "-1") {

            TimeStep timeStep = simulatedDOMS.reportFilteredStateToCCM(iteration);
            ccm.updateModel(timeStep);

            input = scanner.nextLine();
            iteration++;
        }
    }
}
