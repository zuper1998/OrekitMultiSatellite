package com.company;

import org.hipparchus.util.FastMath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Satellite_Sajat {
    String Name;
    double a; // semi major axis in meters
    double e; // eccentricity
    double i; // inclination
    double omega; // perigee argument
    double raan; // right ascension of ascending node
    double lM; // Mean Anomaly M = sqrt(mu/a^3) time
    // gravitation coefficient
    final static double mu =  3.986004415e+14;

    Satellite_Sajat(double _a, double _e, double _i, double _omega, double _raan, double _lM, String _Name){
        a=_a;
        e=_e;
        i=_i;
        omega=_omega;
        raan=_raan;
        lM=_lM;
        Name = _Name;
    }

    public static ArrayList<Satellite_Sajat> SatLoader(String FileName){
        File f = new File(FileName);
        ArrayList<Satellite_Sajat> sats = new ArrayList<>();
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader(f));

        // Line Format: semi-major axis| eccentricity | inclination | longitude asc. node | argument of periapsis | time of periapsis | sat name
        String s;
        while ((s = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(s,"|");
            // semi major axis
            double semi = (Double.parseDouble(st.nextToken())+6371) *1000;
            // Eccentricity
            double ecc  =  Double.parseDouble(st.nextToken());
            // Inclination
            double inc = FastMath.toRadians(Double.parseDouble(st.nextToken()));
            // raan
            double raas = FastMath.toRadians(Double.parseDouble(st.nextToken()));
            // omega
            double omega = FastMath.toRadians(Double.parseDouble(st.nextToken()));
            // Mean Anomaly
            //double lM = FastMath.toRadians( Double.parseDouble(st.nextToken())); //FastMath.toDegrees(Math.sqrt(mu/(semi*semi*semi)) * Double.parseDouble(st.nextToken()) % 360);
            double lM = FastMath.toRadians(Math.sqrt(mu/(semi*semi*semi)) * Double.parseDouble(st.nextToken()) % 360);
            //System.out.println(lM);
            sats.add(new Satellite_Sajat(semi,ecc,inc,raas,omega,lM,st.nextToken().strip()));

        }

        } catch (Exception e){
            System.out.println(e);
        }
        return sats;
    }
}
