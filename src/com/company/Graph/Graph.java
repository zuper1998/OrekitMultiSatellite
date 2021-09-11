package com.company.Graph;

import com.company.*;
import com.company.Graph.DynamicHelper.AllPathsReturn;
import com.company.Graph.DynamicHelper.Path;
import com.sun.tools.jconsole.JConsoleContext;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.company.SatOrbitProbagation.MAX_TIME;

public class Graph {

    public Map<String,Node> nodes = new HashMap<>();


    public void GenerateGraph(Map<String, ArrayList<SatFlightData>> timelineMap, String city1, String city2) {

       for( Map.Entry<String, ArrayList<SatFlightData>> tlm :  timelineMap.entrySet()){
            nodes.put(tlm.getKey(),new Node(tlm.getKey()));
       }
       // add edges
       for( Map.Entry<String, ArrayList<SatFlightData>> tlm :  timelineMap.entrySet()){
           for(SatFlightData sfd : tlm.getValue()) {
               //for(TimeInterval t : sfd.Interval) {
               for(int i =0;i<sfd.Interval.size();i++){
                   //Error comes from the ground states not giving data
                   SatFlightData.SatFlightDataRetunVal satDat = sfd.getDataAt(i);
                   nodes.get(tlm.getKey()).addEdge(nodes.get(sfd.Dest),satDat.getTimeInterval().start,satDat.getTimeInterval().end,satDat.getIntervalData());
               }}
           }




        //Save data to dat.ser
        FileOutputStream fos = null;
        try {
            //fos = new FileOutputStream("/home/narcano/OrekitMultiSatellite/src/Data/dat.ser");
            fos = new FileOutputStream("C:\\Users\\Narcano\\IdeaProjects\\OrekitMultiSatellite\\src\\Data\\dat.ser");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(nodes);
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printAllEdges(String city1, String city2) {
        final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());

        System.out.println("digraph G{");
        System.out.println("layouit=dot");
        System.out.println("graph [ dpi = 300 ];");
        System.out.println("rankdir=LR;");
        System.out.println(city1);


        for (Map.Entry<String, Node> n : nodes.entrySet()) {
            if (!n.getKey().equals(city1) || !n.getKey().equals(city2)) {
                System.out.println(n.getKey());
            }

        }
        System.out.println(city2);
        int ind = 0;
        for (Map.Entry<String, Node> n : nodes.entrySet()) {
            for(Edge e : n.getValue().edges){
                e.printColorLabelDurationFromStart(ind,initialDate);
            }
            ind++;
        }

        System.out.println("}");

    }




    public void printBest(String city1, String city2) {
        final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());


        ArrayList<AllPathsReturn> allp =  dynamicGenerateBetweenCity(city1,city2);
        PrintStream console = System.out;

        for (int i = 0 ; i< allp.size();i++){
            try {
                String file = String.format("src\\data\\Output\\%s_%s_time_%.1f_hours",city1,city2,SatOrbitProbagation.duration/3600);
                new File(file).mkdir(); // creat folder
                PrintStream o = new PrintStream(new File(file+"\\Graph_"+i+".txt"));
                System.setOut(o);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            System.out.println("digraph G{");
            System.out.println("layouit=dot");
            System.out.println("graph [ dpi = 300 ];");
            System.out.println("rankdir=LR;");


            System.out.println(city1);
            AllPathsReturn cur  =  allp.get(i);
            System.out.printf("label = \"%d iteration: %f qubits \"",i,cur.getBest().qbitsGenerated());

            System.out.println(city2);
            cur.print(i);

            System.out.println("}");

        }
        System.setOut(console);



        for (int i = 0 ; i< allp.size();i++) {
            try {
                String file = String.format("src\\data\\Output\\%s_%s_time_%.1f_hours\\Data", city1, city2, SatOrbitProbagation.duration / 3600);
                new File(file).mkdir(); // creat folder
                PrintStream o = new PrintStream(new File(file + "\\Graph_" + i + ".txt"));
                System.setOut(o);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
                allp.get(i).printEdgeData();

        }

        System.setOut(console);


        for(int i =0;i<allp.size();i++){
            String tmp = String.format("%d iteration: %f qubits",i,allp.get(i).getBest().qbitsGenerated());
            System.out.println(tmp);
        }

    }


    public void loadFromFile(){
        try {
            //FileInputStream fis = new FileInputStream("/home/narcano/OrekitMultiSatellite/src/Data/dat.ser");
            FileInputStream fis = new FileInputStream("C:\\Users\\Narcano\\IdeaProjects\\OrekitMultiSatellite\\src\\Data\\dat.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            nodes = (HashMap<String,Node>) ois.readObject();
            ois.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<AllPathsReturn> dynamicGenerateBetweenCity(String city1, String city2){


        ArrayList<AllPathsReturn> out = new ArrayList<>();
        for(Edge e : nodes.get(city1).edges) {
            AllPathsReturn cur = dynamicAlgo(e, city2);
            if(cur!=null)
            out.add(cur);
        }



        return out;
    }


    public AllPathsReturn dynamicAlgo(Edge in,  String target){


        double Max=0;
        ArrayList<Path> otherPaths = new ArrayList<>();
        Path best = null;
        ArrayList<Path> nextRound = new ArrayList<>();
        nextRound.add(new Path(in));

        boolean running = true;
        while (running){
            running = false;
            ArrayList<Path> TnextRound = new ArrayList<>(nextRound);
            nextRound = new ArrayList<>();
            for(Path p : TnextRound){
                for (Edge outerEdge : p.getLastEdge().end.edges) {
                    if(outerEdge.getDataStart().isAfter(p.getLastEdge().getDataStart())){
                        Path curP = null;
                        try {
                            curP = p.generateNewWith(outerEdge);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (curP != null) {
                            double curBest = curP.computeOverallTransmittance();

                            if (curP.getLastEdge().end.name.equals(target)) { // its the target city
                                if (Max < curBest) {
                                    Max = curBest;
                                    if(best!=null) {
                                        otherPaths.add(best); //adding older best path to the other paths
                                    }
                                    best = curP;
                                }else {
                                    otherPaths.add(curP);
                                }
                            }else if (curP.getDur()<MAX_TIME){
                                nextRound.add(curP);
                                running = true;
                            }
                        }

                    }
                }
            }



        }


        if(best!=null) {
            return new AllPathsReturn(best, otherPaths);
        } else {
            return null;
        }
    }
}
