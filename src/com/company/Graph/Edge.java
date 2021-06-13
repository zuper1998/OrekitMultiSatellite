package com.company.Graph;

import org.hipparchus.analysis.function.Abs;
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

    public AbsoluteDate getDataStart(){
        return data.start;
    }
    public AbsoluteDate getDataEnd(){
        return data.end;
    }
    public double getDataDuration(){
        return data.duration;
    }
    public void print() {
        String out = String.format("%s->%s [label=%f]",start.name,end.name,data.duration);
        System.out.println(out);
    }
    public void printNoLabel() {
        String out = String.format("%s->%s",start.name,end.name);
        System.out.println(out);
    }

    public void printColor(String color) {
        String out = String.format("%s->%s [color=%s]",start.name,end.name,color);
        System.out.println(out);

    }

    public void printColorAndLabel(String color){
        String out = String.format("%s->%s [color=%s label=\"%f seconds\"];",start.name,end.name,color,data.duration/60);
        System.out.println(out);
    }
    public void printColorLabelDurationFromStart(int index, AbsoluteDate s){
        ColorsForEdge color = ColorsForEdge.values()[index];

        String out = String.format("\"%s V%d\"->\"%s V%d\" [color=%s label=\"Dur: %.1fs, TimeSinceStart:%.1f [min] \"];",start.name,index,end.name,index,color,data.duration,data.start.durationFrom(s)/60);
        System.out.println(out);
    }

    private class EdgeData {
        public AbsoluteDate start;
        public AbsoluteDate end;

        public double duration;
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
