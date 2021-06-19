package com.company;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.attitudes.Attitude;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.awt.*;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Map;


public class Utility {
    public static boolean SatVisible(SpacecraftState spaceState_outer, SpacecraftState spaceState_inner) {
         Vector3D pos_outer =  spaceState_outer.getOrbit().getPVCoordinates().getPosition(); // it in meters :D
         Vector3D pos_inner = spaceState_inner.getOrbit().getPVCoordinates().getPosition();
         // We asume that sats are not too close to the earth
        Vector3D earthCore = new Vector3D(0,0,0);
        Vector3D closestP = getClosestP(pos_inner,pos_outer,earthCore);

        double tmp = closestP.distance(earthCore);
        //System.out.print(tmp+" , ");
        return tmp>((6371+500)*1000);

    }
    //https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
    private static Vector3D getClosestP(Vector3D A, Vector3D B, Vector3D P) {
        double t = 0;
        Vector3D v = B.subtract(A);
        Vector3D u = A.subtract(P);
        t = -1*(v.dotProduct(u)/v.dotProduct(v));
        if (t<0 || 1<t){
            t= 0;
            Vector3D a1 = A.scalarMultiply(1-t).add(B.scalarMultiply(t)).subtract(P); // (1−t)A+tB−P
            double g1 = FastMath.sqrt(a1.getX()*a1.getX()+a1.getY()*a1.getY()+a1.getZ()*a1.getZ());
            t=1;
            Vector3D a2 = A.scalarMultiply(1-t).add(B.scalarMultiply(t)).subtract(P); // (1−t)A+tB−P
            double g2 = FastMath.sqrt(a2.getX()*a2.getX()+a2.getY()*a2.getY()+a2.getZ()*a2.getZ());
            return g1<g2 ? A : B;

        }
        return A.scalarMultiply(1-t).add(B.scalarMultiply(t)); //(1−t)A+tB
    }

    public static double GetMaxWindow(SatTimeline a) {
        return a.getMaxWindow();
    }



    public static ArrayList<TimeInterval> getTimeIntervals(ArrayList<AbsoluteDate> timeSet) {
        ArrayList<TimeInterval> out = new ArrayList<>();
        AbsoluteDate start  =  new AbsoluteDate(2021, 01, 01, 23, 30, 00.000, TimeScalesFactory.getUTC()) ;
        AbsoluteDate tmp_start = null;
        AbsoluteDate tmp_end = null;
        boolean first = true;
        boolean last = false;
        AbsoluteDate lastDate = timeSet.get(0);
        for(AbsoluteDate date : timeSet){
            if(first) {
                tmp_start = date;
                first = false;
            }

            if(last){
                tmp_end=lastDate;
                first = true;
                last = false;
                out.add(new TimeInterval(tmp_start,tmp_end));
            }

            if(date.durationFrom(lastDate)>5){
                last=true;
            }

            lastDate=date;
        }

        return out;
    }
    public static ArrayList<TimeInterval> getTimeIntervalsSetTime(ArrayList<AbsoluteDate> timeSet,AbsoluteDate startDate) {
        ArrayList<TimeInterval> out = new ArrayList<>();
        AbsoluteDate tmp_start = null;
        AbsoluteDate tmp_end = null;
        boolean first = true;
        boolean last = false;
        for (AbsoluteDate extrapDate = startDate;
             extrapDate.compareTo(timeSet.get(timeSet.size()-1)) <= 0;
             extrapDate = extrapDate.shiftedBy(SatOrbitProbagation.stepT)) {

            if(timeSet.contains(extrapDate)){
                if(first) {
                    tmp_start = extrapDate;
                    first = false;
                    last = true;
                }
                tmp_end=extrapDate;
            } else if(last){
                first = true;
                last= false;
                out.add(new TimeInterval(tmp_start,tmp_end));
            }
        }
        return out;
    }
    public static ArrayList<TimeInterval> getTimeIntervalsSetTimeWithMAXWindow(ArrayList<AbsoluteDate> timeSet,AbsoluteDate startDate, double MAX_WINDOW) {
        ArrayList<TimeInterval> out = new ArrayList<>();
        AbsoluteDate tmp_start = null;
        AbsoluteDate tmp_end = null;
        boolean first = true;
        boolean last = false;
        double cnt = 0; //FOR MAX_WINDOW
        for (AbsoluteDate extrapDate = startDate;
             extrapDate.compareTo(timeSet.get(timeSet.size()-1)) <= 0;
             extrapDate = extrapDate.shiftedBy(SatOrbitProbagation.stepT)) {

            if(cnt>=MAX_WINDOW){
                first = true;
                last= false;
                out.add(new TimeInterval(tmp_start,tmp_end));
                cnt=0;
            } else if(timeSet.contains(extrapDate)){
                cnt++;
                if(first) {
                    tmp_start = extrapDate;
                    first = false;
                    last = true;
                }
                tmp_end=extrapDate;
            } else if(last){
                first = true;
                last= false;
                out.add(new TimeInterval(tmp_start,tmp_end));
                cnt=0;
            }


        }
        return out;
    }
}
