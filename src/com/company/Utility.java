package com.company;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.propagation.SpacecraftState;


public class Utility {
    public static boolean SatVisible(SpacecraftState spaceState_outer, SpacecraftState spaceState_inner) {
         Vector3D pos_outer =  spaceState_outer.getOrbit().getPVCoordinates().getPosition(); // it in meters :D
         Vector3D pos_inner = spaceState_inner.getOrbit().getPVCoordinates().getPosition();
         Vector3D zeroPoint = new Vector3D(0,0,0);

        return pos_inner.distance(pos_outer) < (6371+200)*1000; //TODO: actually do a right method for this

    }
}
