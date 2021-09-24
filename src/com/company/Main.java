package com.company;

import Data.SimValues;
import com.company.Graph.Graph;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        SatOrbitProbagation.loadStuff();
        g.GenerateGraph(SatOrbitProbagation.Generate());
        g.loadFromFile();
        //This part can be made to threads, Yay

        for(City c1 : SimValues.cities) {
            for (City c2 : SimValues.cities) {
                g.printBest(c1.name, c2.name);
            }
        }
        //g.printAllEdges("Budapest","Berlin");

    }
}
