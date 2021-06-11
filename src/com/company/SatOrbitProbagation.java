package com.company;

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
    public static double duration = 3600*6;
    public static Map<String,SatTimeline> Generate(){
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
    final Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);

        var ref = new Object() {
            String GlobalKey = "";
        };
    Map<String,SatTimeline> timelines = new HashMap<>();
    //prepare timelines
    for(Map.Entry<String, Propagator> p : orbits.entrySet()){
        timelines.put(p.getKey(),new SatTimeline(p.getKey()));
    }
    for(City c : cities){
        timelines.put(c.name,new SatTimeline(c.name));
    }
        var ref1 = new Object() {
            AbsoluteDate globDate = initialDate;
        };

    final BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
            Constants.WGS84_EARTH_FLATTENING,
            earthFrame);
    //Overpass ddetection
        for (City c: cities) {
            final GeodeticPoint station1 = new GeodeticPoint(c.latitude, c.longitude, c.altitude);
            final TopocentricFrame sta1Frame = new TopocentricFrame(earth, station1, c.name);
            // Event definition
            final double maxcheck = 60.0;
            final double threshold = 0.001;
            final double elevation = FastMath.toRadians(20.0);
            EventDetector a = new ElevationDetector(maxcheck, threshold, sta1Frame).
                    withConstantElevation(elevation).
                    withHandler((s, detector, increasing) -> { //TODO: fill the interval TODO2: refactor so the interval filling is in the main branch use only atomic bool here
                        if(increasing) {
                            timelines.get(detector.getTopocentricFrame().getName()).AddElement(ref1.globDate, timelines.get(ref.GlobalKey));
                            timelines.get(ref.GlobalKey).AddElement(ref1.globDate, timelines.get(detector.getTopocentricFrame().getName()));
                        } else {
                            AbsoluteDate startP = initialDate;
                            for (AbsoluteDate extrapDate = startP;
                                 extrapDate.compareTo(s.getDate()) <= 0;
                                 extrapDate = extrapDate.shiftedBy(stepT)) {
                                if(extrapDate.isAfter(timelines.get(detector.getTopocentricFrame().getName()).getLast(timelines.get(ref.GlobalKey)))) {
                                    timelines.get(detector.getTopocentricFrame().getName()).AddElement(extrapDate, timelines.get(ref.GlobalKey));
                                    timelines.get(ref.GlobalKey).AddElement(extrapDate, timelines.get(detector.getTopocentricFrame().getName()));
                                }
                            }
                        }
                        return Action.CONTINUE;
                    });
            for(Map.Entry<String, Propagator> p : orbits.entrySet()){
                p.getValue().addEventDetector(a);
            }



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

            // check for visibility inter satellites
            for(Map.Entry<String, SpacecraftState> spaceState_outer : curState.entrySet()){
                for(Map.Entry<String, SpacecraftState> spaceState_inner : curState.entrySet()){
                    if(spaceState_inner!=spaceState_outer)
                    if(Utility.SatVisible(spaceState_outer.getValue(),spaceState_inner.getValue())){
                        timelines.get(spaceState_outer.getKey()).AddElement(extrapDate,timelines.get(spaceState_inner.getKey()));
                    }
                }

            }

        }
        //timelines.forEach((s, satTimeline) -> {satTimeline.print();});
        timelines.get("Budapest").print();
        return timelines;
    }

    public static void GenerateGraph(Map<String, SatTimeline> timelineMap, String city1,String city2) {
        double MAX_WINDOW_SIZE = 0;
        for(Map.Entry<String, SatTimeline> a : timelineMap.entrySet()) {
            if (a.getKey().equals(city1) || a.getKey().equals(city2)) {
                double tmp_max = Utility.GetMaxWindow(a.getValue());
                if (tmp_max > MAX_WINDOW_SIZE)
                    MAX_WINDOW_SIZE = tmp_max;
            }
        }
        System.out.println(MAX_WINDOW_SIZE);

    }
}
