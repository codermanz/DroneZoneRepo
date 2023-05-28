package models;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import utils.Mapping;
import utils.jsonObjectModels.Observation;
import utils.jsonObjectModels.TimeStep;
import utils.Setup;
import javax.json.JsonObject;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import utils.jsonObjectModels.UserAction;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class ComputationalCognitiveModel {

    // Singleton Computation Cognitive Model Singleton Instance
    private static ComputationalCognitiveModel INSTANCE = null;
    private static Mapping missionActionMapping;
    private static Mapping actionObservationMapping;
    private static Mapping observationUIMapping;
    private static Mapping actionUIMapping;
    private static Operator operatorModel = null;
    private static GraphTraversalSource g;


    /**
     * Constructor of Cognitive Model. Private to ensure Singletonness
     * Constructor will instantiate graph model and go through set up for defining
     */
    private ComputationalCognitiveModel(JsonObject jsonObj) {
        // Create and connect to janus graph instance
        this.g = traversal().
                withRemote(DriverRemoteConnection.using("localhost",8182,"g"));

        // TODO: Define obv and action space

        // TODO: Get into the .json files and think about / edit the attributes so that they make sense
        // create initial graph with mission action and component nodes from json-scripts
        Setup componentNodeSetup = new Setup("./configs/componentScript.json", "ui-component");
        Setup missionNodeSetup = new Setup("./configs/missionScript.json", "mission");
        Setup actionNodeSetup = new Setup("./configs/actionScript.json", "action");

        componentNodeSetup.createNodes(g);
        missionNodeSetup.createNodes(g);
        actionNodeSetup.createNodes(g);


        // TODO: Get into the .conf files and think about / edit the mappings so that the mappings itself, the weights,
        //  edge attributes etc. make sense
        // define mappings between certain types of vertices
        missionActionMapping = new Mapping("./configs/default-mission-action-mapping.conf");
        actionObservationMapping = new Mapping("./configs/default-action-observation-mapping.conf");
        observationUIMapping = new Mapping("./configs/default-observation-ui-mapping.conf");
        actionUIMapping = new Mapping("./configs/default-action-ui-mapping.conf");


        /* Update graph based on the defined static mappings: (create edges)
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
     * Implements singletonness of this class. Will return a
     * @return
     */
    public static synchronized ComputationalCognitiveModel getInstance() {
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

        // TODO: Decay all current observation edges --- CODE NOT VERIFIED
//        GraphTraversal<Edge, Edge> all_reported_edges = gtx.E().hasLabel("reports");
//        while(all_reported_edges.hasNext()) {
//            Edge edge = all_reported_edges.next();
//            edge.property("weight", Integer.parseInt(edge.value("weight")) * 0.8);
//        }

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

                // Create edge between agent and observation
                GraphTraversal<Edge, Edge> edge = gtx.addE(obv.getEdge().getEdge_name()).from(agentVert).to(obvVert);
                obv.getEdge().getAttributes().forEach(x -> edge.property(x.getAttribute_Name(), x.getAttribute_Value()));
                edge.iterate();

            }
            // Create mapping from action to observation
            actionObservationMapping.updateGraph(gtx, vertices);
            // Create mapping from observation to UI
            observationUIMapping.updateGraph(gtx, vertices);

            // Update weights based on new observations
            updateWeights();

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

        // TODO: See if any action taken triggers an activation or deactivation

        // TODO: Make the activation or deactivation based on edges

        // TODO: Update weights
        updateWeights();

    }

    /**
     * This function updates relevant edge. It should update the following weights in the following sequence:
     *  - Outgoing edges from all action nodes based on in-weights from mission nodes
     *  - Outgoing edges from all observations. Based on in-weights from actions, and agents
     */
    public static void updateWeights() {
        // IMPLEMENT AS SINGLE TRANSACTION

        // TODO: For each action node, calculate in weight then update all out edges from the node
            // Query all in edges and sum them to calculate in weight
            // If no in edges, inweight = 0
            // Query all out edges and update them

        // TODO: For each observation, calculate in weight then update all out edges from the node
            // Query all in edges and sum them to calculate in weight
            // Query all out edges and update them

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
