from gremlin_python import statics
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.anonymous_traversal import traversal
from gremlin_python.structure.graph import Graph
from tools.converter import *
from tools.scenario import *
statics.load_statics(globals())


# Establish remote connection
graph = Graph()
connection = DriverRemoteConnection('ws://localhost:8182/gremlin', 'g')
g = traversal().withRemote(connection)

# Build graph based on some scenario.csv
g = build_graph(g, './data/scenario_example.csv')

# Convert graph Traversal -> graphml
graphml_file = g2graphml(g)

# Store graphml file
with open('./output/graph.graphml', "w") as f:
    f.write(graphml_file)
