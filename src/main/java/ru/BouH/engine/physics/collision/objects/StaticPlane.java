package ru.BouH.engine.physics.collision.objects;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletCollision.btStaticPlaneShape;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;

public class StaticPlane extends AbstractCollision {
    private final Vector3d normal;

    public StaticPlane(double scale, Vector3d normal, float weight, btMotionState motionState, btVector3 inertia) {
        super(scale, weight, motionState, inertia);
        this.normal = normal;
    }

    public StaticPlane(float weight, Vector3d normal, btMotionState motionState, btVector3 inertia) {
        this(1.0d, normal, weight, motionState, inertia);
    }

    public StaticPlane(Vector3d normal, btMotionState motionState, btVector3 inertia) {
        this(1.0d, normal, 0.0f, motionState, inertia);
    }

    @Override
    protected btCollisionShape buildCollisionShape(double scale) {
        return new btStaticPlaneShape(new btVector3(normal.x, normal.y, normal.z), 0.0f);
    }
}
