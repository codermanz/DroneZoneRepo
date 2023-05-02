package models;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import utils.Mapping;
import utils.jsonObjectModels.Observation;
import utils.jsonObjectModels.TimeStep;

import javax.json.JsonObject;
import javax.json.JsonValue;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class ComputationalCognitiveModel {

    // Singleton Computation Cognitive Model Singleton Instance
    private static ComputationalCognitiveModel INSTANCE = null;
    private static Mapping mapping = null;
    private static Operator operatorModel = null;
    private static GraphTraversalSource g;


    /**
     * Constructor of Cognitive Model. Private to ensure Singletonness
     * Constructor will instantiate graph model and go through set up for defining
     */
    private ComputationalCognitiveModel(JsonObject jsonObj) {
        // TODO: Define obv space


        // Define mapping between: action -> mission, action -> observations, observations -> UI Components
        Mapping map = new Mapping();

//        ArrayList<Vertex> v = new ArrayList<>();

        // Create agents
//        for(int i = 0; i < 4; i++){
//            v.add(g.addV("drone").property("id", i+1).next());
//        }
//
//        v.add(g.addV("action").property("name", "drop pack").
//                property("mission", "identify").next());
//        v.add(g.addV("action").property("name", "search").
//                property("mission", "scan").next());
//
//        Iterator<String> it = jsonObj.getJsonObject("$defs").keySet().iterator();
//        while(it.hasNext()){
//            String i = it.next();
//            //create obv nodes and map thier edges
//            v.add(g.addV("observation").property("name", i).next());
//            String type = jsonObj.getJsonObject(i).getString("type");
//            switch(type){
//                case "target":
//                    g.addE("solved with").from(v.get(v.size()-1)).to(v.get(4)).iterate();
//                    break;
//                case "movement":
//                    g.addE("solved with").from(v.get(v.size()-1)).to(v.get(5)).iterate();
//                    break;
//            }
//        }
        
        // Create Operator object
        operatorModel = Operator.getInstance();

        // Create and connect to janus graph instance
        this.g = traversal().
                    withRemote(DriverRemoteConnection.using("localhost",8182,"g"));
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
//        System.out.println(timeStep);
        // Read from model as a transaction
        Transaction tx = g.tx();
        GraphTraversalSource gtx = tx.begin();

        try {
            // Write all observations to the graph
            for (Observation obv : timeStep.getObservations()) {
                // Create agents
                GraphTraversal<Vertex, Vertex> agent = g.V().hasLabel("agent")
                        .has("agent", "name", obv.getAgent_id());
                Vertex agentVert;
                // Create agent node if not already exist
                if (!agent.hasNext()) {
                    agent = g.addV("agent").property("name", obv.getAgent_id());
                }
                agentVert = agent.next();

                // Create observation vertex
                GraphTraversal<Vertex, Vertex> observation = g.addV("observation").
                        property("obv_type", obv.getObv_type());
                Vertex obvVert;
                obv.getAttributes().forEach(x -> observation.property(x.getAttribute_Name(), x.getAttribute_Value()));
                obvVert = observation.next();
                // TODO: Should auto create weighed relation between observation to actions/UI components

                // Create edge between agent and observation
                g.addE("reports").from(agentVert).to(obvVert).property("weight", obv.getEdge_weight()).
                        property("timestamp", obv.getEdge_timestamp()).iterate();

            }
            tx.commit();
            gtx.close();

        }  catch (Exception ex) {
            tx.rollback();
            ex.printStackTrace();
        }

        System.out.println("-->Update the model - invoke this function when ");
        renderFrontEnd();
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

    public static Mapping getMapping() {
        return mapping;
    }

    public static void setMapping(Mapping mapping) {
        ComputationalCognitiveModel.mapping = mapping;
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