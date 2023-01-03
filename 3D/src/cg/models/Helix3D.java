package cg.models;



import cg.math.Vector3;
import cg.third.IModel;
import cg.third.PolyLine3D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Helix3D implements IModel {
    private List<Vector3> points = new LinkedList<>();
    private List<PolyLine3D> lines = new LinkedList<>();

    public Helix3D(int numberOfLoops, double a, double r, double h) {
        double d = Math.sqrt(r * r + h * h);
        List<Vector3> previousVectors = new ArrayList<>();
        for (double t = 0; t <= 2 * numberOfLoops * Math.PI; t += Math.PI / 24 ) {
            List<Vector3> tempVectors = new ArrayList<>();
            for (double u = 0; u <= 2 * Math.PI; u += 0.5) {
                double x = h * t + (r * a * Math.sin(u)) / d;
                double y = r * Math.cos(t) - a * Math.cos(t) * Math.cos(u) + (h * a * Math.sin(t) * Math.sin(u)) / d;
                double z = r * Math.sin(t) - a * Math.sin(t) * Math.cos(u) - (h * a * Math.cos(t) * Math.sin(u)) / d;
                tempVectors.add(new Vector3((float) x, (float) y, (float) z));
            }
            if (previousVectors.size() != 0) {
                for (int i = 0; i < tempVectors.size(); i++) {
                    int next = i + 1;
                    List<Vector3> firstTriangle = new LinkedList<>();
                    List<Vector3> secondTriangle = new LinkedList<>();
                    if (i == tempVectors.size() - 1){
                        next = 0;
                    }
                    firstTriangle.add(previousVectors.get(i));
                    firstTriangle.add(previousVectors.get(next));
                    firstTriangle.add(tempVectors.get(i));

                    secondTriangle.add(tempVectors.get(i));
                    secondTriangle.add(tempVectors.get(next));
                    secondTriangle.add(previousVectors.get(next));
                    lines.add(new PolyLine3D(firstTriangle, true));
                    lines.add(new PolyLine3D(secondTriangle, true));
                }
            }
            previousVectors = tempVectors;
        }
    }

    @Override
    public List<PolyLine3D> getLines() {
        if (lines.size() == 0){
            List<PolyLine3D> testLines = new LinkedList<>();
            testLines.add(new PolyLine3D(points, false));
            return testLines;
        } else {
            return lines;
        }
    }
}
