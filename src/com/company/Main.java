package com.company;

import com.company.Graph.Graph;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        SatOrbitProbagation.loadStuff();
        g.GenerateGraph(SatOrbitProbagation.Generate());
        g.loadFromFile();
        g.printBest("Budapest", "NewYork");
        //g.printAllEdges("Budapest","Berlin");

    }
}
