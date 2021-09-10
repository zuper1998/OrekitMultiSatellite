package com.company.Graph;

import com.company.IntervalData;
import com.company.SatTimeline;
import org.orekit.time.AbsoluteDate;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    ArrayList<Edge> edges = new ArrayList<>();
    String name;
    public Node(String n){
        name = n;
    }
    public void addEdge (Node e, AbsoluteDate ds, AbsoluteDate de, IntervalData dat) {
        Edge toAdd = new Edge(this,e,ds,de,dat);
        if(!edges.contains(toAdd))
        edges.add(toAdd);
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Node){
            return ((Node)o).name.equals(name);
        }
        return false;
    }

    public void printDotEdges() {
        for(Edge e : edges){
            e.print();
        }
    }
    public void printDotEdgesNoLabel() {
        for(Edge e : edges){
            e.printNoLabel();
        }
    }

    public boolean isCity() {
        return name.equals("NewYork")||name.equals("Budapest"); //TODO: make nicer way for this
    }
}
