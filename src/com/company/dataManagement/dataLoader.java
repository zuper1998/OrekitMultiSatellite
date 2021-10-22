package com.company.dataManagement;

import com.company.Graph.DynamicHelper.AllPathsReturn;
import com.company.Graph.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class  dataLoader {
    public static ArrayList<AllPathsReturn> loadAllPaths(String filepath){
        File[] directories = new File(filepath).listFiles(File::isDirectory);
        ArrayList<AllPathsReturn> out = new ArrayList<>();
        assert directories != null;

        for(File dir_out : directories){
            for(File file : Objects.requireNonNull(dir_out.listFiles(File::isFile))){
                FileInputStream fis = null;
                try {
                    //System.out.println(file);
                    fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    out.add ((AllPathsReturn) ois.readObject());
                    ois.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    return out;
    }
}
