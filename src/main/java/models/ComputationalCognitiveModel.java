package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.gremlin.driver.RequestOptions;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import utils.Mapping;
import utils.ConfigClasses.Connection;
import utils.jsonObjectModels.*;
import utils.ConfigClasses.Setup;
import javax.json.JsonObject;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import utils.Decays;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inV;

public class ComputationalCognitiveModel {

    // Singleton Computation Cognitive Model Singleton Instance
    private static ComputationalCognitiveModel INSTANCE = null;
    private static Mapping missionActionMapping;
    private static Mapping actionObservationMapping;
    private static Mapping observationUIMapping;
    private static Mapping actionUIMapping;
    private static Prioritization[] prioritizations;
    private static Deprioritization[] deprioritizations;
    private static Operator operatorModel = null;
    private static GraphTraversalSource g;


    /**
     * Constructor of Cognitive Model. Private to ensure Singletonness
     * Constructor will instantiate graph model and go through set up for defining
     */
    private ComputationalCognitiveModel(JsonObject jsonObj) throws ExecutionException, InterruptedException, JsonProcessingException {

        // Create and connect to janusgraph instance + add float-schema for observation weights
        Connection connection = new Connection();
        this.g = connection.createConnection();

        // TODO: Get into the .json files and think about / edit the attributes so that they make sense
        // create initial graph with mission action and component nodes from json-scripts
        Setup componentNodeSetup = new Setup("./configs/vertexScripts/componentScript.json", "ui-component");
        Setup missionNodeSetup = new Setup("./configs/vertexScripts/missionScript.json", "mission");
        Setup actionNodeSetup = new Setup("./configs/vertexScripts/actionScript.json", "action");

        componentNodeSetup.createNodes(g);
        missionNodeSetup.createNodes(g);
        actionNodeSetup.createNodes(g);

        // TODO: Get into the .conf files and think about / edit the mappings so that the mappings itself, the weights,
        //  edge attributes etc. make sense
        // define mappings between certain types of vertices
        missionActionMapping = new Mapping("./configs/mappings/default-mission-action-mapping.conf");
        actionObservationMapping = new Mapping("./configs/mappings/default-action-observation-mapping.conf");
        observationUIMapping = new Mapping("./configs/mappings/default-observation-ui-mapping.conf");
        actionUIMapping = new Mapping("./configs/mappings/default-action-ui-mapping.conf");

        // Read in prioritization and deprioritizations lists
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            prioritizations = objectMapper.readValue(new File("configs/prioritizations.json"), Prioritization[].class);
            deprioritizations = objectMapper.readValue(new File("configs/deprioritizations.json"), Deprioritization[].class);
        } catch (IOException e) {
            System.out.println("COULDNT READ FROM PRIOTIZATION AND DEPRIOTIZATION");
            e.printStackTrace();
        }
        /*
            Update graph based on the defined static mappings: (create edges)
                - mission - action mapping
                - action - ui component mapping
        */
        missionActionMapping.updateGraph(g, missionNodeSetup.getCreatedVertices());
        actionUIMapping.updateGraph(g, actionNodeSetup.getCreatedVertices());
        
