package com.company;


import com.company.Graph.Node;

import java.util.ArrayList;

public class SatFlightData {
    public String Dest;
    public ArrayList<TimeInterval> Interval = new ArrayList<>();
    ArrayList<IntervalData> data = new ArrayList<>();

    SatFlightData(String Dest){
        this.Dest = Dest;
    }


    @Override
    public boolean equals(Object o){
        if(o instanceof SatFlightData){
            return ((SatFlightData)o).Dest.equals(Dest);
        }
        return false;
    }

    public void addInterval(TimeInterval t){
        Interval.add(t);
    }

    public SatFlightDataRetunVal getDataAt(int index){
        return new SatFlightDataRetunVal(Interval.get(index), data.get(index));
    }

    static class IntervalData{
        ArrayList<Double> stuff; // wont be used just made to be usable for future expansions of the prject
    }

    public static class SatFlightDataRetunVal{
        TimeInterval t;
        IntervalData iD;
        SatFlightDataRetunVal(TimeInterval t,IntervalData iD){
            this.t=t;
            this.iD=iD;
        }
    }
}
