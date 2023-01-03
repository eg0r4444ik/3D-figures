package cg.models;

import cg.math.Matrix4;
import cg.math.Matrix4Factories;
import cg.math.Vector3;
import cg.math.Vector4;
import cg.third.IModel;
import cg.third.PolyLine3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Torus3D implements IModel {
    private List<Vector3> points = new LinkedList<>();
    private List<PolyLine3D> lines = new LinkedList<>();
    private final int count = 20;

    public Torus3D(double a, double r, double h) {
        List<Vector3> pointsList = new ArrayList<>();

        for (float t = 0; t < Math.PI * 2.01; t += .02f) {
            double x = Math.cos(t);
            double y = Math.sin(t);
            double z = 0;
            Vector3 newP = new Vector3((float) x, (float) y, (float) z);
            pointsList.add(newP);
        }

        Vector3[] points = new Vector3[pointsList.size() + 2];
        for (int i = 0; i < points.length - 2; i++) {
            points[i] = pointsList.get(i);
        }
        points[pointsList.size()] = pointsList.get(1);
        points[pointsList.size() + 1] = pointsList.get(2);

        Vector3[] vertices = new Vector3[count], nextVertices = new Vector3[count];
        for (int i = 2; i < points.length; i++) {
            Vector3 pa = points[i - 2];
            Vector3 pb = points[i - 1];
            Vector3 pc = points[i];
            Vector3 mid1 = Vector3.mid(pa, pb);
            Vector3 mid2 = Vector3.mid(pb, pc);

            Vector3 normal = new PolyLine3D(Arrays.asList(pa, pb, pc), true).getNormal();

            Vector3 ab = Vector3.sub(pa, pb);
            Vector3 bc = Vector3.sub(pb, pc);
            Vector3 dir1 = Vector3.cross(normal, ab);
            Vector3 dir2 = Vector3.cross(normal, bc);

            Vector3 center = Vector3.intersection(mid1, dir1, mid2, dir2);

            if (i == 2) {
                Vector3 rDir = Vector3.cross(dir1, ab).normalize();
                rDir = Matrix4Factories.scale((float)r).mul(new Vector4(rDir, 1)).asVector3();
                Vector3 p = Vector3.add(mid1, rDir);
                for (int j = 0; j < count; j++) {
                    vertices[j] = new Vector3(p.getX(), p.getY(), p.getZ());
                    p = p.rotateAround(pa, pb, (float) (Math.PI * 2 / count));
                }
            }

            for (int j = 0; j < count; j++) {
                nextVertices[j] = vertices[j].rotateAround(
                        Vector3.add(center, normal),
                        Vector3.sub(center, normal),
                        (float) (-Math.acos(dir1.cos(dir2)))
                );
            }

            for (int j = 1; j < count; j++) {
                lines.add(new PolyLine3D(Arrays.asList(nextVertices[j - 1], vertices[j - 1], vertices[j]), true));
                lines.add(new PolyLine3D(Arrays.asList(nextVertices[j], nextVertices[j - 1], vertices[j]), true));
            }
            lines.add(new PolyLine3D(Arrays.asList(nextVertices[count - 1], vertices[count - 1], vertices[0]), true));
            lines.add(new PolyLine3D(Arrays.asList(nextVertices[0], nextVertices[count - 1], vertices[0]), true));

            for (int j = 0; j < count; j++) {
                vertices[j] = nextVertices[j];
            }
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
