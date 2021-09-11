package com.company.Graph;

import com.company.IntervalData;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import org.hipparchus.util.FastMath;
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

    public  double getDurationScaledWithTransmitance() {
        double out=0;
        if(start.isCity()|| end.isCity()){ //its a cit
            for(int i = 0; i<getOrbitData().Angle.size(); i++){
                double a = getOrbitData().Angle.get(i);
                double d = getOrbitData().Distance.get(i);
                int dir = 0;
                if(end.isCity())
                dir = 2;
                out+=QuantumBitTransmitanceCalculator.calculateTransmitanceCity(a,d* FastMath.sin(FastMath.toRadians(a)),dir);
            }
        } else {
            for(Double d : getOrbitData().Distance){
                out+= QuantumBitTransmitanceCalculator.calculateTransmitanceSat(d);
            }
        }
        return out;
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
    public IntervalData getOrbitData(){return data.orbitData;}
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
    public void printColorThrougputAndUsedPercent(String color, double duration,double Tr){
        String out = String.format("%s->%s [color=%s label=\" Throughput: %.1f seconds %n total duration usage: %.1f%% \"]",start.name,end.name,color,Tr,duration/getDataDuration()*100);
        System.out.println(out);
    }

    public void printData() {
        if(data.orbitData.Angle !=null) {
            System.out.printf("# %s->%s%n",start.name,end.name);
            for (int i = 0; i < data.orbitData.Angle.size(); i++) {
                System.out.printf("%.3f %.3f%n",getOrbitData().Angle.get(i),getOrbitData().Distance.get(i));
            }
        }
    }

    public double getFirstTransmittance() {
        if(getOrbitData().Distance.isEmpty()){
            return 0;
        }
        if(start.isCity()|| end.isCity()){ //its a cit
                double a = getOrbitData().Angle.get(0);
                double d = getOrbitData().Distance.get(0);
                int dir = 0;
                if(end.isCity())
                    dir = 2;
                return QuantumBitTransmitanceCalculator.calculateTransmitanceCity(a,d* FastMath.sin(FastMath.toRadians(a)),dir);
            }else {
            return QuantumBitTransmitanceCalculator.calculateTransmitanceSat(getOrbitData().Distance.get(0));
        }
    }

    public double getLastTransmittance() {
        if(getOrbitData().Distance.isEmpty()){
            return 0;
        }
        if(start.isCity()|| end.isCity()){ //its a cit
            double a = getOrbitData().Angle.get(getOrbitData().Angle.size()-1);
            double d = getOrbitData().Distance.get(getOrbitData().Distance.size()-1);
            int dir = 0;
            if(end.isCity())
                dir = 2;
            return QuantumBitTransmitanceCalculator.calculateTransmitanceCity(a,d* FastMath.sin(FastMath.toRadians(a)),dir);
        }else {
            return QuantumBitTransmitanceCalculator.calculateTransmitanceSat(getOrbitData().Distance.get(0));
        }
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
