package com.company;

import org.hipparchus.util.FastMath;

public class City {
     double longitude = FastMath.toRadians(47.49840560);
     double latitude = FastMath.toRadians(19.04075780);
     double altitude = 106.46;
    String name = "Budapest";
    City(){}
    City(double _longitude, double _latitude, double _altitude, String _name){
        longitude=FastMath.toRadians(_longitude);latitude=FastMath.toRadians(_latitude);altitude=_altitude;name=_name;
    }
}