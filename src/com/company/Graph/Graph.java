package com.company.Graph;

import Data.SimValues;
import com.company.Graph.DynamicHelper.AllPathsReturn;
import com.company.Graph.DynamicHelper.Path;
import com.company.SatFlightData;
import org.hipparchus.util.FastMath;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Data.SimValues.MAX_TIME;
import static Data.SimValues.SearchDepth;


public class Graph {

    public Map<String, Node> nodes = new HashMap<>();


    public void GenerateGraph(Map<String, ArrayList<SatFlightData>> timelineMap) {

        for (Map.Entry<String, ArrayList<SatFlightData>> tlm : timelineMap.entrySet()) {
            nodes.put(tlm.getKey(), new Node(tlm.getKey()));
        }
        // add edges
        for (Map.Entry<String, ArrayList<SatFlightData>> tlm : timelineMap.entrySet()) {
            for (SatFlightData sfd : tlm.getValue()) {
                for (int i = 0; i < sfd.Interval.size(); i++) {
                    //Error comes from the ground states not giving data
                    SatFlightData.SatFlightDataRetunVal satDat = sfd.getDataAt(i);
                    nodes.get(tlm.getKey()).addEdge(nodes.get(sfd.Dest), satDat.getTimeInterval().start, satDat.getTimeInterval().end, satDat.getIntervalData());
                }
            }
        }


        //Save data to dat.ser
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("src/Data/dat.ser");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(nodes);
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void printBest(String city1) {
        System.err.println(nodes.get(city1).edges.size());
        for (int i = 0; i < nodes.get(city1).edges.size(); i++) {
            System.err.printf("%f%n", (double) i / nodes.get(city1).edges.size());
            HashMap<String, AllPathsReturn> cur = dynamicGenerateBetweenCityIndexable(city1, i);

            for (Map.Entry<String, AllPathsReturn> a : cur.entrySet()) {
                String fileFolder = String.format("src/Data/Output/Time_%f_Sat_%s/%s_%s", SimValues.duration / 3600, new File(SimValues.satData).getName(), city1, a.getKey());
                try {
                    new File(fileFolder).mkdirs();
                    FileOutputStream fos = new FileOutputStream(fileFolder + "/out" + i + ".ser");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    a.getValue().StripDown();
                    oos.writeObject(a.getValue());
                    oos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }


    }


    public void loadFromFile() {
        try {
            //FileInputStream fis = new FileInputStream("/home/narcano/OrekitMultiSatellite/src/Data/dat.ser");
            FileInputStream fis = new FileInputStream("src/Data/dat.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            nodes = (HashMap<String, Node>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateAllTransmittance() {

        nodes.forEach((a, b) -> b.edges.forEach(Edge::genTransmittance));
    }

    public HashMap<String, AllPathsReturn> dynamicGenerateBetweenCityIndexable(String city1, int i) {

        Edge e = nodes.get(city1).edges.get(i);
        return dynamicAlgo(e);
    }


    public HashMap<String, AllPathsReturn> dynamicAlgo(Edge in) {
        ArrayList<Path> nextRound = new ArrayList<>();
        nextRound.add(new Path(in));
        HashMap<String, Double> bestValues = new HashMap<>();
        HashMap<String, AllPathsReturn> bestPaths = new HashMap<>();

        boolean running = true;
        int cnt = 0;

        while (running) { // If there are no backward Edges in the paths there cant be more levels then there are nodes
            if (++cnt > SearchDepth)
                break;
            running = false;
            ArrayList<Path> tNextRound = nextRound;
            nextRound = new ArrayList<>();
            Runtime.getRuntime().gc();

            for (Path p : tNextRound) {
                for (Edge outerEdge : p.getLastEdge().getEndNode().edges) {
                    if (outerEdge.getDataEnd().isAfter(p.getLastEdge().getDataStart())
                            && (FastMath.abs(outerEdge.getDataStart().durationFrom(p.getPath().get(0).getDataEnd())) < MAX_TIME) // if |---a |---| |---| |---| b--| and dist(a,b)>MAX_TIME -> there cant be a good route
                            && !p.containsNode(outerEdge.getEndNode())) {
                        Path curP = null;
                        try {
                            curP = p.generateNewWith(outerEdge);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (curP != null && !curP.isEmpty()) {
                            if (curP.getDur() > MAX_TIME) {
                                if (!curP.trimToWindowSize()) {
                                    continue;
                                }
                            }
                            if (curP.getLastEdge().getEndNode().isCity()) { // its the target city
                                double curBest = curP.computeBestTransmittance();
                                String lastNode = curP.getLastEdge().getEndNode().name;
                                bestValues.putIfAbsent(lastNode, 0.0);
                                if (bestValues.get(lastNode) < curBest) {
                                    bestValues.put(lastNode, curBest);
                                    bestPaths.putIfAbsent(lastNode, new AllPathsReturn());
                                    bestPaths.get(lastNode).addNewBest(curP);

                                }
                            } else {
                                nextRound.add(curP);
                                running = true;
                            }
                        }

                    }
                }
            }


        }


        return bestPaths;
    }


}
