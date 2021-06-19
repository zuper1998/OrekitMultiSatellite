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
                if(RecursiveDFSSearch(edge.end,e,e_out,edge.getDataStart(),visited,TIME_LEFT-time_spent)){
                    e_out.add(edge);
                    ret = true;
                }
            }

        }
        return ret;
    }

}
