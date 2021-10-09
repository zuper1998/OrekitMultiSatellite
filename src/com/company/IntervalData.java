package com.company;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class IntervalData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public ArrayList<Double> Distance;
    public ArrayList<Double> Angle;
    public ArrayList<Double> Transmittance;

    public IntervalData(ArrayList<Double> dist) {
        Distance = dist;

    }

    public IntervalData(IntervalData id) {
        Distance = new ArrayList<>(id.Distance);
        if (id.Angle != null)
            Angle = new ArrayList<>(id.Angle);
        if (id.Transmittance != null)
            Transmittance = new ArrayList<>(id.Transmittance);
    }

    public IntervalData(ArrayList<Double> dist, ArrayList<Double> angle) {
        Distance = new ArrayList<>(dist);
        Angle = new ArrayList<>(angle);
    }


    public void popLastData() {
        Distance.remove(Distance.size() - 1);
        if (Angle != null)
            Angle.remove(Angle.size() - 1);
        if (Transmittance != null)
            Transmittance.remove(Transmittance.size() - 1);

    }

    public void popFirstData() {
        Distance.remove(0);
        if (Angle != null)
            Angle.remove(0);
        if (Transmittance != null)
            Transmittance.remove(0);
    }
}
