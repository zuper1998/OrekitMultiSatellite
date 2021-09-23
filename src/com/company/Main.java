package com.company;

import com.company.Graph.Graph;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        SatOrbitProbagation.loadStuff();
        g.GenerateGraph(SatOrbitProbagation.Generate());
        g.loadFromFile();
        //This part can be made to threads, Yay
        g.printBest("Budapest", "NewYork");
        g.printBest("Budapest", "Berlin");
        g.printBest("Copenhagen","Berlin");
        //g.printAllEdges("Budapest","Berlin");

    }
}