        // Create Operator object
        operatorModel = Operator.getInstance();
    }

    @Override
    protected void finalize() throws Throwable {
        g.close();
        super.finalize();
    }

    /**
     * Implements singletoness of this class. Will return a
     * @return
     */
    public static synchronized ComputationalCognitiveModel getInstance() throws ExecutionException, InterruptedException, JsonProcessingException {
        JsonObject json = null;
        if (INSTANCE == null)
            INSTANCE = new ComputationalCognitiveModel(json);

        return INSTANCE;
    }

    /**
     * This method should be called anytime there should be any update in the CCM. Currently the two identified
     * times when the model may need to be updated is:
     *  - Operator model changes
     *  - New observation in the world state (for our demo, this is when we change timesteps)
     *
     *  This function specifically should be called when needing to update the model due to operator model changing
     */
    public static void updateModel() {
        System.out.println("-->Update the model - invoke this function when model needs to be updated due to " +
                "change in operator model");
        // Report new observations as a transaction
        Transaction tx = g.tx();
        GraphTraversalSource gtx = tx.begin();
        for(Vertex v : gtx.V().toList()){
            gtx.V(v.id()).property("attentiveness", operatorModel.getAttentiveness()).property("stress", operatorModel.getStress()).hasNot("action").property("usefulness", operatorModel.getAttentiveness()*v.label().hashCode()).iterate();
        }
        renderFrontEnd();
    }

    /**
     * This method should be called anytime there should be any update in the CCM. Currently the two identified
     * times when the model may need to be updated is:
     *  - Operator model changes
     *  - New observation in the world state (for our demo, this is when we change timesteps)
     *
     *  This function specifically should be called when needing to update the model due to new observations from
     *  DOMS.
     *
     * @param timeStep - new observations from DOMS in the form of timeStep Object
     */
    public static void updateModel(TimeStep timeStep) {
        System.out.println(timeStep);
        // Report new observations as a transaction
        Transaction tx = g.tx();
        GraphTraversalSource gtx = tx.begin();
        // Maintain list of all types of observations for priotization framework
        List<String> typesOfNewObservations = new ArrayList<>();

        // Decay all current observation edges
        List<Edge> all_reported_edges = gtx.E().hasLabel("reports").toList();
        for (Edge reportedEdge : all_reported_edges) {
            double decayedValue = Double.parseDouble(gtx.E(reportedEdge.id()).valueMap().next().get("gewicht").toString().replaceAll("[a-zA-Z]", "")) * 0.8;
            gtx.E(reportedEdge.id()).property("gewicht", Double.toString(decayedValue)).iterate();
        }

        // Add all new observations
        try {
            List<Vertex> vertices = new ArrayList<>();
            // Write all observations to the graph
            for (Observation obv : timeStep.getObservations()) {
                // Create agents
                GraphTraversal<Vertex, Vertex> agent = gtx.V().hasLabel("agent")
                        .has("agent", "name", obv.getAgent_id());
                Vertex agentVert;
                // Create agent node if not already exist
                if (!agent.hasNext()) {
                    agent = gtx.addV("agent").property("name", obv.getAgent_id());
                }
                agentVert = agent.next();

                // Create observation vertex
                GraphTraversal<Vertex, Vertex> observation = gtx.addV("observation").
                        property("obv_type", obv.getObv_type());
                Vertex obvVert;
                obv.getAttributes().forEach(x -> observation.property(x.getAttribute_Name(), x.getAttribute_Value()));
                obvVert = observation.next();
                vertices.add(obvVert);
                // Add observation type to list
                typesOfNewObservations.add(obv.getObv_type());

                // Create edge between agent and observation
                GraphTraversal<Edge, Edge> edge = gtx.addE(obv.getEdge().getEdge_name()).from(agentVert).to(obvVert);
                obv.getEdge().getAttributes().forEach(x -> {
                    edge.property((String)x.getAttribute_Name(), (String)x.getAttribute_Value());
                        }
                );
                edge.iterate();

            }
            // Create mapping from action to observation
            actionObservationMapping.updateGraph(gtx, vertices);
            // Create mapping from observation to UI
            observationUIMapping.updateGraph(gtx, vertices);

            tx.commit();
            gtx.close();

        }  catch (Exception ex) {
            tx.rollback();
            ex.printStackTrace();
        }

        // Transaction for priotization framework updates
        tx = g.tx();
        gtx = tx.begin();
        try {
            // Prioritizations/deprioritizations
            prioritizingFramework(typesOfNewObservations, gtx);

            tx.commit();
            gtx.close();

        }  catch (Exception ex) {
            tx.rollback();
            ex.printStackTrace();
        }

        // Transaction for update weights framework
        tx = g.tx();
        gtx = tx.begin();
        try {

            // Update weights based on new observations
            updateWeights(gtx);

            tx.commit();
            gtx.close();

        }  catch (Exception ex) {
            tx.rollback();
            ex.printStackTrace();
        }

        System.out.println("-->Update the model - invoke this function when ");
        renderFrontEnd();
    }

    public static void updateModel(UserAction actionTaken) {
        Transaction tx = g.tx();
        GraphTraversalSource gtx = tx.begin();

        // Transaction for prioritization/deprioritizations
        try {
            // Prioritizations/Deprioritzations
            prioritizingFramework(actionTaken.getActions_taken(), gtx);

            tx.commit();
            gtx.close();
        }  catch (Exception ex) {
            tx.rollback();
            ex.printStackTrace();
        }

        // Transcation to update wieghts
        tx = g.tx();
        gtx = tx.begin();
        try {

            // Update weights
            updateWeights(gtx);

            tx.commit();
            gtx.close();

        }  catch (Exception ex) {
            tx.rollback();
            ex.printStackTrace();
        }
    }


    public static void prioritizingFramework(List<String> listOfPrioritizationInfluencers, GraphTraversalSource gtx) {
        for(String act : listOfPrioritizationInfluencers) {
            for(Prioritization p : prioritizations){
                if(p.getPrioritizing_node_type().equals(act)){
                    for(String mission : p.getPrioritizing_missions()){
                        for(String a :p.getTarget_actions()){
                            List<Edge> outgoingEdges = gtx.V().has("mission_id", mission).outE("requires for completion").filter(inV().has("action_id", a)).toList();
                            for (Edge edge : outgoingEdges) {
                                double weight = Double.parseDouble(gtx.E(edge.id()).valueMap().next().get("gewicht").toString().replaceAll("[a-zA-Z]", ""));
                                gtx.E(edge.id()).property("gewicht", Double.toString(weight*p.getScalar_multiplier())).iterate();
                            }
                        }
                    }
                }
            }
            for(Deprioritization p : deprioritizations){
                if(p.getDeprioritizing_node_type().equals(act)){
                    for(String mission : p.getDeprioritizing_missions()){
                        for(String a :p.getTarget_actions()){
                            List<Edge> outgoingEdges = gtx.V().has("mission_id", mission).outE("requires for completion").filter(inV().has("action_id", a)).toList();
                            for (Edge edge : outgoingEdges) {
                                double weight = Double.parseDouble(gtx.E(edge.id()).valueMap().next().get("gewicht").toString().replaceAll("[a-zA-Z]", ""));
                                gtx.E(edge.id()).property("gewicht", Double.toString(weight*p.getScalar_multiplier())).iterate();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This function updates relevant edge. It should update the following weights in the following sequence:
     *  - Outgoing edges from all action nodes based on in-weights from mission nodes
     *  - Outgoing edges from all observations. Based on in-weights from actions, and agents
     */
    public static void updateWeights(GraphTraversalSource gtx) {
        // IMPLEMENT AS SINGLE TRANSACTION -- THIS MAKES A NESTED TRANSACTION AND THUS REQUIRES some verification.

        GraphTraversal<Vertex, Vertex> actions = gtx.V().hasLabel("action");

        // For all action nodes in the graph, calculate in weight, and update all out going edges with this value
        while (actions.hasNext()) {
            double cumulativeWeight = 0;
            Vertex actionNode = actions.next();

            // Calculate cumulative in-weights based on all incoming edges
            List<Edge> incomingEdges = gtx.V(actionNode).inE().toList();
            for (Edge edge : incomingEdges)
                cumulativeWeight += Double.parseDouble(gtx.E(edge.id()).valueMap().next().get("gewicht").toString().replaceAll("[a-zA-Z]", ""));

            // Update all outgoing edges based on calculated in weights
            List<Edge> outgoingEdges = gtx.V(actionNode).outE().toList();
            for (Edge edge : outgoingEdges) {
                gtx.E(edge.id()).property("gewicht", Double.toString(cumulativeWeight)).iterate();
            }
        }

        GraphTraversal<Vertex, Vertex> observations = gtx.V().hasLabel("observation");

        // For all action nodes in the graph, calculate in weight, and update all out going edges with this value
        while (observations.hasNext()) {
            double cumulativeWeight = 0;
            Vertex observationNode = observations.next();

            // Calculate cumulative in-weights based on all incoming edges
            List<Edge> incomingEdges = gtx.V(observationNode).inE().toList();
            for (Edge edge : incomingEdges)
                cumulativeWeight += Double.parseDouble(gtx.E(edge.id()).valueMap().next().get("gewicht").toString().replaceAll("[a-zA-Z]", ""));

            // Update all outgoing edges based on calculated in weights
            List<Edge> outgoingEdges = gtx.V(observationNode).outE().toList();
            for (Edge edge : outgoingEdges) {
                gtx.E(edge.id()).property("gewicht", Double.toString(cumulativeWeight)).iterate();
            }
        }
    }

    /**
     * Rendering algorithm that creates a UI tree based on information present in CCM. This can be defined as a stretch
     * goal... The frontend should render whenever the model is updated. Therefore, this function should be called
     * at the end of the updateModel() function.
     *
     * // TODO: Open Question - do we want to allow other classes/objects to rerenderfrontend (hence changing private to
     *          perhaps public?) Currently, frontend should only be rerendered when there's an update to the model, else
     *          there should be no reason to rerender
     */
    private static void renderFrontEnd() {
        System.out.println("-->Render out front end based on model - define " +
            "'UI component' picking algorithm here");
    }

    public static Mapping getMissionActionMapping() {
        return missionActionMapping;
    }

    public static void setMissionActionMapping(Mapping missionActionMapping) {
        ComputationalCognitiveModel.missionActionMapping = missionActionMapping;
    }

    public static Mapping getActionObservationMapping() {
        return actionObservationMapping;
    }

    public static void setActionObservationMapping(Mapping actionObservationMapping) {
        ComputationalCognitiveModel.actionObservationMapping = actionObservationMapping;
    }

    public static Operator getOperatorModel() {
        return operatorModel;
    }

    public static GraphTraversalSource getG() {
        return g;
    }

    public static void setG(GraphTraversalSource g) {
        ComputationalCognitiveModel.g = g;
    }
}
