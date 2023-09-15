package ru.BouH.engine.physics.collision.objects;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;

public class OBB extends AbstractCollision {
    private final Vector3d size;

    public OBB(double scale, Vector3d size, float weight, btMotionState motionState, btVector3 inertia) {
        super(scale, weight, motionState, inertia);
        this.size = size;
    }

    public OBB(float weight, Vector3d size, btMotionState motionState, btVector3 inertia) {
        this(1.0d, size, weight, motionState, inertia);
    }

    public Vector3d getSize() {
        return new Vector3d(this.size).mul(this.getScale());
    }

    @Override
    protected btCollisionShape buildCollisionShape(double scale) {
        Vector3d vector3d = this.getSize();
        return new btBoxShape(new btVector3(vector3d.x / 2.0f, vector3d.y / 2.0f, vector3d.z / 2.0f));
    }
}
