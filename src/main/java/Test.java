/**
 * Sample code for connecting to gremlin server. Is used in tandem with air routes dataset from the book.
 * Please ignore.
 */

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;

import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;
import static org.apache.tinkerpop.gremlin.process.traversal.P.neq;

import org.apache.tinkerpop.gremlin.driver.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import static org.apache.tinkerpop.gremlin.structure.T.*;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.javatuples.Tuple;

class Test{
    public static void main(String[] args) throws Exception {
        GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using("localhost",8182,"g"));


        // add mapping
        Mapping missionActionMapping = new Mapping();
        g = missionActionMapping.createGraph(g);

    }
}