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
                //for(TimeInterval t : sfd.Interval) {
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
            //fos = new FileOutputStream("C:\\Users\\Narcano\\IdeaProjects\\OrekitMultiSatellite\\src\\Data\\dat.ser");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(nodes);
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private  int compareTime(AbsoluteDate a,AbsoluteDate b){
        if(a.isBefore(b))
            return -1;
        if(b.isBefore(a))
            return 1;

        return 0;
    }



    public void printBest(String city1, String city2) {


        //ArrayList<AllPathsReturn> allp =  dynamicGenerateBetweenCity(city1,city2);

            ArrayList<AllPathsReturn> allp = new ArrayList<>();
        String fileFolder = String.format("src/Data/Output/%s_%s_time_%.1f_hours_%s", city1, city2, SimValues.duration / 3600, new File(SimValues.satData).getName());

        //nodes.get(city1).edges.sort((o1,o2) -> compareTime(o1.getDataStart(),o2.getDataStart()) );
        for (int i = 0; i < nodes.get(city1).edges.size(); i++) {
            AllPathsReturn cur = dynamicGenerateBetweenCityIndexable(city1, city2, i);
            if (cur != null) {

                try {
                    new File(fileFolder).mkdir(); // creat folder
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    new File(fileFolder).mkdir(); // creat folder
                    FileWriter o = new FileWriter((fileFolder + "/Data_" + cur.getBest().getPath().get(0).getEdgeWay() + "_" + i + ".txt"));
                    BufferedWriter writer = new BufferedWriter(o);
                    cur.getBest().SaveData(writer);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }






       /*for(int i =0;i<allp.size();i++){
            String tmp = String.format("%d iteration: %f qubits",i,allp.get(i).getBest().qbitsGenerated());
            System.out.println(tmp);
        }*/


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
            //System.err.println(cnt + "   Max:" + Max + "   Paths's in use:" + nextRound.size());
            if (++cnt > SearchDepth)
                break;
            running = false;
            ArrayList<Path> TnextRound = new ArrayList<>(nextRound);
            nextRound = new ArrayList<>();
            for (Path p : TnextRound) {
                for (Edge outerEdge : p.getLastEdge().end.edges) {
                    if (outerEdge.getDataEnd().isAfter(p.getLastEdge().getDataStart()) && !p.containsNode(outerEdge.getEndNode())) {
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
                                    //addIfBestKind(curP,otherPaths);
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
