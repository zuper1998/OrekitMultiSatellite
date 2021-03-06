package com.company.Graph;

import Data.SimValues;
import com.company.City;
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
    public String name;
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
    public boolean stringEquals(Node n){
        return name.equals(n.name);
    }


    public boolean isCity() {
        for(City c : SimValues.cities){
            if(c.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
}
