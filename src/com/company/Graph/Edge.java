package com.company.Graph;

import com.company.IntervalData;
import org.hipparchus.analysis.function.Abs;
import org.hipparchus.distribution.IntegerDistribution;
import org.orekit.time.AbsoluteDate;

import java.io.Serial;
import java.io.Serializable;

public class Edge implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Node start;
    Node end;
    EdgeData data;

    public Edge(Node s, Node e, AbsoluteDate ds, AbsoluteDate de, IntervalData dat) {
        start = s;
        end = e;
        data = new EdgeData(ds, de,dat);
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
        if(index>12) index =12;
        ColorsForEdge color = ColorsForEdge.values()[index];

        String out = String.format("\"%s\"->\"%s\" [color=%s label=\"Dur: %.1fs, TimeSinceStart:%.1f [min] \"];",start.name,end.name,color,data.duration,data.start.durationFrom(s)/60);
        System.out.println(out);
    }
    public void printCostumLabel(int index, String label){
        ColorsForEdge color = ColorsForEdge.values()[index];

        String out = String.format("\"%s\"->\"%s\" [color=%s label=\" %s \"];",start.name,end.name,color,label);
        System.out.println(out);
    }


    public void printColorAndThrougput(String color, double Tr){
        String out = String.format("%s->%s [color=%s label=\" %.1f \"]",start.name,end.name,color,Tr);
        System.out.println(out);
    }

    private static class EdgeData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        public AbsoluteDate start;
        public AbsoluteDate end;
        public double duration;
        public IntervalData orbitData;
        public EdgeData(AbsoluteDate s, AbsoluteDate e, IntervalData dat){
            start = s;
            end = e;
            duration = e.durationFrom(s);
            orbitData = dat;
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
