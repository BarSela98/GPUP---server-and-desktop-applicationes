package graph;

import xml.XmlException;

/// send error message if the xml file is corrupt
public class GraphIsExists extends XmlException { // the xml file is corrupt -

    private String message;

    public GraphIsExists(String name){
        super(name);
        message = "Graph Is Exists";
    }

    @Override
    public String toString() {
        return  super.toString() + message + "\n\r";
    }

}
