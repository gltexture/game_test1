package ru.BouH.engine.physics.world.object;

import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btQuaternion;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;

public interface JBulletObject {
    AbstractCollision getCollision();

    btRigidBody getRigidBody();
    default Vector3d getRigidBodyRot() {
        try (btQuaternion quaternion = this.getRigidBody().getOrientation()) {
            double[] x = new double[1];
            double[] y = new double[1];
            double[] z = new double[1];
            quaternion.getEulerZYX(z, y, x);
            return new Vector3d(Math.toDegrees(z[0]), Math.toDegrees(y[0]), Math.toDegrees(x[0])).negate();
        }
    }

    default Vector3d getRigidBodyPos() {
        btTransform transform = this.getRigidBody().getWorldTransform();
        return new Vector3d(transform.getOrigin().getX(), transform.getOrigin().getY(), transform.getOrigin().getZ());
    }

    default boolean hasCollision() {
        return this.getRigidBody() != null && this.getCollision() != null;
    }
}
