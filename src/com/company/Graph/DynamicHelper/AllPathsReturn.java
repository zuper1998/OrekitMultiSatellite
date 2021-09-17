package com.company.Graph.DynamicHelper;

import com.company.Graph.ColorsForEdge;
import com.company.Graph.Edge;

import java.util.ArrayList;

public class AllPathsReturn {
    Path best;
    ArrayList<Path> otherPaths;

    public AllPathsReturn(Path p, ArrayList<Path> o){
        best = p;
        otherPaths=o;
    }

    public Path getBest(){
        return best;
    }
    public void print(int index) {
        if(index>14) index =14;
        ColorsForEdge color = ColorsForEdge.values()[index];
        best.getPath().forEach(edge -> edge.printColorThrougputAndUsedPercent(color.name(),best._computeBest(),best.computeOverallTransmittance()));
        for(Path p : otherPaths){
            p.getPath().forEach(edge -> edge.printColorAndThrougput("Black",p._computeBest(),p.computeOverallTransmittance()));
        }
    }

    public void printEdgeData() {
        best.getPath().forEach(Edge::printData);
    }
}
