package com.company.Graph;

import com.company.*;
import com.company.Graph.DynamicHelper.Path;
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
               for(TimeInterval t : sfd.Interval) {
                   nodes.get(tlm.getKey()).addEdge(nodes.get(sfd.Dest),t.start,t.end);
               }
           }
        }



        //Save data to dat.ser
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("/home/narcano/OrekitMultiSatellite/src/Data/dat.ser");
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


    public void printG(String city1, String city2){
        final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());

        System.out.println("digraph G{");
        System.out.println("layouit=dot");
        System.out.println("graph [ dpi = 300 ];");
        System.out.println("rankdir=LR;");
        System.out.println(city1);

        for(Map.Entry<String, Node> n : nodes.entrySet()){
            if(!n.getKey().equals(city1) ||!n.getKey().equals(city2) ){
                System.out.println(n.getKey());
            }
        }
        System.out.println(city2);

        for(Edge edge : nodes.get("Budapest").edges) {
            int index = nodes.get("Budapest").edges.indexOf(edge);
            ArrayList<Edge> e =  GraphUtility.findPathTo(this,edge.end,nodes.get("Berlin"),edge.getDataStart());
            if(!e.isEmpty()) {
                e.add(edge);
            }
            //find best route for each iter:
            //we need another graph for this -- by graph i mean nodes
            Map<String,Node> local_nodes = new HashMap<>();

            for(Map.Entry<String, Node> n : nodes.entrySet()){
                local_nodes.put(n.getKey(),new Node(n.getKey()));
            }
            for(Edge loc_edge : e) {
                local_nodes.get(loc_edge.start.name).addEdge(local_nodes.get(loc_edge.end.name),edge.getDataStart(),loc_edge.getDataEnd());
            }



            ArrayList<Edge> best = GraphUtility.findBestPath(local_nodes.get(city1), local_nodes.get(city2));


            best.forEach(aaaa -> aaaa.printColorLabelDurationFromStart(index,initialDate));
        }

        System.out.println("}");

    }

    public void printBest(String city1, String city2) {
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
        dynamicGenerateBetweenCity(city1,city2).forEach(Edge::print);

        System.out.println("}");

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


    public ArrayList<Edge> dynamicGenerateBetweenCity(String city1, String city2){


        ArrayList<Edge> allOtherEdge= new ArrayList<>();
        for(Node n : nodes.values()){
            if(!n.name.equals(city1)||!n.name.equals(city2)){
                allOtherEdge.addAll(n.edges);
            }
        }

        ArrayList<Edge> out = dynamicAlgo(nodes.get(city1).edges, allOtherEdge,city2);




        return out;
    }


    public ArrayList<Edge> dynamicAlgo(ArrayList<Edge> in, ArrayList<Edge> AllOther, String target){

        Path[] paths = new Path[in.size()];
        for(int i = 0 ; i < in.size();i++){
            paths[i] = new Path(in.get(i));
        }
        double Max=0;
        ArrayList<Edge> out = new ArrayList<>();
        ArrayList<Path> nextRound = new ArrayList<>();
        for(int i = 0 ; i < in.size();i++){
            for (Edge outerEdge : paths[i].getLastEdge().end.edges) {
                if (outerEdge.getDataStart().isAfter(paths[i].getLastEdge().getDataStart())) {
                    Path curP = null;
                    try {
                        curP = paths[i].generateNewWith(outerEdge);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (curP != null) {
                        double curBest = curP.computeBest();

                        if (curP.getLastEdge().end.name.equals(target)) { // its the target city
                            if (Max < curBest) {
                                Max = curBest;
                                out = curP.getPath();
                            }
                        }else if (curP.getDur()<MAX_TIME){
                            nextRound.add(curP);
                        }
                    }
                }
            }
        }


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
                            double curBest = curP.computeBest();

                            if (curP.getLastEdge().end.name.equals(target)) { // its the target city
                                if (Max < curBest) {
                                    Max = curBest;
                                    out = curP.getPath();
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






        return out;
    }


    public void GenerateGraphRecursive(Map<String, SatTimeline> timelineMap, String city1, String city2) {
        double MAX_WINDOW_SIZE = 0;
        final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());

        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
                double tmp_max = Utility.GetMaxWindow(a.getValue());
                if (tmp_max > MAX_WINDOW_SIZE)
                    MAX_WINDOW_SIZE = tmp_max;
            }
        }
        // Import Nodes
        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
           nodes.put(a.getKey(),new Node(a.getKey()));
        }


        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
                //a.getValue().recursiveStuff(initialDate, MAX_TIME,MAX_WINDOW_SIZE);
                for(Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> timeline : a.getValue().timelineList.entrySet()) {
                    ArrayList<TimeInterval> intervals = Utility.getTimeIntervals(timeline.getValue());

                    for(TimeInterval t : intervals){
                        ArrayList<SatTimeline> used = new ArrayList<>();
                        used.add(a.getValue());
                        //System.out.println(a.getKey() + "->" + timeline.getKey().name + "  duration: " + t.end.durationFrom(t.start) + " time remaining:" + MAX_TIME);
                        nodes.get(a.getKey()).addEdge(nodes.get(timeline.getKey().name),t.start,t.end);
                        timeline.getKey().recursiveGraphBuilding(t.end.shiftedBy(100), MAX_TIME,t.end.durationFrom(t.start),this,used);
                        //break;//SO it only runs the first intervak (it is geting fucking slow xd)
                        //used.forEach(us -> System.out.print(us.name+ "  "));
                        //System.out.println();
                    }

                }
            }

        }


        System.out.println("digraph G{");
        System.out.println("graph [ dpi = 300 ];");
        System.out.println("rankdir=LR;");



        for(Edge edge : nodes.get("Budapest").edges) {
            int index = nodes.get("Budapest").edges.indexOf(edge);
            ArrayList<Edge> e =  GraphUtility.findPathTo(this,edge.end,nodes.get("Berlin"),edge.getDataEnd());
            if(!e.isEmpty()) {
                e.add(edge);
            }
            e.forEach(aaaa -> aaaa.printColorLabelDurationFromStart(index,initialDate));
        }

        /*ArrayList<Edge> e =  GraphUtility.findPathTo(this,nodes.get("Budapest"),nodes.get("Berlin"),initialDate);
        for(Edge edge : e){
            edge.printNoLabel();
        }*/

        /*for(Map.Entry<String, Node> a :  nodes.entrySet()){
            a.getValue().printDotEdges();
        }*/

        System.out.println("}");
    }
}
