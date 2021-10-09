package com.company.Graph.DynamicHelper;

import Data.SimValues;
import com.company.Graph.Edge;
import com.company.Graph.Node;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import org.hipparchus.util.FastMath;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import static Data.SimValues.entanglementGenHz;
import static Data.SimValues.stepT;

public class Path implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    ArrayList<adaptEdge> path = new ArrayList<>();

    public Path(Edge e) {
        path.add(new adaptEdge(e));
    }

    public Path(ArrayList<adaptEdge> e) {
        for(adaptEdge ae : e){
            path.add(new adaptEdge(ae));
        }
    }

    public Path addEdge(Edge e) throws Exception {
        if (e.getDataEnd().isAfter(getLastEdge().getDataStart()) && !this.containsNode(e.getEndNode())) {
            adaptEdge tE = getLastEdge();
            path.remove(path.size() - 1); //remove last edge so it can be "redesigned"
            ArrayList<adaptEdge> nPair = calculateBestTransition(tE, new adaptEdge(e));
            if (nPair.isEmpty()) return null;
            path.addAll(nPair);
        } else {
            throw new Exception("New Edge Must be after the last one, AND must note contain a node already visited (Check Data start and stuff)");
        }
        return this;
    }

    public boolean containsNode(Node node) {
        for (adaptEdge e : path) {
            if (e.getStartNode().stringEquals(node)) {
                return true;
            }
        }
        return false;
    }

    public boolean trimToWindowSize() {
        double delta = getDur() - SimValues.MAX_TIME;
        adaptEdge v1 = path.get(0);
        adaptEdge v2 = getLastEdge();
        if(((v1.Duration()+v2.Duration())<delta))
            return false;
        return EdgeTrimmer(delta, v1, v2);


    }



    private boolean EdgeTrimmer(double delta, adaptEdge v1, adaptEdge v2) {
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
            if (v1.Duration() < SimValues.MIN_WINDOW || v2.Duration() < SimValues.MIN_WINDOW) {
                return false;
            }
            delta -= stepT;

        }
        return true;
    }

    /*
        Gives back 2 Edges, first one is the last of the current path before the new edge, second one is the new edge recalculated
    */
    private ArrayList<adaptEdge> calculateBestTransition(adaptEdge v1, adaptEdge v2) {
        ArrayList<adaptEdge> out = new ArrayList<>();
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

    public adaptEdge getLastEdge() {
        return path.get(path.size() - 1);
    }
    public adaptEdge getFirstEdge() {
        return path.get(0);
    }
    /*
        Outdated use overall transmitance for choosing path's
     */
    public double _computeBest() {
        double curB = path.get(0).Duration();
        for (adaptEdge e : path) { // Feltetelezzuk hogy sorrendben vannak az intervallumok idorend szerint (Kezdes)!
            curB = FastMath.min(e.Duration(),curB);
        }

        return curB;
    }

    /*
        Get Minimal transmittance over the edge
     */
    public double computeBestTransmittance() {
        double min = 0;
        for (adaptEdge e : path) {
            if (min == 0) {
                min = e.getDurationScaledWithTransmitance();
            } else {
                min = FastMath.min(min,e.getDurationScaledWithTransmitance());
            }
        }


        return min;
    }




    public Path generateNewWith(Edge edge) throws Exception {
        return new Path(path).addEdge(edge);
    }

    public ArrayList<adaptEdge> getPath() {
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
                        writer.newLine();
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


    public Path Strip() {
        ArrayList<adaptEdge> newPath = new ArrayList<>();
        for(adaptEdge e : path){
            newPath.add(e.genMinimal());
        }
        path = newPath;
        return this;
    }

    public ArrayList<Double> getEbits() {
        double maxEbits=this.computeBestTransmittance();
        ArrayList<Double> out = new ArrayList<>();
        for(Double tr : getLastEdge().getTransmittance()){
            if(maxEbits<0){
                break;
            }
            maxEbits-=tr;
            out.add(tr);
        }

        return out;
    }


}
