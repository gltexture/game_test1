package ru.BouH.engine.physics.collision.objects;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.util.ObjectArrayList;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;

import javax.vecmath.Vector3f;

public class ConvexShape extends AbstractCollision {
    private final Vector3d[] points;

    public ConvexShape(double scale, Vector3d[] points, float weight, MotionState motionState, BPVector3f inertia) {
        super(scale, weight, motionState, inertia);
        this.points = points;
    }

    public ConvexShape(float weight, Vector3d[] points, MotionState motionState, BPVector3f inertia) {
        this(1.0d, points, weight, motionState, inertia);
    }

    public ConvexShape(Vector3d[] points, MotionState motionState, BPVector3f inertia) {
        this(0.0f, points, motionState, inertia);
    }

    public Vector3d[] getPoints() {
        return this.points;
    }

    @Override
    protected CollisionShape buildCollisionShape(double scale) {
        ObjectArrayList<Vector3f> objectArrayList = new ObjectArrayList<>();
        ConvexHullShape convexHullShape = new ConvexHullShape(objectArrayList);
        for (Vector3d vector3d : this.points) {
            convexHullShape.addPoint(new BPVector3f((float) vector3d.x, (float) vector3d.y, (float) vector3d.z));
        }
        return convexHullShape;
    }
}
