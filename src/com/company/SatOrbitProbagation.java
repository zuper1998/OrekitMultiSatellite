package com.company;

import org.hipparchus.analysis.function.Abs;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.frames.Transform;
import org.orekit.orbits.EquinoctialOrbit;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SatOrbitProbagation {
    // configure Orekit
    public static final double stepT = 1;
    public static double duration = 3600*12;
    public static double MAX_TIME = 3600;


    public static void loadStuff(){
        final File home = new File(System.getProperty("user.home"));
        final File orekitData = new File(home, "orekit-data");
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "Failed to find %s folder%n",
                    orekitData.getAbsolutePath());
            System.err.format(Locale.US, "You need to download %s from %s, unzip it in %s and rename it 'orekit-data' for this tutorial to work%n",
                    "orekit-data-master.zip", "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
                    home.getAbsolutePath());
            System.exit(1);
        }
        final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new DirectoryCrawler(orekitData));

    }
    public static Map<String,ArrayList<SatFlightData>> Generate(){

    //  Initial state definition : date, orbit
    final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC())
            .shiftedBy(0    );
    final double mu = 3.986004415e+14; // gravitation coefficient
    final Frame inertialFrame = FramesFactory.getEME2000(); // inertial frame for orbit definition
    ArrayList<Satellite_Sajat> sats = Satellite_Sajat.SatLoader(
            "src/Data/StarlinkOrb2.txt");

    Map<String, Propagator> orbits = new HashMap<>();
    for (Satellite_Sajat s1 : sats) {
        final Orbit initialOrbitE = new KeplerianOrbit(s1.a, s1.e, s1.i, s1.omega, s1.raan, s1.lM, PositionAngle.MEAN,
               inertialFrame, initialDate, mu);
        final Orbit initialOrbit = new EquinoctialOrbit(initialOrbitE);

        // Propagator : consider a simple Keplerian motion (could be more elaborate)
        orbits.put(s1.Name, new KeplerianPropagator(initialOrbit));
    }
    ArrayList<City> cities = new ArrayList<>();
    cities.add(new City());
    cities.add(new City(52.520008, 13.404954, 43, "Berlin"));
    cities.add(new City(-74,40.69,43,"NewYork"));
    final Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);

        var ref = new Object() {
            String GlobalKey = "";
        };
        Map<String,ArrayList< SatFlightData>> timelines = new HashMap<>();
        HashMap<String, AbsoluteDate> timelineHelperMap = new HashMap<>(); // absolut date is also a bool --> false if null
        HashMap<String, ArrayList<Double>> dataMap = new HashMap<>();
    //prepare timelines
    for(Map.Entry<String, Propagator> p : orbits.entrySet()){
        timelines.put(p.getKey(),new ArrayList<>());
    }
    for(City c : cities){
        timelines.put(c.name,new ArrayList<>());
    }

    var ref1 = new Object() {
        AbsoluteDate globDate = initialDate;
    };


    ArrayList<TopocentricFrame> cityFrames = new ArrayList<>();


    final BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
            Constants.WGS84_EARTH_FLATTENING,
            earthFrame);

        for (City c: cities) {
            final GeodeticPoint station1 = new GeodeticPoint(c.latitude, c.longitude, c.altitude);
            final TopocentricFrame sta1Frame = new TopocentricFrame(earth, station1, c.name);
            cityFrames.add(sta1Frame);


            /*
            final GeodeticPoint station1 = new GeodeticPoint(c.latitude, c.longitude, c.altitude);
            final TopocentricFrame sta1Frame = new TopocentricFrame(earth, station1, c.name);
            // Event definition
            final double maxcheck = 60.0;
            final double threshold = 0.001;
            final double elevation = FastMath.toRadians(20.0);
            EventDetector a = new ElevationDetector(maxcheck, threshold, sta1Frame).
                    withConstantElevation(elevation).
                    withHandler((s, detector, increasing) -> {
                    String name = String.format("%s->%s",detector.getTopocentricFrame().getName(),ref.GlobalKey);
                    String name_backwards = String.format("%s->%s",ref.GlobalKey,detector.getTopocentricFrame().getName());
                        if(increasing) {
                            // ->
                            timelineHelperMap.put(name,ref1.globDate);
                            // <-
                            timelineHelperMap.put(name_backwards,ref1.globDate);
                        } else {
                            // ->
                            AbsoluteDate start = timelineHelperMap.get(name);
                            if(!timelines.get(detector.getTopocentricFrame().getName()).contains(new SatFlightData(ref.GlobalKey))){
                                timelines.get(detector.getTopocentricFrame().getName()).add(new SatFlightData(ref.GlobalKey));
                            }
                            int index = timelines.get(detector.getTopocentricFrame().getName()).indexOf(new SatFlightData(ref.GlobalKey));
                            timelines.get(detector.getTopocentricFrame().getName()).get(index).addInterval(new TimeInterval(start,ref1.globDate));
                            // <-
                            if(!timelines.get(ref.GlobalKey).contains(new SatFlightData(detector.getTopocentricFrame().getName()))){
                                timelines.get(ref.GlobalKey).add(new SatFlightData(detector.getTopocentricFrame().getName()));
                            }
                            int index_backwards = timelines.get(ref.GlobalKey).indexOf(new SatFlightData(detector.getTopocentricFrame().getName()));
                            timelines.get(ref.GlobalKey).get(index_backwards).addInterval(new TimeInterval(start,ref1.globDate));
                        }
                        return Action.CONTINUE;
                    });
            for(Map.Entry<String, Propagator> p : orbits.entrySet()){
                p.getValue().addEventDetector(a);
            }

            */

        }

        final AbsoluteDate finalDate = initialDate.shiftedBy(duration);



        for (AbsoluteDate extrapDate = initialDate;
             extrapDate.compareTo(finalDate) <= 0;
             extrapDate = extrapDate.shiftedBy(stepT)) {
            Map<String,SpacecraftState> curState= new HashMap<>();
            for(Map.Entry<String, Propagator> p : orbits.entrySet()){
                ref.GlobalKey = p.getKey();
                ref1.globDate =extrapDate;
                curState.put(p.getKey(), p.getValue().propagate(extrapDate)); // Use HashMap to store the cur state

            }

            //check visibility for cities
            for(TopocentricFrame c : cityFrames){
                Vector3D coord = c.getPVCoordinates(extrapDate,earthFrame).getPosition(); // 99% the basis of the vector system is earth itself so it wont realy move xd


                for(Map.Entry<String, SpacecraftState> Sat : curState.entrySet()){
                    SpacecraftState ss = Sat.getValue();
                    double degree = Math.abs(Math.toDegrees(c.getElevation(ss.getPVCoordinates().getPosition() , ss.getFrame(),extrapDate)));
                    double distance = coord.distance(ss.getPVCoordinates().getPosition());
                    //TODO: implement the interval gen as below
                }


            }


            // check for visibility inter satellites
            for(Map.Entry<String, SpacecraftState> spaceState_outer : curState.entrySet()){
                for(Map.Entry<String, SpacecraftState> spaceState_inner : curState.entrySet()){
                    if(spaceState_inner!=spaceState_outer){
                        String name = String.format("%s->%s",spaceState_outer.getKey(),spaceState_inner.getKey());

                    if(Utility.SatVisible(spaceState_outer.getValue(),spaceState_inner.getValue())){
                            timelineHelperMap.putIfAbsent(name, extrapDate);
                            dataMap.putIfAbsent(name,new ArrayList<>());
                            Vector3D posOuter = spaceState_outer.getValue().getPVCoordinates().getPosition();
                            Vector3D posInner = spaceState_inner.getValue().getPVCoordinates().getPosition();
                            double distance = posInner.distance(posOuter);
                            dataMap.get(name).add(distance);
                    } else if(timelineHelperMap.get(name)!=null) { // end: first time iteration when there is no visibility
                        AbsoluteDate start = timelineHelperMap.get(name);

                        if(!timelines.get(spaceState_outer.getKey()).contains(new SatFlightData(spaceState_inner.getKey()))){
                            timelines.get(spaceState_outer.getKey()).add(new SatFlightData(spaceState_inner.getKey()));
                        }
                        int index = timelines.get(spaceState_outer.getKey()).indexOf(new SatFlightData(spaceState_inner.getKey()));
                        timelines.get(spaceState_outer.getKey()).get(index).addIntervalWithData(new TimeInterval(start,extrapDate),new IntervalData(dataMap.get(name)));
                        // clean helpers
                        dataMap.put(name,null);
                        timelineHelperMap.put(name,null);
                    }
                }
            }
        }}
        return timelines;
    }

    public static void GenerateGraph(Map<String, SatTimeline> timelineMap, String city1,String city2) {
        double MAX_WINDOW_SIZE = 0;
        final AbsoluteDate initialDate = new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC());

        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
                double tmp_max = Utility.GetMaxWindow(a.getValue());
                if (tmp_max > MAX_WINDOW_SIZE)
                    MAX_WINDOW_SIZE = tmp_max;
            }
        }
        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            System.out.println(a.getKey()+ "[shape=circle]");
        }


            for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
               //a.getValue().recursiveStuff(initialDate, MAX_TIME,MAX_WINDOW_SIZE);
                for(Map.Entry<SatTimeline, ArrayList<AbsoluteDate>> timeline : a.getValue().timelineList.entrySet()) {
                    ArrayList<TimeInterval> intervals = Utility.getTimeIntervals(timeline.getValue());
                    for(TimeInterval t : intervals){
                        System.out.println(a.getKey() + "->" + timeline.getKey().name + " [label=" + t.end.durationFrom(t.start) + "]");
                        timeline.getKey().recursiveStuff(t.end.shiftedBy(10),MAX_TIME,t.end.durationFrom(t.start));
                    }
            }
        }

    }
}
}
