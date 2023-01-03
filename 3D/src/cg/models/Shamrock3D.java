package cg.models;

import cg.math.Matrix4;
import cg.math.Matrix4Factories;
import cg.math.Vector3;
import cg.math.Vector4;
import cg.third.IModel;
import cg.third.PolyLine3D;

import java.util.*;

public class Shamrock3D implements IModel {
    private List<Vector3> points = new LinkedList<>();
    private List<PolyLine3D> lines = new LinkedList<>();
    private final int count = 20;

    public Shamrock3D(double a, double r, double h) {
//        double qq = Math.PI / 40;
//
//        for(double t = 0; t <= 2*Math.PI; t += qq){
//            double x = Math.sin(t) + 2*Math.sin(2*t);
//            double y = Math.cos(t) - 2* Math.cos(2*t);
//            double z = -Math.sin(3*t);
//            points.add(new Vector3((float) x,(float) y,(float) z));
//        }
//        lines.add(new PolyLine3D(points, true));
//
//        List<Vector3> previousVectors = new ArrayList<>();
//        Vector3 prevDot = new Vector3((float) (Math.sin(0) + 2*Math.sin(0)) , (float) (Math.cos(0) - 2*Math.cos(0))
//                ,(float) (-Math.sin(0)));
//
//        for (double t = 0; t <= 2 * Math.PI+qq; t += qq ) {
//            List<Vector3> tempVectors = new ArrayList<>();
//            double x = (Math.sin(t) + 2*Math.sin(2*t));
//            double y = (Math.cos(t) - 2*Math.cos(2*t));
//            double z = (-Math.sin(3*t));
//            Vector3 vector = new Vector3((float) x - prevDot.getX(), (float) y - prevDot.getY(), (float) z - prevDot.getZ());
//
//            List<Vector3> circle = new ArrayList<>();
//            for(double u = 0; u <= 2*Math.PI; u += qq){
//                double x1 = r*Math.cos(u);
//                double y1 = r*Math.sin(u);
//                double z1 = 0;
//                circle.add(new Vector3((float) x1, (float) y1,(float) z1));
//            }
//
//            double alpha1 = -Math.atan2(vector.getX(), vector.getZ());
//            double alpha2 = -Math.atan2(vector.getY(), vector.getX());
//            Matrix4 m1 = Matrix4Factories.rotationXYZ(alpha1, Matrix4Factories.Axis.Y);
//            Matrix4 m2 = Matrix4Factories.rotationXYZ(alpha2, Matrix4Factories.Axis.Z);
//
//            for(Vector3 v : circle){
//                Vector4 v4 = m1.mul(m2.mul(new Vector4(v, 0)));
//                tempVectors.add(new Vector3(v4.getX() + (float) x, v4.getY() + (float) y, v4.getZ() + (float) z));
//            }
//
//            if (previousVectors.size() != 0) {
//                for (int i = 0; i < tempVectors.size(); i++) {
//                    int next = i + 1;
//                    List<Vector3> firstTriangle = new LinkedList<>();
//                    List<Vector3> secondTriangle = new LinkedList<>();
//                    if (i == tempVectors.size() - 1){
//                        next = 0;
//                    }
//                    firstTriangle.add(previousVectors.get(i));
//                    firstTriangle.add(previousVectors.get(next));
//                    firstTriangle.add(tempVectors.get(i));
//
//                    secondTriangle.add(tempVectors.get(next));
//                    secondTriangle.add(tempVectors.get(i));
//                    secondTriangle.add(previousVectors.get(next));
//                    lines.add(new PolyLine3D(firstTriangle, true));
//                    lines.add(new PolyLine3D(secondTriangle, true));
//                }
//            }
//            lines.add(new PolyLine3D(tempVectors, true));
//
//            previousVectors = tempVectors;
//            prevDot = new Vector3((float) x, (float) y,(float) z);
//        }

        List<Vector3> pointsList = new ArrayList<>();

        for (float t = 0; t < Math.PI * 2.01; t += .02f) {
            double x = (Math.sin(t) + 2*Math.sin(2*t));
            double y = (Math.cos(t) - 2*Math.cos(2*t));
            double z = (-Math.sin(3*t));
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