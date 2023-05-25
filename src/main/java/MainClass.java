import models.ComputationalCognitiveModel;
import models.SimulatedDOMS;
import utils.jsonObjectModels.TimeStep;

import java.io.IOException;
import java.util.Scanner;

public class MainClass {

    public static void main(String[] args) throws IOException {

        // Create models
        ComputationalCognitiveModel ccm = ComputationalCognitiveModel.getInstance();
        SimulatedDOMS simulatedDOMS = SimulatedDOMS.getInstance();
        // TODO: Create another Simulated UI Component that streams in actions taken by user

        // Other vars
        Scanner scanner = new Scanner(System.in);
        int iteration = 0;
        String input = "";

        // Continue reading script from simulated DOMS until it runs out or user says -1
        while (input != "-1") {

            TimeStep timeStep = simulatedDOMS.reportFilteredStateToCCM(iteration);
            if (timeStep == null)
                break;

            ccm.updateModel(timeStep);


            input = scanner.nextLine();
            iteration++;
        }

        System.out.println("FINISHED SCRIPT - GOODBYE");

    }
}
