package com.company.dataManagement;

import Data.SimValues;
import com.company.Graph.DynamicHelper.AllPathsReturn;
import com.company.Main;
import com.company.SatOrbitProbagation;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.io.*;
import java.util.ArrayList;

public class dataExporter {
    static final String sourceFileFolder = String.format("src/Data/Output/Time_%f_Sat_%s/", SimValues.duration / 3600, new File(SimValues.satData).getName());
    static final String destFileFolder = "src/Data/Output/";
    public static void exportALL(){

        ArrayList<AllPathsReturn> allp = dataLoader.loadAllPaths(sourceFileFolder);
        for(AllPathsReturn a : allp){
            MakeGraph(a);
            printData(a);
        }
        //printEBits(allp);


    }
    public static void MakeGraph(AllPathsReturn cur){
       try {
           AbsoluteDate initialDate = new AbsoluteDate(2021, 1, 1, 23, 30, 00.000, TimeScalesFactory.getUTC())
                   .shiftedBy(0);
           String f = destFileFolder+String.format("GRAPH/%s_%s_%f",cur.getBest().getPath().get(0).getStartNode().name,
                   cur.getBest().getLastEdge().getEndNode().name,
                   cur.getBest().getPath().get(0).getDataStart().durationFrom(initialDate));
           new File(destFileFolder+"GRAPH/").mkdirs(); // creat folder

            FileWriter o = new FileWriter(f);

           BufferedWriter writer = new BufferedWriter(o);


            writer.append("digraph G{");
            writer.newLine();
            writer.append("layout=dot");
            writer.newLine();
            writer.append("graph [ dpi = 100 ];");
            writer.newLine();
            writer.append("rankdir=LR;");
            writer.newLine();

            writer.append(cur.getBest().getPath().get(0).getStartNode().name);
            writer.newLine();
            writer.append(String.format("label = \" Total duration %.3f \"", cur.getBest().getDur())).append("\n");;
            writer.newLine();
            writer.append(cur.getBest().getLastEdge().getEndNode().name);
            writer.newLine();
            cur.Save(5,writer);

            writer.append("}");
            writer.close();

            o.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void printData(AllPathsReturn cur){
        AbsoluteDate initialDate = new AbsoluteDate(2021, 1, 1, 23, 30, 00.000, TimeScalesFactory.getUTC())
                .shiftedBy(0);
          try {
                    String f = destFileFolder+String.format("DATA/%s_%s_%f",cur.getBest().getPath().get(0).getStartNode().name,
                      cur.getBest().getLastEdge().getEndNode().name,
                      cur.getBest().getPath().get(0).getDataStart().durationFrom(initialDate));
                    new File(destFileFolder+"DATA/").mkdirs(); // creat folder

                    FileWriter o = new FileWriter(f);
                    BufferedWriter writer = new BufferedWriter(o);
                    cur.getBest().SaveData(writer);
                    writer.close();
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


    }


    //TODO: this will work funky since the arraylist conains all paths between all cities
    public static void printEBits(ArrayList<AllPathsReturn> allp){

        double eBits=0;
        for(AllPathsReturn a : allp){

            eBits+=  a.getBest().computeOverallTransmittance()*SimValues.entanglementGenHz*SimValues.frequencyBinEntanglementFidelity;
        }
        FileWriter o = null;
        try {
            o = new FileWriter((destFileFolder + "/ebitStats" +".txt"));
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

    }
}
