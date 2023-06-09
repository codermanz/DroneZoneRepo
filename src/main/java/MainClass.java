import java.io.FileReader;
import models.ComputationalCognitiveModel;
import models.SimulatedDOMS;
import models.SimulatedUIStream;
import utils.jsonObjectModels.TimeStep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import models.Operator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.jsonObjectModels.UserAction;
import java.util.List;

public class MainClass {

    public static void main(String[] args) throws IOException, ParseException, ExecutionException, InterruptedException {

        // Create models
        ComputationalCognitiveModel ccm = ComputationalCognitiveModel.getInstance();
        SimulatedDOMS simulatedDOMS = SimulatedDOMS.getInstance();
        SimulatedUIStream uiStream = SimulatedUIStream.getInstance();
        Operator op = ccm.getOperatorModel();
        // Other vars
        ArrayList<String> screenshots = new ArrayList<String>();
        JSONArray js = (JSONArray) new JSONParser().parse(new FileReader("configs/operator.json"));
        
        Scanner scanner = new Scanner(System.in);
        int iteration = 0;
        String input = "";

        // Continue reading script from simulated DOMS until it runs out or user says -1
        while (input != "-1") {

            TimeStep timeStep = simulatedDOMS.reportFilteredStateToCCM(iteration);
            UserAction userActionForTimestep = uiStream.reportAction(iteration);

            // Update model according to action if action was received
            if (userActionForTimestep != null)
                ccm.updateModel(userActionForTimestep);

            // If no observations for this time step, quit
            if (timeStep == null)
                continue;

            // Update model with new observations
            ccm.updateModel(timeStep);

            JSONObject json = (JSONObject) js.get(iteration);
            Long stress = (Long) json.get("stress");
            Long attentiveness = (Long) json.get("attentiveness");
            String old = op.getCurrentOperatorState();
            op.updateStress(stress);
            op.updatedAttentiveness(attentiveness);
            if(!op.getCurrentOperatorState().equals(old)) {
                ccm.updateModel();
            }
            String outnodes = GetTopXUINodes.getTopNodes(iteration+1);
            screenshots.add(outnodes);
            input = scanner.nextLine();
            iteration++;
        }

        System.out.println("FINISHED SCRIPT - GOODBYE");
        for (String nodes: screenshots){
            System.out.println(nodes);
        }

    }
}
