package com.company.Graph;

import com.company.SatOrbitProbagation;
import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;



public class GraphUtility {

    public static double MIN_WINDOW = 100; // seconds
    public static ArrayList<Edge> findPathTo(Graph g, Node s , Node e,AbsoluteDate minDate){
        //Ima use DFS bc i am lazy to implement anything more spicy
        ArrayList<Edge> EdgeOut =new ArrayList<Edge>();

        RecursiveDFSSearch(s, e, EdgeOut, minDate, new ArrayList<Edge>(), SatOrbitProbagation.MAX_TIME);

        return EdgeOut;
    }

    private static boolean RecursiveDFSSearch(Node s, Node e, ArrayList<Edge> e_out, AbsoluteDate minDate, ArrayList<Edge> visited,double TIME_LEFT ) {
        if(TIME_LEFT<0){
            return false;
        }

        if(s.equals(e)){
            return true;
        }


        boolean ret =false;
        for(Edge edge : s.edges){
            if(!visited.contains(edge) && edge.getDataStart().durationFrom(minDate)>MIN_WINDOW){
                double time_spent = Math.min(edge.getDataStart().durationFrom(minDate),edge.getDataDuration());
                if(!visited.contains(edge)){
                    visited.add(edge);
                }
                if(RecursiveDFSSearch(edge.end,e,e_out,edge.getDataStart(),new ArrayList<>(visited),TIME_LEFT-time_spent)){
                    e_out.add(edge);
                    ret = true;
                }


            }

        }
        return ret;
    }



    public static ArrayList<Edge> findBestPath(Node s, Node e) {
        ArrayList<Edge> out = new ArrayList<>();

        MutableDouble GlobalMin = new MutableDouble(0.0); // Bigger the better
        for(Edge firstEdge : s.edges) {
            ArrayList<Edge> visited = new ArrayList<>();
            visited.add(firstEdge);
            RecursiveBestSearchWithSpooks(firstEdge.end, e, firstEdge.getDataEnd(), out, visited, firstEdge.getDataDuration(), GlobalMin);
        }
        return out;
    }

    private static boolean RecursiveBestSearchWithSpooks(Node s, Node e, AbsoluteDate prev_end,ArrayList<Edge> e_out, ArrayList<Edge> visited, double durMin, MutableDouble glob) {


        if(s.equals(e)){
            if(durMin>glob.getValue()){
                e_out.clear();
                e_out.addAll(visited);// DOnt use visited it will fuck up the graph with same start duration date
                glob.setValue(durMin);
            }
            return true;
        }


        boolean ret =false;
        for(Edge edge : s.edges){
            if(!visited.contains(edge)  && edge.getDataEnd().durationFrom(prev_end)>MIN_WINDOW){

                if(!visited.contains(edge)){
                    visited.add(edge);
                }
                durMin=   Math.min( durMin, Math.min(edge.getDataDuration(),edge.getDataEnd().durationFrom(prev_end)));
                if(RecursiveBestSearchWithSpooks(edge.end,e, edge.getDataEnd() ,e_out, new ArrayList<>(visited),durMin,glob)){

                    ret = true;
                }


            }

        }
        return ret;


    }
}
