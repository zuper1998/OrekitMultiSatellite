package com.company.Graph.DynamicHelper;

import com.company.Graph.Edge;

import java.util.ArrayList;

public class Path {

    ArrayList<Edge> path = new ArrayList<>();

    public Path(Edge e){
        path.add(e);
    }
    public Path(ArrayList<Edge> e){
        path.addAll(e);
    }
    public Path addEdge(Edge e) throws Exception {
        if(getLastEdge().getDataStart().isBefore(e.getDataStart())) {
            path.add(e);
        }
        else{
            throw new Exception("New Edge Must be after the last one (Check Data start and stuff)");
        }
        return this;
    }

    public Edge getLastEdge(){
        return path.get(path.size()-1);
    }

    public double computeBest(){
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
