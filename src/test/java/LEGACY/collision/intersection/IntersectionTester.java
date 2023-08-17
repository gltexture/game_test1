package LEGACY.collision.intersection;

import LEGACY.collision.IBox;
import LEGACY.collision.ICollision;
import LEGACY.collision.objects.CollisionOrientedBox3D;
import org.joml.Math;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.g_static.profiler.SectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntersectionTester {
    public static IntersectionTester instance = new IntersectionTester();

    public IntersectionTester() {
        Game.getGame().getProfiler().startSection(SectionManager.intersectionTest);
    }

    public Vector3d testIntersectionC1C2(ICollision c1, ICollision c2) {
        if (c1 == null || c2 == null || c1.equals(c2)) {
            return null;
        }
        if (this.isBoxBox(c1, c2)) {
            if (this.isObOb(c1, c2)) {
                CollisionOrientedBox3D cl1 = (CollisionOrientedBox3D) c1;
                CollisionOrientedBox3D cl2 = (CollisionOrientedBox3D) c2;
                return this.testObOb(cl1, cl2);
            }
        }
        return null;
    }

    public Vector3d testObOb(CollisionOrientedBox3D c1, CollisionOrientedBox3D c2) {
        return this.projectionIntersection(c1.getVerticesList(), c2.getVerticesList());
    }

    private boolean isBox(ICollision collision) {
        return collision instanceof IBox;
    }

    private boolean isOb(ICollision collision) {
        return collision instanceof CollisionOrientedBox3D;
    }

    private boolean isBoxBox(ICollision collision, ICollision collision2) {
        return this.isBox(collision) && this.isBox(collision2);
    }

    private boolean isObOb(ICollision collision, ICollision collision2) {
        return this.isOb(collision) && this.isOb(collision2);
    }


    private Vector3d projectionIntersection(List<Vector3d> l1, List<Vector3d> l2) {
        List<Vector3d> axis = this.getAxis(l1, l2);
        Vector3d normal = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        for (Vector3d axisVector : axis) {
            Vector2d projection1 = this.projectionAxis(l1, axisVector);
            Vector2d projection2 = this.projectionAxis(l2, axisVector);
            double maxV1 = projection1.y;
            double minV1 = projection1.x;
            double maxV2 = projection2.y;
            double minV2 = projection2.x;
            List<Double> points = new ArrayList<Double>() {{
                add(minV1);
                add(minV2);
                add(maxV1);
                add(maxV2);
            }};
            Collections.sort(points);
            double s1 = (maxV2 - minV2) + (maxV1 - minV1);
            double length = Math.abs(points.get(3) - points.get(0));
            if (s1 <= length) {
                return null;
            }
            double dl = Math.abs(points.get(2) - points.get(1));
            if (dl < normal.length()) {
                normal = axisVector.mul(dl);
                if (points.get(0) != minV1) {
                    normal.negate();
                }
            }
        }
        return normal;
    }

    private Vector2d projectionAxis(List<Vector3d> points, Vector3d axis) {
        double min = this.findProjection(points.get(0), axis);
        double max = this.findProjection(points.get(0), axis);
        for (int i = 1; i < points.size(); i++) {
            double d1 = this.findProjection(points.get(i), axis);
            if (d1 > max) {
                max = d1;
            }
            if (d1 < min) {
                min = d1;
            }
        }
        return new Vector2d(min, max);
    }


    private List<Vector3d> getAxis(List<Vector3d> l1, List<Vector3d> l2) {
        Vector3d vectorA;
        Vector3d vectorB;

        List<Vector3d> axis = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            vectorA = new Vector3d(l1.get(i)).sub(l1.get(0));
            vectorB = new Vector3d(l1.get((i + 1) % 3 + 1)).sub(l1.get(0));
            axis.add(vectorA.cross(vectorB).normalize());
        }

        for (int i = 1; i < 4; i++) {
            vectorA = new Vector3d(l2.get(i)).sub(l2.get(0));
            vectorB = new Vector3d(l2.get((i + 1) % 3 + 1)).sub(l2.get(0));
            axis.add(vectorA.cross(vectorB).normalize());
        }

        for (int i = 1; i < 4; i++) {
            vectorA = new Vector3d(l1.get(i)).sub(l1.get(0));
            for (int j = 1; j < 4; j++) {
                vectorB = new Vector3d(l2.get(j)).sub(l2.get(0));
                Vector3d vector3d = vectorA.cross(vectorB);
                if (vector3d.length() != 0) {
                    axis.add(vector3d.normalize());
                }
            }
        }

        return axis;
    }

    private double findProjection(Vector3d vector3d1, Vector3d vector3d2) {
        Vector3d v1 = new Vector3d(vector3d1);
        Vector3d v2 = new Vector3d(vector3d2).normalize();
        return v1.dot(v2) / v2.length();
    }
}
