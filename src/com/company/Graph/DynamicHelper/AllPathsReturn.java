package com.company.Graph.DynamicHelper;

import java.util.ArrayList;

public class AllPathsReturn {
    Path best;
    ArrayList<Path> otherPaths;

    public AllPathsReturn(Path p, ArrayList<Path> o){
        best = p;
        otherPaths=o;
    }


    public void print(String color) {
        best.getPath().forEach(edge -> edge.printColor(color));
        for(Path p : otherPaths){
            p.getPath().forEach(edge -> edge.printColor("Black"));
        }
    }
}
