package com.company.Graph.DynamicHelper;

import com.company.Graph.ColorsForEdge;

import java.util.ArrayList;

public class AllPathsReturn {
    Path best;
    ArrayList<Path> otherPaths;

    public AllPathsReturn(Path p, ArrayList<Path> o){
        best = p;
        otherPaths=o;
    }


    public void print(int index) {
        if(index>14) index =14;
        ColorsForEdge color = ColorsForEdge.values()[index];
        best.getPath().forEach(edge -> edge.printColorAndThrougput(color.name(),best.computeBest()));
        for(Path p : otherPaths){
            p.getPath().forEach(edge -> edge.printColorAndThrougput("Black",p.computeBest()));
        }
    }

}
