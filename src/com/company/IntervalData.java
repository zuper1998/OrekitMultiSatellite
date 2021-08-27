package com.company;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class IntervalData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
        public ArrayList<Double> Distance;
        public ArrayList<Double> angle;
        public IntervalData(ArrayList<Double> dist){
            Distance=dist;

        }
        public IntervalData(ArrayList<Double> dist,ArrayList<Double> angle){
            Distance=dist;
            this.angle = angle;
        }


}
