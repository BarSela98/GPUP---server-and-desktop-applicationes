package gpup.servlets;

import ODT.Graph;

import java.util.HashMap;
import java.util.Map;

public class GraphManger {
    Map<String,Graph> graphs = new HashMap<>();

    public void addGraph(Graph newGraph){graphs.put(newGraph.getGraphName(),newGraph);}

    public Map<String,Graph> getGraph() {
        return graphs;
    }
    public Graph getGraphByName(String name)throws Exception{
        if(!graphs.containsKey(name))
            throw new Exception("graph name not exsit");
        System.out.println("--------------- " + graphs.get(name));
        return graphs.get(name);
    }
}
