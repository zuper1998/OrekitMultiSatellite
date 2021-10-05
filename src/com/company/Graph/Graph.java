package com.company.Graph;

import Data.SimValues;
import com.company.Graph.DynamicHelper.AllPathsReturn;
import com.company.Graph.DynamicHelper.Path;
import com.company.SatFlightData;
import com.company.SatOrbitProbagation;
import org.orekit.time.AbsoluteDate;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
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




    public void printBest(String city1, String city2) {



            ArrayList<AllPathsReturn> allp = new ArrayList<>();
        String fileFolder = String.format("src/Data/Output/Time_%f_Sat_%s/%s_%s", SimValues.duration / 3600, new File(SimValues.satData).getName(), city1, city2);

        for (int i = 0; i < nodes.get(city1).edges.size(); i++) {
            AllPathsReturn cur = dynamicGenerateBetweenCityIndexable(city1, city2, i);
            if (cur != null) {

                try {
                    new File(fileFolder).mkdirs(); // creat folder
                    FileWriter o = new FileWriter(fileFolder + "/Graph_" + cur.getBest().getPath().get(0).getEdgeWay()+ "_"+ cur.getBest().getPath().get(0).getDataStart().durationFrom(SatOrbitProbagation.initialDate.getDate()) + "_" + i + ".txt");
                    BufferedWriter writer = new BufferedWriter(o);


                    allp.add(cur);

                    writer.append("digraph G{");
                    writer.newLine();
                    writer.append("layout=dot");
                    writer.newLine();
                    writer.append("graph [ dpi = 100 ];");
                    writer.newLine();
                    writer.append("rankdir=LR;");
                    writer.newLine();

                    writer.append(city1);
                    writer.newLine();
                    writer.append(String.format("label = \"%d iteration: total duration %.3f \"", i, cur.getBest().getDur())).append("\n");;
                    writer.newLine();
                    writer.append(city2);
                    writer.newLine();
                    cur.Save(i,writer);

                    writer.append("}");
                    writer.close();

                     o.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    new File(fileFolder).mkdirs(); // creat folder
                    FileWriter o = new FileWriter((fileFolder + "/Data_" + cur.getBest().getPath().get(0).getEdgeWay() + "_" + i + ".txt"));
                    BufferedWriter writer = new BufferedWriter(o);
                    cur.getBest().SaveData(writer);
                    writer.close();
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        double eBits=0;
        for(AllPathsReturn a : allp){

            eBits+=  a.getBest().computeOverallTransmittance()*SimValues.entanglementGenHz*SimValues.frequencyBinEntanglementFidelity;
        }
        FileWriter o = null;
        try {
            o = new FileWriter((fileFolder + "/ebitStats" +".txt"));
            BufferedWriter writer = new BufferedWriter(o);

            writer.append("eBits: ").append(String.valueOf(eBits));
            writer.newLine();
            writer.append("Total time elapsed: "+SimValues.duration+" [s]");
            writer.newLine();
            writer.append("eBits/s: ").append(String.valueOf(eBits / SimValues.duration));
            writer.close();
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fos = new FileOutputStream(fileFolder+"/out.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allp);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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


    public AllPathsReturn dynamicGenerateBetweenCityIndexable(String city1, String city2, int i) {

        Edge e = nodes.get(city1).edges.get(i);
        return dynamicAlgo(e, city2);
    }


    public AllPathsReturn dynamicAlgo(Edge in, String target) {
        double Max = 0;
        ArrayList<Path> otherPaths = new ArrayList<>();
        Path best = null;
        ArrayList<Path> nextRound = new ArrayList<>();
        nextRound.add(new Path(new Edge(in)));

        boolean running = true;
        int cnt = 0;

        while (running) { // If there are no backward Edges in the paths there cant be more levels then there are nodes
            if (++cnt > SearchDepth)
                break;
            running = false;
            ArrayList<Path> TnextRound = new ArrayList<>(nextRound);
            nextRound = new ArrayList<>();
            for (Path p : TnextRound) {
                for (Edge outerEdge : p.getLastEdge().end.edges) {
                    if (outerEdge.getDataEnd().isAfter(p.getLastEdge().getDataStart())
                            && !p.containsNode(outerEdge.getEndNode())
                            && outerEdge.getDataStart().durationFrom(p.getLastEdge().getDataEnd())< MAX_TIME) {
                        Path curP = null;
                        try {
                            curP = p.generateNewWith(new Edge(outerEdge));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (curP != null && !curP.isEmpty()) {
                            if (curP.getDur() > MAX_TIME) {
                                if (!curP.trimToWindowSize()) {
                                    continue;
                                }

                            }
                            if (curP.getLastEdge().end.name.equals(target)) { // its the target city
                                double curBest = curP.computeBestTransmittance();
                                if (Max < curBest) {
                                    Max = curBest;
                                    if (best != null) {
                                        otherPaths.add(best); //adding older best path to the other paths
                                    }
                                    best = curP;
                                } else {
                                    otherPaths.add(curP);
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


        if (best != null) {
            return new AllPathsReturn(best, otherPaths);
        } else {
            return null;
        }
    }

}
