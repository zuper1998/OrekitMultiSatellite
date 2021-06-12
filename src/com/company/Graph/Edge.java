package com.company.Graph;

import org.orekit.time.AbsoluteDate;

public class Edge {
    Node start;
    Node end;
    EdgeData data;

    public Edge(Node s, Node e, AbsoluteDate ds, AbsoluteDate de) {
        start = s;
        end = e;
        data = new EdgeData(ds,de);
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Edge){
            Edge outer = ((Edge)o);
            return outer.start.equals(start) && outer.end.equals(end) && outer.data.equals(data);
        }
        return false;
    }

    private class EdgeData {
        AbsoluteDate start;
        AbsoluteDate end;
        double duration;
        public EdgeData(AbsoluteDate s, AbsoluteDate e){
            start = s;
            end = e;
            duration = e.durationFrom(s);
        }
        @Override
        public boolean equals(Object o){
            if(o instanceof EdgeData){
                EdgeData outer = ((EdgeData)o);
                return outer.start.equals(start) && outer.end.equals(end);
            }
            return false;
        }
    }
}
