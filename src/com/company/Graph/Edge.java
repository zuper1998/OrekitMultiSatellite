package com.company.Graph;

import Data.SimValues;
import com.company.IntervalData;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import org.hipparchus.util.FastMath;
import org.orekit.time.AbsoluteDate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

import static Data.SimValues.stepT;

public class Edge implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Node start;
    Node end;
    EdgeData data;

    public Node getEndNode() {
        return end;
    }
    public Node getStartNode(){return start;}

    /**
     * @param s   Start node
     * @param e   End Node
     * @param ds  Start date
     * @param de  End Date
     * @param dat Data for the edge timeline
     */
    public Edge(Node s, Node e, AbsoluteDate ds, AbsoluteDate de, IntervalData dat) {
        start = s;
        end = e;
        data = new EdgeData(ds, de, dat);
    }

    /**
     * @param e The Edge that should be copied
     */
    public Edge(Edge e) {
        start = e.start;
        end = e.end;
        data = new EdgeData(e.data);

    }

    public String getEdgeWay(){
        return String.format("%s-%s",start.name,end.name);
    }

    /**
     * @return the duration scaled with the transmittance of the timeline
     */
    public double getDurationScaledWithTransmitance() {
        double out = 0;

        if (start.isCity() || end.isCity()) { //its a cit
            for (int i = 0; i < getOrbitData().Angle.size(); i++) {
                double a = getOrbitData().Angle.get(i);
                double d = getOrbitData().Distance.get(i);
                int dir = 0;
                if (end.isCity())
                    dir = 2;
                out += SimValues.calc.get().calculateTransmitanceCity(a, d * FastMath.sin(FastMath.toRadians(a)), dir) * stepT;
            }
        } else {
            for (Double d : getOrbitData().Distance) {
                out += SimValues.calc.get().calculateTransmitanceSat(d) * stepT;
            }
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge) {
            Edge outer = ((Edge) o);
            return outer.start.equals(start) && outer.end.equals(end) && outer.data.equals(data);
        }
        return false;
    }

    public AbsoluteDate getDataStart() {
        return data.start;
    }

    public AbsoluteDate getDataEnd() {
        return data.end;
    }

    public double getDataDuration() {
        return data.duration;
    }

    public IntervalData getOrbitData() {
        return data.orbitData;
    }

    public void print() {
        String out = String.format("%s->%s [label=%f]", start.name, end.name, data.duration);
        System.out.println(out);
    }

    public void printNoLabel() {
        String out = String.format("%s->%s", start.name, end.name);
        System.out.println(out);
    }

    public void printColor(String color) {
        String out = String.format("%s->%s [color=%s]", start.name, end.name, color);
        System.out.println(out);

    }

    public void printColorAndLabel(String color) {
        String out = String.format("%s->%s [color=%s label=\"%f seconds\"];", start.name, end.name, color, data.duration / 60);
        System.out.println(out);
    }

    public void printColorLabelDurationFromStart(int index, AbsoluteDate s) {
        if (index > 12) index = 12;
        ColorsForEdge color = ColorsForEdge.values()[index];

        String out = String.format("\"%s\"->\"%s\" [color=%s label=\"Dur: %.1fs, TimeSinceStart:%.1f [min] \"];", start.name, end.name, color, data.duration, data.start.durationFrom(s) / 60);
        System.out.println(out);
    }

    public void printCostumLabel(int index, String label) {
        ColorsForEdge color = ColorsForEdge.values()[index];

        String out = String.format("\"%s\"->\"%s\" [color=%s label=\" %s \"];", start.name, end.name, color, label);
        System.out.println(out);
    }


    public void SaveColorAndThrougput(String color, double duration, double Tr, BufferedWriter writer) throws IOException {
        String out = String.format("%s->%s [color=%s label=\" Total Dur: %.1f %n TR: %.1f \"]", start.name, end.name, color, duration, Tr);
        writer.append(out);
        writer.newLine();
    }

    public void printColorThrougputAndUsedPercent(String color, double duration, double Tr) {
        String out = String.format("%s->%s [color=%s label=\" Transmittance: %.1f, duration: %.1f seconds %n total duration usage: %.1f%% \"]", start.name, end.name, color, Tr, duration, duration / getDataDuration() * 100);
        System.out.println(out);
    }

    public void SaveColorTransmitanceDurationAndPercentUsed(String color, double duration, double Tr, BufferedWriter writer) throws IOException {
        String out = String.format("%s->%s [color=%s label=\" Overall Transmittance: %.1f, duration: %.1f seconds %n Transmittance usage %.1f%% Duration usage: %.1f%% \"]", start.name, end.name, color, Tr, duration, Tr/this.getDurationScaledWithTransmitance()*100, duration / getDataDuration() * 100);
        writer.append(out);
        writer.newLine();
    }


    /**
     * Prints data in the following format: Angle Distance Transmitance
     * @param writer
     */
    public void printData(BufferedWriter writer) throws IOException {
        if(end.isCity()||start.isCity()){
            for (int i = 0; i < getOrbitData().Angle.size(); i++) {
                double a = getOrbitData().Angle.get(i);
                double d = getOrbitData().Distance.get(i);
                int dir = 0;
                if (end.isCity())
                    dir = 2;
                double tr =  SimValues.calc.get().calculateTransmitanceCity(a, d * FastMath.sin(FastMath.toRadians(a)), dir) * stepT;
                writer.append(String.format("%.3f %.3f %.9f%n",a,d,tr));
            }
        } else {
            for (Double d : getOrbitData().Distance) {
                double tr = SimValues.calc.get().calculateTransmitanceSat(d) * stepT;
                writer.append(String.format("%.3f %.3f%n",d,tr));
            }
        }
    }

    /**
     * @return the Transmittance of the first element in the list
     */
    public double getFirstTransmittance() {

        if (getOrbitData().Distance.isEmpty()) {
            return 0;
        }
        if (start.isCity() || end.isCity()) { //its a cit
            double a = getOrbitData().Angle.get(0);
            double d = getOrbitData().Distance.get(0);
            int dir = 0;
            if (end.isCity())
                dir = 2;
            return SimValues.calc.get().calculateTransmitanceCity(a, d * FastMath.sin(FastMath.toRadians(a)), dir);
        } else {
            return SimValues.calc.get().calculateTransmitanceSat(getOrbitData().Distance.get(0));
        }
    }

    /**
     * @return the Transmittance of the last element in the list
     */
    public double getLastTransmittance() {

        if (getOrbitData().Distance.isEmpty()) {
            return 0;
        }
        if (start.isCity() || end.isCity()) { //its a cit
            double a = getOrbitData().Angle.get(getOrbitData().Angle.size() - 1);
            double d = getOrbitData().Distance.get(getOrbitData().Distance.size() - 1);
            int dir = 0;
            if (end.isCity())
                dir = 2;
            return SimValues.calc.get().calculateTransmitanceCity(a, d * FastMath.sin(FastMath.toRadians(a)), dir);
        } else {
            return SimValues.calc.get().calculateTransmitanceSat(getOrbitData().Distance.get(0));
        }
    }


    public void popLastData() {
        getOrbitData().popLastData();
        data.end = data.end.shiftedBy(-1 * stepT);
        data.recalcDur();

    }

    public void popFirstData() {
        getOrbitData().popFirstData();
        data.start = data.start.shiftedBy(stepT);
        data.recalcDur();

    }

    private static class EdgeData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        public AbsoluteDate start;
        public AbsoluteDate end;
        public double duration;
        public IntervalData orbitData;

        public EdgeData(EdgeData ed) {
            start = ed.start;
            end = ed.end;
            duration = ed.duration;
            orbitData = new IntervalData(ed.orbitData);
        }

        /**
         * @param s   Start date
         * @param e   End date
         * @param dat Data for the timeline
         */
        public EdgeData(AbsoluteDate s, AbsoluteDate e, IntervalData dat) {
            start = s;
            end = e;
            duration = e.durationFrom(s);
            orbitData = dat;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof EdgeData) {
                EdgeData outer = ((EdgeData) o);
                return outer.start.equals(start) && outer.end.equals(end);
            }
            return false;
        }

        public void recalcDur() {
            duration = end.durationFrom(start);
        }
    }
}
