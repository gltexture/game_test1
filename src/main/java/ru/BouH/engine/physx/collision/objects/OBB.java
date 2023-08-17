package ru.BouH.engine.physx.collision.objects;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.MotionState;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;

public class OBB extends AbstractCollision {
    private final Vector3d size;

    public OBB(double scale, Vector3d size, float weight, MotionState motionState, BPVector3f inertia) {
        super(scale, weight, motionState, inertia);
        this.size = size;
    }

    public OBB(float weight, Vector3d size, MotionState motionState, BPVector3f inertia) {
        this(1.0d, size, weight, motionState, inertia);
    }

    public Vector3d getSize() {
        return new Vector3d(this.size).mul(this.getScale());
    }

    @Override
    protected CollisionShape buildCollisionShape(double scale) {
        Vector3d vector3d = this.getSize();
        return new BoxShape(new BPVector3f((float) (vector3d.x / 2.0f), (float) (vector3d.y / 2.0f), (float) (vector3d.z / 2.0f)));
    }
}
