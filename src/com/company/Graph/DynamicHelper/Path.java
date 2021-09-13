package com.company.Graph.DynamicHelper;

import com.company.Graph.Edge;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import com.company.SatOrbitProbagation;
import org.hipparchus.util.FastMath;

import java.util.ArrayList;

public class Path {

    ArrayList<Edge> path = new ArrayList<>();
    QuantumBitTransmitanceCalculator calc = new QuantumBitTransmitanceCalculator();

    public Path(Edge e){
        path.add(e);
    }
    public Path(ArrayList<Edge> e){
        path.addAll(e);
    }
    public Path addEdge(Edge e) throws Exception {
        if(getLastEdge().getDataStart().isBefore(e.getDataStart())) {
            Edge tE = getLastEdge();
            path.remove(path.size()-1); //remove last edge so it can be "redesigned"
            path.addAll(calculateBestTransition(tE,e));
        }
        else{
            throw new Exception("New Edge Must be after the last one (Check Data start and stuff)");
        }
        return this;
    }
    /*
        Gives back 2 Edges, first one is the last of the current path before the new edge, second one is the new edge recalculated
    */
    private ArrayList<Edge> calculateBestTransition(Edge _v1,Edge _v2){
        Edge v1 = new Edge(_v1);
        Edge v2 = new Edge((_v2));
        ArrayList<Edge> out = new ArrayList<>();
        double delta = v1.getDataEnd().durationFrom(v2.getDataStart());
        if(delta>0){
            while (delta>0){
                double sv1 = v1.getDurationScaledWithTransmitance();// the size * transmitance
                double sv2 = v2.getDurationScaledWithTransmitance();
                if(sv1>sv2){
                    v1.getOrbitData().popLastData();
                } else if(sv1<sv2){
                    v2.getOrbitData().popFirstData();
                } else {
                    if (v1.getLastTransmittance() > v2.getFirstTransmittance()) {
                        v1.popLastData();

                    } else {
                        v2.popFirstData();
                    }

                }


                delta-= SatOrbitProbagation.stepT;
            }
            out.add(v1);
            out.add(v2);
        }else {
            out.add(v1);
            out.add(v2);
        }



        return out ;
    }

    public Edge getLastEdge(){
        return path.get(path.size()-1);
    }
/*
    Outdated use overall transmitance for choosing path's
 */
    public double _computeBest(){
        double curB=path.get(0).getDataDuration();
        for(int i = 0 ; i < path.size()-1;i++){ // Feltetelezzuk hogy sorrendben vannak az intervallumok idorend szerint (Kezdes)!
            Edge first = path.get(i);
            Edge next = path.get(i+1);
            double a = first.getDataDuration();
            double b = next.getDataDuration();
            double delta = first.getDataEnd().durationFrom(next.getDataStart());
            double Tr = 0;
            if(delta>0){
                if(Math.abs((a-b) )>delta){
                    Tr= Math.min(a,b);
                } else if(b>a) {
                    Tr=b-delta;
                } else {
                    Tr = a-delta;
                }
            } else {
                Tr= Math.min(a,b);
            }

            curB = Math.min(curB, Tr);
        }
        return curB;
    }

    /*
        Really resource heavy, TODO: speed up
     */
    public double computeOverallTransmittance(){
        double outTR =0;
        for(Edge e: path){
            outTR += e.getDurationScaledWithTransmitance();
        }
        return outTR;
    }

    public double qbitsGenerated(){
        double Tr = this._computeBest();

        Edge first = path.get(0);
        Edge last = this.getLastEdge();
        double min;
        double curMin=0;
        for(int i =0;i<Tr;i++){
            curMin+= calcQbitCity(first.getOrbitData().Distance.get(i),first.getOrbitData().Angle.get(i),0);
        }
        min = curMin;
        curMin = 0;
        for(int i =0;i<Tr;i++){
            curMin+= calcQbitCity(last.getOrbitData().Distance.get(i),last.getOrbitData().Angle.get(i),0);
        }
        min = FastMath.min(min,curMin);


        return min;
    }


    public double calcQbitSat(double distance){
        return calc.calculateQBITSUMSat(distance);
    }

    public double calcQbitCity(double distance, double elevation,int dir){ // dir 0 -> ; dir 2 <-
        return calc.calculateQBITSUMCity(elevation,distance*FastMath.sin(FastMath.toRadians(elevation)),dir);
    }

    public Path generateNewWith(Edge edge) throws Exception {
        return new Path(path).addEdge(edge);
    }

    public ArrayList<Edge> getPath() {
        return new ArrayList<>(path);
    }

    public double getDur() {
        return getLastEdge().getDataEnd().durationFrom(path.get(0).getDataStart());
    }

}
