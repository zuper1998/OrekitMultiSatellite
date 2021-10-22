package com.company.dataManagement;

import Data.SimValues;
import com.company.City;
import com.company.Graph.DynamicHelper.AllPathsReturn;
import com.company.Graph.DynamicHelper.Path;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.io.*;
import java.util.*;

import static Data.SimValues.initialDate;

public class dataExporter {
    static final String sourceFileFolder = String.format("src/Data/Output/Time_%f_Sat_%s/", SimValues.duration / 3600, new File(SimValues.satData).getName());
    static final String destFileFolder = String.format("src/Data/Output/%s/", new File(SimValues.satData).getName());

    public static void exportALL() {
        ArrayList<AllPathsReturn> allp = dataLoader.loadAllPaths(sourceFileFolder);
        for (AllPathsReturn a : allp) {
            makeGraph(a);
            printData(a);
        }
        for (City s1 : SimValues.cities) {
            for (City s2 : SimValues.cities) {
                String c1 = s1.getName();
                String c2 = s2.getName();
                if (!c1.equals(c2)) {
                    makeCityGraph(allp, c1, c2);
                }
            }
        }


        HashMap<String, Double> genEbitsPerSec = new HashMap<>();
        for (City s1 : SimValues.cities) {
            for (City s2 : SimValues.cities) {
                String c1 = s1.getName();
                String c2 = s2.getName();
                double curEbit = 0;
                if (!c1.equals(c2)) {

                    curEbit = printEBits(allp, c1, c2);
                    genEbitsPerSec.put(c1 + "_" + c2, curEbit);
                }
            }
        }
        printToCsV(genEbitsPerSec);
        System.out.print("- & ");
        for (City s1 : SimValues.cities) {
            System.out.printf(" %s &",s1.getName());
        }
        System.out.println(" \\\\");
        for (City s1 : SimValues.cities) {
            System.out.printf(" %s &",s1.getName());
            for (City s2 : SimValues.cities) {
                String c1 = s1.getName();
                String c2 = s2.getName();
                if (!c1.equals(c2)) {
                    System.out.printf(" %.5f &",genEbitsPerSec.get(c1+"_"+c2));
                } else {
                    System.out.print(" - &");
                }
            }
            System.out.println(" \\\\");
        }


    }

    public static void printToCsV(HashMap<String, Double> hm) {
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter(destFileFolder + "table.csv")) {
            writer.append("S").append(',').append("T").append(',').append("W").append(eol);
            for (Map.Entry<String, Double> entry : hm.entrySet()) {
                StringTokenizer stk = new StringTokenizer(entry.getKey(), "_");
                writer.append(stk.nextToken())
                        .append(',')
                        .append(stk.nextToken())
                        .append(',')
                        .append(entry.getValue().toString())
                        .append(eol);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

    }

    public static void makeCityGraph(ArrayList<AllPathsReturn> allp, String c1, String c2) {
        try {
            String f = destFileFolder + String.format("GRAPH/%s_%s_COMPLETE", c1,
                    c2);
            new File(destFileFolder + "GRAPH/").mkdirs(); // creat folder

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
            writer.append(String.format("label = \" %s->%s úvonal legjobb útvonalai \"", c1, c2));
            writer.append(c1);
            writer.newLine();
            writer.append(c2);
            writer.newLine();
            int i = 0;
            for (AllPathsReturn cur : allp) {
                if (cur.getBest().getFirstEdge().getStartNode().name.equals(c1) && cur.getBest().getLastEdge().getEndNode().name.equals(c2))
                    cur.Save(i++, writer);
            }
            writer.append("}");

            writer.close();

            o.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeGraph(AllPathsReturn cur) {
        try {
            AbsoluteDate initialDate = new AbsoluteDate(2021, 1, 1, 23, 30, 00.000, TimeScalesFactory.getUTC())
                    .shiftedBy(0);
            String f = destFileFolder + String.format("GRAPH/%s_%s_%f", cur.getBest().getPath().get(0).getStartNode().name,
                    cur.getBest().getLastEdge().getEndNode().name,
                    cur.getBest().getPath().get(0).getDataStart().durationFrom(initialDate));
            new File(destFileFolder + "GRAPH/").mkdirs(); // creat folder

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
            writer.append(String.format("label = \" Teljes időtartam: %.3f [másodperc] \"", cur.getBest().getDur())).append("\n");

            writer.newLine();
            writer.append(cur.getBest().getLastEdge().getEndNode().name);
            writer.newLine();
            cur.Save(5, writer);

            writer.append("}");
            writer.close();

            o.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printData(AllPathsReturn cur) {
        AbsoluteDate initialDate = new AbsoluteDate(2021, 1, 1, 23, 30, 00.000, TimeScalesFactory.getUTC())
                .shiftedBy(0);
        try {
            String f = destFileFolder + String.format("DATA/%s_%s_%f", cur.getBest().getFirstEdge().getStartNode().name,
                    cur.getBest().getLastEdge().getEndNode().name,
                    cur.getBest().getFirstEdge().getDataStart().durationFrom(initialDate));
            new File(destFileFolder + "DATA/").mkdirs(); // creat folder

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
    public static double printEBits(ArrayList<AllPathsReturn> allp, String c1, String c2) {
        ArrayList<Path> curPaths = new ArrayList<>();
        for (AllPathsReturn a : allp) {
            Path p = a.getBest();
            if (p.getPath().get(0).getStartNode().name.equals(c1)
                    && p.getLastEdge().getEndNode().name.equals(c2)) {
                curPaths.add(p);
            }
        }
        curPaths.sort(Comparator.comparing(a -> a.getFirstEdge().getDataStart()));

        double[] eBits = new double[(int) SimValues.duration];
        for (int i = 0; i < (int) SimValues.MAX_TIME; i++) {
            eBits[i] = 0;
        }
        for (Path p : curPaths) {
            ArrayList<Double> Transmittance = p.getEbits();
            int shitf = (int) p.getLastEdge().getDataStart().durationFrom(initialDate);
            for (int i = 0; i < Transmittance.size(); i++) {
                eBits[i + shitf] += Transmittance.get(i) * SimValues.entangledPhotonDetectionRateHz;
            }
        }


        String f = destFileFolder + String.format("EBITDATA/%s_%s", c1, c2);
        new File(destFileFolder + "EBITDATA/").mkdirs(); // creat folder

        FileWriter o = null;
        try {
            o = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(o);

            for (double d : eBits) {
                writer.append(String.valueOf(d));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        double sumMax = 0;
        for (int i = 0; i < SimValues.duration; i++) {
            sumMax += eBits[i];
        }

        return sumMax / SimValues.duration;


    }
}
