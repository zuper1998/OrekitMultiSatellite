package com.company.Graph.DynamicHelper;

import com.company.Graph.ColorsForEdge;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AllPathsReturn {
    Path best;
    ArrayList<Path> otherPaths;

    public AllPathsReturn(Path p, ArrayList<Path> o) {
        best = p;
        otherPaths = o;
    }

    public Path getBest() {
        return best;
    }

    public void Save(int index, BufferedWriter writer) {
        if (index > 14) index = 14;
        ColorsForEdge color = ColorsForEdge.values()[index];
        best.getPath().forEach(edge -> {
            try {
                edge.SaveColorTransmitanceDurationAndPercentUsed(color.name(), best._computeBest(), best.computeBestTransmittance(),writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        for (Path p : otherPaths) {
            p.getPath().forEach(edge -> {
                try {
                    edge.SaveColorAndThrougput("Black", p._computeBest(), p.computeOverallTransmittance(),writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public ArrayList<Path> getOtherPaths() {
        return otherPaths;
    }

}
