package com.company.Graph;

import com.company.SatTimeline;
import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;

public class Node {
    ArrayList<Edge> edges = new ArrayList<>();
    String name;
    public Node(String n){
        name = n;
    }
    public void addEdge (Node e, AbsoluteDate ds, AbsoluteDate de) {
        if(!edges.contains(new Edge(this,e,ds,de)))
        edges.add(new Edge(this,e,ds,de));
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Node){
            return ((Node)o).name.equals(name);
        }
        return false;
    }

    public void printDotEdges() {
        //TODO: print in dot format (graphviz)
    }
}
