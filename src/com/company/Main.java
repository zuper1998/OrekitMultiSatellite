package com.company;

import Data.SimValues;
import com.company.Graph.Graph;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import com.company.dataManagement.dataExporter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        SatOrbitProbagation.loadStuff();

        if (SimValues.IsSim) {
            Graph g = new Graph();
            g.GenerateGraph(SatOrbitProbagation.Generate());
            g.loadFromFile();
            ExecutorService ex = Executors.newFixedThreadPool(SimValues.concurentThreads);

            SimValues.calc.set(new QuantumBitTransmitanceCalculator());
            List<ThreadedRun> t = new ArrayList<>();
            g.calculateAllTransmittance();
            for (City c1 : SimValues.cities) {

                    //System.err.printf("Starting %s%n", c1.name);
                    //g.printBest(c1.name);
                    //System.err.printf("Done %s%n", c1.name);
                    ex.submit(new ThreadedRun(g, c1.name));

            }
            if(ex.isTerminated()){
                ex.shutdown();
            }
        } else {
            SimValues.calc.set(new QuantumBitTransmitanceCalculator());

            dataExporter.exportALL();

        }


    }


}

//For multi threading we have to remake the saving part of the data :)
class ThreadedRun implements Runnable {
    String c1;
    Graph g;

    ThreadedRun(Graph _g, String _c1) {
        c1 = _c1;
        g = _g;
    }

    public void run() {
            SimValues.calc.set(new QuantumBitTransmitanceCalculator());
            System.err.printf("Starting %s%n", c1);
            g.printBest(c1);
            System.err.printf("Done %s%n", c1);
            }


}