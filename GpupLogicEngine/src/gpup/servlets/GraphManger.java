package gpup.servlets;

import ODT.Graph;
import graph.GraphIsExists;

import java.util.HashMap;
import java.util.Map;

public class GraphManger {
    private Map<String,Graph> graphs = new HashMap<>();

    public synchronized void addGraph(Graph newGraph) throws GraphIsExists {
        if (!graphs.containsKey(newGraph.getGraphName()))
            graphs.put(newGraph.getGraphName(),newGraph);
        else{throw new GraphIsExists(newGraph.getGraphName());
        }
    }

    public synchronized Map<String,Graph> getGraph() {
        return graphs;
    }
    public synchronized Graph getGraphByName(String name)throws Exception{
        if(!graphs.containsKey(name))
            throw new Exception("graph name not exist");
        return graphs.get(name);
    }
}
