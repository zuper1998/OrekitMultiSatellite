package com.company;

import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SatTimeline {
    String name;
    Map<SatTimeline, ArrayList<AbsoluteDate>> timelineList = new HashMap<>();

    public SatTimeline(String n){
        name=n;
    }
    void AddElement(AbsoluteDate date, SatTimeline target){
        if(!timelineList.containsKey(target)){
            timelineList.put(target,new ArrayList<AbsoluteDate>());
        }
            timelineList.get(target).add(date);

    }
    @Override
    public boolean equals(Object o){
        if(o instanceof  SatTimeline){
            return ((SatTimeline)o).name.equals(name);
        }
        return false;
    }

    public AbsoluteDate getLast(SatTimeline st){
        return timelineList.get(st).get(timelineList.get(st).size()-1);
    }

    public void print(){
        final AbsoluteDate start = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());
        final AbsoluteDate end = start.shiftedBy(SatOrbitProbagation.duration);

        for(Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> satTime : timelineList.entrySet()) {
            System.out.println(name+" --> " + satTime.getKey().name);
            for (AbsoluteDate extrapDate = start;
                 extrapDate.compareTo(end) <= 0;
                 extrapDate = extrapDate.shiftedBy(SatOrbitProbagation.stepT)) {
                    if(satTime.getValue().contains(extrapDate)){
                        System.out.print("+");
                    } else {
                        System.out.print(" ");
                    }
            }
            System.out.println();
        }
    }

    public double getMaxWindow() {
        final AbsoluteDate start = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());
        final AbsoluteDate end = start.shiftedBy(SatOrbitProbagation.duration);
        double max=0;
        double cnt=0;
        for(Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> a : timelineList.entrySet()){
            boolean first = false;

            for (AbsoluteDate extrapDate = start;
                 extrapDate.compareTo(end) <= 0;
                 extrapDate = extrapDate.shiftedBy(SatOrbitProbagation.stepT)) {
                if (a.getValue().contains(extrapDate)) {
                    cnt += 1;
                    first = true;
                } else if (first) {
                    max = Math.max(cnt, max);
                    first = false;
                    cnt = 0;
                }
            }
            }
        return max;
    }
}
