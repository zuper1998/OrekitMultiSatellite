package com.company.Graph;

import com.company.SatTimeline;
import com.company.TimeInterval;
import com.company.Utility;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.company.SatOrbitProbagation.MAX_TIME;

public class Graph {

    public Map<String,Node> nodes = new HashMap<>();


    public void GenerateGraph(Map<String, SatTimeline> timelineMap, String city1, String city2) {
        double MAX_WINDOW_SIZE = 0;
        final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());

        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
                double tmp_max = Utility.GetMaxWindow(a.getValue());
                if (tmp_max > MAX_WINDOW_SIZE)
                    MAX_WINDOW_SIZE = tmp_max;
            }
        }
        // Import Nodes
        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
           nodes.put(a.getKey(),new Node(a.getKey()));
        }


        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
                //a.getValue().recursiveStuff(initialDate, MAX_TIME,MAX_WINDOW_SIZE);
                for(Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> timeline : a.getValue().timelineList.entrySet()) {
                    ArrayList<TimeInterval> intervals = Utility.getTimeIntervals(timeline.getValue());
                    ArrayList<SatTimeline> used = new ArrayList<>();
                    used.add(a.getValue());
                    for(TimeInterval t : intervals){
                        System.out.println(a.getKey() + "->" + timeline.getKey().name + "  duration: " + t.end.durationFrom(t.start) + "] time remaining:" + MAX_TIME);
                        nodes.get(a.getKey()).addEdge(nodes.get(timeline.getKey().name),t.start,t.end);
                        timeline.getKey().recursiveGraphBuilding(t.end.shiftedBy(100), MAX_TIME,t.end.durationFrom(t.start),this,used);
                        break;//SO it only runs the first intervak (it is geting fucking slow xd)
                    }
                }
            }

        }
        System.out.println("START PRINTING");

        for(Map.Entry<String, Node> a :  nodes.entrySet()){
            a.getValue().printDotEdges();
        }
    }
}
