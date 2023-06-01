package utils.ConfigClasses;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.MessageSerializer;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV3d0;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class Connection {
    private Cluster cluster;
    private GraphTraversalSource gts;
    private DriverRemoteConnection drc;

    public GraphTraversalSource createConnection()
    {
        Map config = new HashMap();
        config.put("ioRegistries", Arrays.asList("org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry"));
        MessageSerializer serializer = new GraphSONMessageSerializerV3d0();
        serializer.configure(config, null);

        Cluster.Builder builder = Cluster.build();
        builder.addContactPoint("localhost");
        builder.port(8182);
        builder.enableSsl(false);
        builder.serializer(serializer);
        this.cluster = builder.create();
        this.drc = DriverRemoteConnection.using(cluster);
        this.gts = traversal().withRemote(drc);

        return(gts);
    }
}