package com.company;

import com.company.Graph.Graph;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        SatOrbitProbagation.loadStuff();

        //g.GenerateGraph(SatOrbitProbagation.Generate(),"Budapest","NewYork");
        g.loadFromFile();
        g.printBest("Budapest","NewYork");
        //g.printAllEdges("Budapest","Berlin");


	    //TODO: generate visibility graph with weights
        //TODO: do the flow maximalization for the graph
    }
}
