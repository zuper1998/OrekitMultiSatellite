package com.company.Graph.DynamicHelper;

import Data.SimValues;
import com.company.Graph.Edge;
import com.company.Graph.Node;
import org.orekit.time.AbsoluteDate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class adaptEdge implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Edge edge;
    int start = 0, end = 0;

    public adaptEdge(Edge toAdapt) {
        edge = toAdapt;
    }

    public adaptEdge(adaptEdge ae){
        edge = ae.edge;
        start=ae.start;
        end=ae.end;
    }

    public AbsoluteDate getDataStart() {
        return edge.getDataStart().shiftedBy(start);
    }

    public AbsoluteDate getDataEnd() {
        return edge.getDataEnd().shiftedBy(-end);
    }

    private adaptEdge setStart(int s) {
        start = s;
        return this;
    }

    private adaptEdge setEnd(int e) {
        end = e;
        return this;
    }

    public double getDurationScaledWithTransmitance() {
        double out = 0;
        if (edge.getOrbitData().Transmittance == null) {
            edge.genTransmittance();
        }
        for (int i = start; i < edge.getOrbitData().Distance.size() - end; i++) {
            out += edge.getOrbitData().Transmittance.get(i);
        }

        return out;
    }

    public void popLastData() {
        end++;
    }

    public void popFirstData() {
        start++;
    }

    public double getLastTransmittance() {
        return edge.getOrbitData().Transmittance.get(end);
    }

    public double getFirstTransmittance() {
        return edge.getOrbitData().Transmittance.get(start);
    }

    public double Duration() {
        return (edge.getOrbitData().Distance.size() - (end + start)) * SimValues.stepT;
    }

    public void printData(BufferedWriter writer) throws IOException {
        edge.printData_adater(writer,start,end);
    }

    public Node getEndNode() {
        return edge.getEndNode();
    }

    public Node getStartNode() {
        return edge.getStartNode();
    }

    public void SaveColorTransmitanceDurationAndPercentUsed(String Cname, double computeBestDuration, double computeBestTransmittance, BufferedWriter writer) throws IOException {
        String out = String.format("%s->%s [color=%s label=\" Csatorna maximális optikai átersztése: %.1f, időtartam: %.1f [mp] %n Optikai áteresztés kihasználtsága %.1f%% Időtartam kihasználtsága: %.1f%% \", taillabel=\"%.1f [mp]\",headlabel=\"%.1f [mp]\"]",
                edge.start.name, edge.end.name, Cname, computeBestTransmittance, computeBestDuration, computeBestTransmittance / getDurationScaledWithTransmitance() * 100, computeBestDuration / Duration() * 100,
                getDataStart().durationFrom(SimValues.initialDate), getDataEnd().durationFrom(SimValues.initialDate));

        writer.append(out);
        writer.newLine();

    }

    public void SaveColorAndThrougput(String black, double computeBestDuration, double computeBestTransmittance, BufferedWriter writer) throws IOException {
        String out = String.format("%s->%s [color=%s label=\" Overall Transmittance: %.1f, duration: %.1f seconds %n Transmittance usage %.1f%% Duration usage: %.1f%% \", taillabel=\"%.1f\",headlabel=\"%.1f\"]",
                edge.start.name, edge.end.name, black, computeBestTransmittance, computeBestDuration, computeBestTransmittance / getDurationScaledWithTransmitance() * 100, computeBestDuration / Duration() * 100,
                getDataStart().durationFrom(SimValues.initialDate), getDataEnd().durationFrom(SimValues.initialDate));

        writer.append(out);
        writer.newLine();
    }

    public ArrayList<Double> getTransmittance(){
        ArrayList<Double> out = new ArrayList<>();
        for(int i=start;i<edge.getOrbitData().Transmittance.size()-end;i++){
            out.add(edge.getOrbitData().Transmittance.get(i));
        }
        return out;
    }


    public adaptEdge genMinimal() {
        Edge min = new Edge(edge);
        min.start = new Node(min.start.name);
        min.end = new Node(min.end.name);
        return new adaptEdge(min).setStart(start).setEnd(end);
    }
}
