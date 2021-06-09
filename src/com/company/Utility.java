package com.company;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.attitudes.Attitude;
import org.orekit.propagation.SpacecraftState;


public class Utility {
    public static boolean SatVisible(SpacecraftState spaceState_outer, SpacecraftState spaceState_inner) {
         Vector3D pos_outer =  spaceState_outer.getOrbit().getPVCoordinates().getPosition(); // it in meters :D
         Vector3D pos_inner = spaceState_inner.getOrbit().getPVCoordinates().getPosition();
         // We asume that sats are not too close to the earth
        Vector3D earthCore = new Vector3D(0,0,0);
        Vector3D closestP = getClosestP(pos_inner,pos_outer,earthCore);


        return closestP.distance(earthCore)>((6371+200)*1000);

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
}
