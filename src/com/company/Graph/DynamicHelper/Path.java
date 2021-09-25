package com.company.Graph.DynamicHelper;

import Data.SimValues;
import com.company.Graph.Edge;
import com.company.Graph.Node;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import org.hipparchus.util.FastMath;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import static Data.SimValues.stepT;

public class Path {

    ArrayList<Edge> path = new ArrayList<>();
    QuantumBitTransmitanceCalculator calc = SimValues.calc;

    public Path(Edge e) {
        path.add(e);
    }

    public Path(ArrayList<Edge> e) {
        path.addAll(e);
    }

    public Path addEdge(Edge e) throws Exception {
        if (e.getDataEnd().isAfter(getLastEdge().getDataStart()) && !this.containsNode(e.getEndNode())) {
            Edge tE = getLastEdge();
            path.remove(path.size() - 1); //remove last edge so it can be "redesigned"
            ArrayList<Edge> nPair = calculateBestTransition(tE, e);
            if (nPair.isEmpty()) return null;
            path.addAll(nPair);
        } else {
            throw new Exception("New Edge Must be after the last one, AND must note contain a node already visited (Check Data start and stuff)");
        }
        return this;
    }

    public boolean containsNode(Node node) {
        for (Edge e : path) {
            if (e.getEndNode().stringEquals(node)) {
                return true;
            }
        }
        return false;
    }

    public boolean trimToWindowSize() {
        double delta = getDur() - SimValues.MAX_TIME;
        Edge v1 = path.get(0);
        Edge v2 = getLastEdge();
        return EdgeTrimmer(delta, v1, v2);


    }

    public boolean isSameKind(Path p) {
        for (int i = 0; i < FastMath.min(path.size(), p.path.size()); i++) {
            Node a = path.get(i).getEndNode();
            Node b = p.path.get(i).getEndNode();
            if (!a.stringEquals(b))
                return false;
        }
        return true;
    }

    private boolean EdgeTrimmer(double delta, Edge v1, Edge v2) {
        while (delta > 0) {
            double sv1 = v1.getDurationScaledWithTransmitance();// the size * transmitance
            double sv2 = v2.getDurationScaledWithTransmitance();
            if (sv1 > sv2) {
                v1.popLastData();
            } else if (sv1 < sv2) {
                v2.popFirstData();
            } else {
                if (v1.getLastTransmittance() < v2.getFirstTransmittance()) {
                    v2.popFirstData();
                } else {
                    v1.popLastData();
                }

            }
            if (v1.getOrbitData().Distance.size() < SimValues.MIN_WINDOW || v2.getOrbitData().Distance.size() < SimValues.MIN_WINDOW) {
                return false;
            }
            delta -= stepT;

        }
        return true;
    }

    /*
        Gives back 2 Edges, first one is the last of the current path before the new edge, second one is the new edge recalculated
    */
    private ArrayList<Edge> calculateBestTransition(Edge _v1, Edge _v2) {
        Edge v1 = new Edge(_v1);
        Edge v2 = new Edge((_v2));
        ArrayList<Edge> out = new ArrayList<>();
        double delta = v1.getDataEnd().durationFrom(v2.getDataStart());
        boolean retVal = true;
        if (delta > 0) {
            retVal = EdgeTrimmer(delta, v1, v2);
        }
        if (retVal) {
            out.add(v1);
            out.add(v2);
        }

        return out;
    }

    public Edge getLastEdge() {
        return path.get(path.size() - 1);
    }

    /*
        Outdated use overall transmitance for choosing path's
     */
    public double _computeBest() {
        double curB = path.get(0).getDataDuration();
        for (int i = 0; i < path.size() - 1; i++) { // Feltetelezzuk hogy sorrendben vannak az intervallumok idorend szerint (Kezdes)!
            Edge first = path.get(i);
            Edge next = path.get(i + 1);
            double a = first.getDataDuration();
            double b = next.getDataDuration();
            double delta = first.getDataEnd().durationFrom(next.getDataStart());

            double Tr = 0;
            if (delta > 0) {
                if (Math.abs((a - b)) > delta) {
                    Tr = Math.min(a, b);
                } else if (b > a) {
                    Tr = b - delta;
                } else {
                    Tr = a - delta;
                }
            } else {
                Tr = Math.min(a, b);
            }

            curB = Math.min(curB, Tr);

        }

        return curB;
    }

    public double computeOverallTransmittance() {
        double outTR = 0;
        for (Edge e : path) {
            outTR += e.getDurationScaledWithTransmitance();
        }
        return outTR;
    }
    /*
        Get Minimal transmittance over the edge
     */
    public double computeBestTransmittance() {
        double min = 0;
        for (Edge e : path) {
            if (min == 0) {
                min = e.getDurationScaledWithTransmitance();
            } else {
                min = FastMath.min(min,e.getDurationScaledWithTransmitance());
            }
        }


        return min;
    }

    public double qbitsGenerated() {
        double Tr = this._computeBest();

        Edge first = path.get(0);
        Edge last = this.getLastEdge();
        double min;
        double curMin = 0;
        for (int i = 0; i < Tr; i++) {
            curMin += calcQbitCity(first.getOrbitData().Distance.get(i), first.getOrbitData().Angle.get(i), 0);
        }
        min = curMin;
        curMin = 0;
        for (int i = 0; i < Tr; i++) {
            curMin += calcQbitCity(last.getOrbitData().Distance.get(i), last.getOrbitData().Angle.get(i), 0);
        }
        min = FastMath.min(min, curMin);


        return min;
    }


    public double calcQbitSat(double distance) {
        return calc.calculateQBITSUMSat(distance);
    }

    public double calcQbitCity(double distance, double elevation, int dir) { // dir 0 -> ; dir 2 <-
        return calc.calculateQBITSUMCity(elevation, distance * FastMath.sin(FastMath.toRadians(elevation)), dir);
    }

    public Path generateNewWith(Edge edge) throws Exception {
        return new Path(getPath()).addEdge(edge);
    }

    public ArrayList<Edge> getPath() {
        return new ArrayList<>(path);
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }


    public void SaveData(BufferedWriter writer) throws IOException {
        for(int i = 0;i<path.size();i++){
            path.get(i).printData(writer);
            if(i!=path.size()-1){
                double delta =  path.get(i+1).getDataStart().durationFrom(path.get(i).getDataEnd());
                if(delta>0){
                    for(int k = 0;k<delta;k++){
                        writer.append("0.0 0.0 0.0");
                    }
                }
            }
        }
    }

    /**
     * @return the total time of the path
     */
    public double getDur() {

        return getLastEdge().getDataEnd().durationFrom(path.get(0).getDataStart());
    }

}
