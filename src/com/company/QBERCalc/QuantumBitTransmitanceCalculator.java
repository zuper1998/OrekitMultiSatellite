package com.company.QBERCalc;

import accessories.DefaultValues;
import enums.Season;
import enums.Weather;
import protocols.BB84;

public class QuantumBitTransmitanceCalculator {


    public static double  calculateQBITSUMCity(double elevation,double height_above_sea,int dir){
        BB84 calc = new BB84();
        calc.setDirection(dir);
        DefaultValues.absorptionAndScatteringPath = "C:\\Users\\Narcano\\IdeaProjects\\OrekitTest\\src\\com\\company\\accessories\\asv_860.csv";
        DefaultValues.zenithAngle = 90-elevation;
        //DefaultValues.groundSpaceChannelLength = 0; // Should be in KMs IDK what it does
        DefaultValues.heightAboveSeaLevel = height_above_sea;
        DefaultValues.quantumEfficiencyOfDetector = 0.7;  //WARNING
        DefaultValues.season = Season.summer;
        DefaultValues.weather = Weather.clear;
        calc.setEfficiencyOfQuantumOperationsByBob(DefaultValues.efficiencyOfQuantumOperationsByBob);
        calc.setFrequencyOfLaserFiring(DefaultValues.frequencyOfLaserFiring);
        calc.setProbabilityOfPolarizationMeasurementError(DefaultValues.probabilityOfPolarizationMeasurementError);
        calc.setTotalNoise(DefaultValues.totalNoise);
        calc.setNumberOfDetectors(DefaultValues.numberOfDetectors);
        calc.setMeanPhotonNumberOfSignal(DefaultValues.meanPhotonNumberOfSignal);
        calc.setQuantumEfficiencyOfDetector(DefaultValues.quantumEfficiencyOfDetector);
        calc.setWaveLength(DefaultValues.waveLength);
        calc.setZenithAngle(DefaultValues.zenithAngle);
        calc.setWindSpeed(DefaultValues.windSpeed);
        calc.setApertureDiameter(DefaultValues.apertureDiameter);
        calc.setMirrorDiameter(DefaultValues.mirrorDiameter);
        calc.setTargetingAngularError(DefaultValues.targetingAngularError);
        calc.setHeightAboveSeaLevel(DefaultValues.heightAboveSeaLevel);
        calc.setWeather(DefaultValues.weather);
        calc.setClimate(DefaultValues.climate);
        calc.setSeason(DefaultValues.season);
        try {
            calc.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        calc.setOpticalDistance(DefaultValues.heightAboveSeaLevel, DefaultValues.zenithAngle);
        calc.setQber(); // if distance over sea is above 2000 it gets funky xd

        double bits = 0.5 * calc.getFrequencyOfLaserFiring() * DefaultValues.quantumEfficiencyOfDetector * calc.getTransmittance() * calc.getMeanPhotonNumberOfSignal();

        return bits;
    }

    //Since its sat->sat it doesnt need a lot of parameters
    public static double  calculateQBITSUMSat(double distance){
        BB84 calc = new BB84();
        calc.setDirection(1);
        DefaultValues.absorptionAndScatteringPath = "C:\\Users\\Narcano\\IdeaProjects\\OrekitTest\\src\\com\\company\\accessories\\asv_860.csv";
        //DefaultValues.groundSpaceChannelLength = 0; // Should be in KMs IDK what it does

        DefaultValues.quantumEfficiencyOfDetector = 0.7;  //WARNING
        DefaultValues.season = Season.summer;
        DefaultValues.weather = Weather.clear;
        calc.setEfficiencyOfQuantumOperationsByBob(DefaultValues.efficiencyOfQuantumOperationsByBob);
        calc.setFrequencyOfLaserFiring(DefaultValues.frequencyOfLaserFiring);
        calc.setProbabilityOfPolarizationMeasurementError(DefaultValues.probabilityOfPolarizationMeasurementError);
        calc.setTotalNoise(DefaultValues.totalNoise);
        calc.setNumberOfDetectors(DefaultValues.numberOfDetectors);
        calc.setMeanPhotonNumberOfSignal(DefaultValues.meanPhotonNumberOfSignal);
        calc.setQuantumEfficiencyOfDetector(DefaultValues.quantumEfficiencyOfDetector);
        calc.setWaveLength(DefaultValues.waveLength);
        calc.setZenithAngle(DefaultValues.zenithAngle);
        calc.setWindSpeed(DefaultValues.windSpeed);
        calc.setApertureDiameter(DefaultValues.apertureDiameter);
        calc.setMirrorDiameter(DefaultValues.mirrorDiameter);
        calc.setTargetingAngularError(DefaultValues.targetingAngularError);
        calc.setHeightAboveSeaLevel(DefaultValues.heightAboveSeaLevel);
        calc.setWeather(DefaultValues.weather);
        calc.setClimate(DefaultValues.climate);
        calc.setSeason(DefaultValues.season);
        calc.setSpaceSpaceChannelLength(distance);
        try {
            calc.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        calc.setQber(); // if distance over sea is above 2000 it gets funky xd

        double bits = 0.5 * calc.getFrequencyOfLaserFiring() * DefaultValues.quantumEfficiencyOfDetector * calc.getTransmittance() * calc.getMeanPhotonNumberOfSignal();

        return bits;
    }
}
