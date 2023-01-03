package cg.math;

import java.util.Arrays;

/**
 * Класс, хранящий трёхмерный вектор / точку в трёхмерном пространстве.
 * @author Alexey
 */
public class Vector3 {
    private float[] values; /*Значения хранятся в виде массива из трёх элементов*/
    public static double E = 1e-6;
    /**
     * Создаёт экземпляр вектора на основе значений трёх составляющих
     * @param x первая составляющая, описывающая X-координату
     * @param y вторая составляющая, описывающая Y-координату
     * @param z третья составляющая, описывающая Z-координату
     */
    public Vector3(float x, float y, float z) {
        values = new float[]{x, y, z};
    }

    /**
     * X-составляющая вектора
     * @return X-составляющая вектора
     */
    public float getX() {
        return values[0];
    }

    /**
     * Y-составляющая вектора
     * @return Y-составляющая вектора
     */
    public float getY() {
        return values[1];
    }

    /**
     * Z-составляющая вектора
     * @return Z-составляющая вектора
     */
    public float getZ() {
        return values[2];
    }

    /**
     * Метод, возвращающий составляющую вектора по порядковому номеру
     * @param idx порядковый номер
     * @return значение составляющей вектора
     */
    public float at(int idx) {
        return values[idx];
    }

    private static final float EPSILON = 1e-10f;
    /**
     * Метод, возвращающий длину вектора
     * @return длина вектора
     */
    public float length() {
        float lenSqr = values[0] * values[0] + values[1] * values[1] + values[2] * values[2];
        if (lenSqr < EPSILON)
            return 0;
        return (float)Math.sqrt(lenSqr);
    }

    public static Vector3 add(Vector3 v1, Vector3 v2) {
        return new Vector3(
                v1.getX() + v2.getX(),
                v1.getY() + v2.getY(),
                v1.getZ() + v2.getZ()
        );
    }

    public static Vector3 sub(Vector3 v1, Vector3 v2) {
        return new Vector3(
                v1.getX() - v2.getX(),
                v1.getY() - v2.getY(),
                v1.getZ() - v2.getZ()
        );
    }

    public static Vector3 mid(Vector3 v1, Vector3 v2) {
        return new Vector3(
                (v1.getX() + v2.getX()) / 2,
                (v1.getY() + v2.getY()) / 2,
                (v1.getZ() + v2.getZ()) / 2
        );
    }

    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3(
                v1.getY() * v2.getZ() - v1.getZ() * v2.getY(),
                v1.getZ() * v2.getX() - v1.getX() * v2.getZ(),
                v1.getX() * v2.getY() - v1.getY() * v2.getX()
        );
    }

    public static Vector3 intersection(Vector3 p1, Vector3 dir1, Vector3 p2, Vector3 dir2) {
        float a1 = dir1.getX(), b1 = dir1.getY(), c1 = dir1.getZ();
        float a2 = dir2.getX(), b2 = dir2.getY(), c2 = dir2.getZ();

        float m = (p2.getY() - p1.getY() + b1 / a1 * (p1.getX() - p2.getX())) / (b1 * a2 / a1 - b2);
        return new Vector3(a2 * m + p2.getX(), b2 * m + p2.getY(), c2 * m + p2.getZ());
//        return null;
    }

    public Vector3 normalize() {
        double l = Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
        values[0] /= l;
        values[1] /= l;
        values[2] /= l;
        return this;
    }

    public Vector3 rotateAround(Vector3 p1, Vector3 p2, float t) {
        Matrix4 tr = Matrix4Factories.translation(new Vector3(-p1.getX(), -p1.getY(), -p1.getZ()));
        Matrix4 tr1 = Matrix4Factories.translation(p1);

        Matrix4 rx = Matrix4.one(), rx1 = Matrix4.one();
        Vector3 u = Vector3.sub(p1, p2).normalize();
        float d = (float) Math.sqrt(u.getY() * u.getY() + u.getZ() * u.getZ());
        float a = u.getX(), b = u.getY(), c = u.getZ();
        if (d > E) {
            rx = new Matrix4(
                    1, 0, 0, 0,
                    0, c / d, -b / d, 0,
                    0, b / d, c / d, 0,
                    0, 0, 0, 1
            );
            rx1 = new Matrix4(
                    1, 0, 0, 0,
                    0, c / d, b / d, 0,
                    0, -b / d, c / d, 0,
                    0, 0, 0, 1
            );
        }

        Matrix4 ry = new Matrix4(
                d, 0, -a, 0,
                0, 1, 0, 0,
                a, 0, d, 0,
                0, 0, 0, 1
        );
        Matrix4 ry1 = new Matrix4(
                d, 0, a, 0,
                0, 1, 0, 0,
                -a, 0, d, 0,
                0, 0, 0, 1
        );

        float sin = (float) Math.sin(t);
        float cos = (float) Math.cos(t);
        Matrix4 rz = new Matrix4(
                cos, sin, 0, 0,
                -sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );

        Vector3 res = tr1.mul(
                rx1.mul(
                        ry1.mul(
                                rz.mul(
                                        ry.mul(
                                                rx.mul(
                                                        tr.mul(
                                                                new Vector4(this, 1)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ).asVector3();

        return res;
    }

    public float cos(Vector3 v) {
        return (float) ((v.getX()*getX() + v.getY()*getY() + v.getZ()*getZ())/(Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2) + Math.pow(v.getZ(), 2))*Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2))));
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
