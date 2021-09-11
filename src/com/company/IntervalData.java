package com.company;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class IntervalData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
        public ArrayList<Double> Distance;
        public ArrayList<Double> Angle;
        public IntervalData(ArrayList<Double> dist){
            Distance=dist;

        }

        public IntervalData(ArrayList<Double> dist,ArrayList<Double> angle){
            Distance=dist;
            this.Angle = angle;
        }


    public void popLastData() {
            Distance.remove(Distance.size()-1);
            if(Angle!=null)
            Angle.remove(Angle.size()-1);
    }
    public void popFirstData(){
        Distance.remove(0);
        if(Angle!=null)
        Angle.remove(0);
    }
}
