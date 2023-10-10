# DroneZoneRepo
Computational Cognitive Model for SRI International Rapid HMI Project.

For comprehensive report, please visit: [our whole report](https://docs.google.com/document/d/1hJVlqz7jKSFbTLHm1JTiDnr4scG1HQtZIvH4RJv0lwI/edit#heading=h.7frfm35z7c11)

Currently, you can create a graph on a remote janusgraph in memory instance and store that graph locally on your 
computer in graphML format. You can open and visualize this file with Gremlin visualizer.

## JanusGraph Setup


1. Download and unzip janusgraph: 
   DroneZoneRepo/janusgraph-0.6.3/...
2. Configure Janusgraph Server
   - Configure server in ```conf/gremlin-server/gremlin-server.yaml```
     - Specify storage system in this script. Default is ```conf/janusgraph-inmemory.properties```
   - Configure connection from gremlin console to janus graph in ```conf/remote.yaml```
   - Default script that's run when connecting gremlin console to gremlin server is ```scripts/empty-sample.groovy``` 
This script is specified in ```gremlin-server.yaml```
   - Refer to the following tutorial for detail: 
https://www.youtube.com/watch?v=ip5ctTXszN8&list=PL71i5-fJZYaEeCRqd_rWTMvbK8FrF4bwn&index=3
3. Start janusgraph-server:
```
./janusgraph-0.6.3/bin/janusgraph-server.sh start
```
4. Monitor logs by using (log name may vary depending on set up file):
```
tail -f logs/janusgraph.log
```


## Gremlin Console Setup
Please refer to the following tutorial: 
https://www.youtube.com/watch?v=ip5ctTXszN8&list=PL71i5-fJZYaEeCRqd_rWTMvbK8FrF4bwn&index=3



## Sample experiment



## Visualizer set up
Refer to the following tutorial: https://www.youtube.com/watch?v=N3ORM4ZIBXg&list=PL71i5-fJZYaEeCRqd_rWTMvbK8FrF4bwn&index=7

Visualizer repository: https://github.com/prabushitha/gremlin-visualizer

Prereqs:
- Janusgraph set up
- Gremlin (Janusgraph) server running
- Docker installed on system (optional)

You may set up the visualizer by cloning the git repo or downloading the docker image. For the sake of simplicity, the following
instructions show how to set up using docker:

1. Pull docker image
```
docker pull prabushitha/gremlin-visualizer:latest
```
2. Start docker container
```
docker run --rm -d -p 3000:3000 -p 3001:3001 --name=gremlin-visualizer --platform=linux/amd64 prabushitha/gremlin-visualizer:latest
```
3. Navigate to ```localhost:3000``` through your browser
4. Specify IP & port of gremlin server
   - NOTE: If you are using an inmemory janus graph and running the visualizer through a docker container, your server 
will be running on the host machine's localhost whereas the visualizer is running in the docker container (on a 
seperate network stack). To access the host's gremlin server from the container, specify IP as 
```host.docker.internal``` and the correct port.
