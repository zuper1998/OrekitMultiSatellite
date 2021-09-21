package com.company;

import Data.SimValues;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Data.SimValues.stepT;

public class SatTimeline {
    public String name;
    public Map<SatTimeline, ArrayList<AbsoluteDate>> timelineList = new HashMap<>();

    public SatTimeline(String n) {
        name = n;
    }

    void AddElement(AbsoluteDate date, SatTimeline target) {
        if (!timelineList.containsKey(target)) {
            timelineList.put(target, new ArrayList<AbsoluteDate>());
        }
        timelineList.get(target).add(date);

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SatTimeline) {
            return ((SatTimeline) o).name.equals(name);
        }
        return false;
    }


    public void recursiveStuff(AbsoluteDate startDate, double MAX_TIME, double MAX_WINDOW) {
        if (MAX_TIME < 0) return;
        for (Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> timeline : timelineList.entrySet()) {
            ArrayList<TimeInterval> intervals = Utility.getTimeIntervalsSetTimeWithMAXWindow(timeline.getValue(), startDate, MAX_WINDOW);
            for (TimeInterval t : intervals) {
                if (t.end.durationFrom(t.start) > 100) {
                    System.out.println(name + "->" + timeline.getKey().name + " [label=" + t.end.durationFrom(t.start) + "]"); //  duration: " + t.end.durationFrom(t.start) + " START:" + t.start );
                    timeline.getKey().recursiveStuff(t.end.shiftedBy(10), MAX_TIME - t.end.durationFrom(startDate), t.end.durationFrom(t.start));
                }
            }
        }
    }

    /*
        public void recursiveGraphBuilding(AbsoluteDate startDate, double MAX_TIME, double MAX_WINDOW, Graph g, ArrayList<SatTimeline> used){
            if(MAX_TIME<100) return;
            for(Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> timeline : timelineList.entrySet()) {
                ArrayList<TimeInterval> intervals = Utility.getTimeIntervalsSetTimeWithMAXWindow(timeline.getValue(),startDate,MAX_WINDOW);
                if(!used.contains(this)){
                    used.add(this);
                }
                for(TimeInterval t : intervals){
                    if(t.end.durationFrom(t.start)>100 && !used.contains(timeline.getKey())) {
                        //System.out.println(name + "->" + timeline.getKey().name + "  duration: " + t.end.durationFrom(t.start)+ " start "+ t.start+ "end "+ t.end + " time remaining:" + MAX_TIME); //  duration: " + t.end.durationFrom(t.start) + " START:" + t.start );
                        g.nodes.get(name).addEdge(g.nodes.get(timeline.getKey().name),t.start,t.end);
                        timeline.getKey().recursiveGraphBuilding(t.end.shiftedBy(100), MAX_TIME - t.end.durationFrom(startDate), t.end.durationFrom(t.start), g, used);

                    }
                }
            }
        }
    */
    public double getMaxWindow() {
        final AbsoluteDate start = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());
        final AbsoluteDate end = start.shiftedBy(SimValues.duration);
        double max = 0;
        double cnt = 0;
        for (Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> a : timelineList.entrySet()) {
            boolean first = false;

            for (AbsoluteDate extrapDate = start;
                 extrapDate.compareTo(end) <= 0;
                 extrapDate = extrapDate.shiftedBy(stepT)) {
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
