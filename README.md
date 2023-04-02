# DroneZoneRepo
Computational Cognitive Model for SRI International Rapid HMI Project

Currently you can create a graph on a remote janusgraph in memory instance and store that graph locally on your computer in graphML format.
You can open and visualize this file with Gephi. 

## Setup
1. Download and unzip janusgraph: DroneZoneRepo/janusgraph-0.6.3/... 
2. Configure Janusgraph Server
3. add nodes/edges/properties to your graph in tools/scenario.py
4. Define output directory and remote settings in main.py
5. Start janusgraph-server:
   - $ sh ./janusgraph-0.6.3/bin/janusgraph-server.sh start
6. Open file.graphml in Gephi for visualization