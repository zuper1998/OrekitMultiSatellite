package com.company.Graph;

import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;



public class GraphUtility {
    public static ArrayList<Edge> findPathTo(Graph g, Node s , Node e,AbsoluteDate minDate){
        //Ima use DFS bc i am lazy to implement anything more spicy
        ArrayList<Edge> EdgeOut =new ArrayList<Edge>();

        RecursiveDFSSearch(s, e, EdgeOut, minDate, new ArrayList<Node>(), 3600);

        return EdgeOut;
    }

    private static boolean RecursiveDFSSearch(Node s, Node e, ArrayList<Edge> e_out, AbsoluteDate minDate, ArrayList<Node> visited,double TIME_LEFT ) {
        if(s.equals(e)){
            return true;
        }
        if(TIME_LEFT<0){
            return false;
        }
        if(!visited.contains(s))
            visited.add(s);

        for(Edge edge : s.edges){
            if(edge.getDataStart().isAfter(minDate) && !visited.contains(edge.end)) {
                //System.out.println(s.name+"   "+ edge.end.name);

                if( RecursiveDFSSearch(edge.end,e,e_out,edge.getDataEnd(),visited,TIME_LEFT-edge.getDataDuration())){
                    e_out.add(edge);
                    return true;
                }
            }
        }
        return false;
    }
}
