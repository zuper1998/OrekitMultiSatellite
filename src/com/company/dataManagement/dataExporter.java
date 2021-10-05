package com.company.dataManagement;

import Data.SimValues;
import com.company.Graph.DynamicHelper.AllPathsReturn;

import java.io.File;
import java.util.ArrayList;

public class dataExporter {
    static final String fileFolder = String.format("src/Data/Output/Time_%f_Sat_%s/", SimValues.duration / 3600, new File(SimValues.satData).getName());
    public static void exportGraph(){

        ArrayList<AllPathsReturn> allp = dataLoader.loadAllPaths(fileFolder);
        allp.forEach(a -> a.SaveToGraph());



    }
}
