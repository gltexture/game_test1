package ru.BouH.engine.physics.collision.objects;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btConvexHullShape;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;

public class ConvexShape extends AbstractCollision {
    private final Vector3d[] points;

    public ConvexShape(double scale, Vector3d[] points, float weight, btMotionState motionState, btVector3 inertia) {
        super(scale, weight, motionState, inertia);
        this.points = points;
    }

    public ConvexShape(float weight, Vector3d[] points, btMotionState motionState, btVector3 inertia) {
        this(1.0d, points, weight, motionState, inertia);
    }

    public ConvexShape(Vector3d[] points, btMotionState motionState, btVector3 inertia) {
        this(0.0f, points, motionState, inertia);
    }

    public Vector3d[] getPoints() {
        return this.points;
    }

    @Override
    protected btCollisionShape buildCollisionShape(double scale) {
        btConvexHullShape convexHullShape = new btConvexHullShape();
        for (Vector3d vector3d : this.points) {
            convexHullShape.addPoint(new btVector3(vector3d.x, vector3d.y, vector3d.z));
        }
        return convexHullShape;
    }
}
