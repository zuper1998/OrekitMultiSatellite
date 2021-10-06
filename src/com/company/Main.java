package com.company;

import Data.SimValues;
import com.company.Graph.Graph;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;
import com.company.dataManagement.dataExporter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        SatOrbitProbagation.loadStuff();

        if (SimValues.IsSim) {
            Graph g = new Graph();
            g.GenerateGraph(SatOrbitProbagation.Generate());
            g.loadFromFile();
            //This part can be made to threads, Yay
            ExecutorService ex = Executors.newFixedThreadPool(SimValues.concurentThreads);

            for (City c1 : SimValues.cities) {
                ex.submit(new ThreadedRun(g, c1.name));

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
        try {
            SimValues.calc.set(new QuantumBitTransmitanceCalculator());
            System.out.printf("Starting %s%n", c1);
            g.printBest(c1);
            System.out.printf("Done %s%n", c1);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}