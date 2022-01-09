package gpup.servlets;

import ODT.Graph;

import java.util.HashMap;
import java.util.Map;

public class GraphManger {
    private Map<String,Graph> graphs = new HashMap<>();

    public synchronized void addGraph(Graph newGraph){graphs.put(newGraph.getGraphName(),newGraph);}

    public synchronized Map<String,Graph> getGraph() {
        return graphs;
    }
    public synchronized Graph getGraphByName(String name)throws Exception{
        if(!graphs.containsKey(name))
            throw new Exception("graph name not exsit");
        return graphs.get(name);
    }
}
