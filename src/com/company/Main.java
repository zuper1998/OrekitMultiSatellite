package com.company;

import com.company.Graph.Graph;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        g.GenerateGraph(SatOrbitProbagation.Generate(),"Budapest","Berlin");
	    //TODO: Generate Visibility Timeline
	    //TODO: generate visibility graph with weights
    }
}
