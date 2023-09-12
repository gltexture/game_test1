package ru.BouH.engine.physics.collision.objects;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.linearmath.MotionState;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;

public class StaticPlane extends AbstractCollision {
    private final Vector3d normal;

    public StaticPlane(double scale, Vector3d normal, float weight, MotionState motionState, BPVector3f inertia) {
        super(scale, weight, motionState, inertia);
        this.normal = normal;
    }

    public StaticPlane(float weight, Vector3d normal, MotionState motionState, BPVector3f inertia) {
        this(1.0d, normal, weight, motionState, inertia);
    }

    public StaticPlane(Vector3d normal, MotionState motionState, BPVector3f inertia) {
        this(1.0d, normal, 0.0f, motionState, inertia);
    }

    @Override
    protected CollisionShape buildCollisionShape(double scale) {
        return new StaticPlaneShape(new BPVector3f((float) normal.x, (float) normal.y, (float) normal.z), 0.0f);
    }
}
