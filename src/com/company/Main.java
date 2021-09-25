package com.company;

import Data.SimValues;
import com.company.Graph.Graph;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        Graph g = new Graph();
        SatOrbitProbagation.loadStuff();
        g.GenerateGraph(SatOrbitProbagation.Generate());
        g.loadFromFile();
        //This part can be made to threads, Yay
        Semaphore sem = new Semaphore(SimValues.concurentThreads);
        ArrayList<ThreadedRun> tr= new ArrayList<>();
        for (City c1 : SimValues.cities) {
            for (City c2 : SimValues.cities) {
                if(!c1.name.equals(c2.name))
                tr.add(new ThreadedRun(g,c1.name,c2.name,sem));
            }
        }
        for(ThreadedRun t : tr){
            t.start();
        }
        //g.printAllEdges("Budapest","Berlin");

    }


}

//For multi threading we have to remake the saving part of the data :)
class ThreadedRun extends Thread {
    Semaphore semaphore;
    String c1,c2;
    Graph g;
    ThreadedRun(Graph _g, String _c1,String _c2, Semaphore s){
        c1=_c1;
        c2=_c2;
        semaphore=s;
        g=_g;
    }
    public void run(){
        try {
            semaphore.acquire();
            System.out.println("Lock acquired");
            g.printBest(c1,c2);
            semaphore.release();
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }




}