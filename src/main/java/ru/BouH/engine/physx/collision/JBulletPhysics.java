package ru.BouH.engine.physx.collision;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;

import javax.vecmath.Quat4f;

public interface JBulletPhysics {
    AbstractCollision getCollision();

    RigidBody getRigidBody();

    void onJBUpdate();

    default Vector3d getRigidBodyRot() {
        Transform transform = new Transform();
        this.getRigidBody().getWorldTransform(transform);
        Quat4f r = new Quat4f();
        MathHelper.getRotation(transform.basis, r);
        return MathHelper.toDegrees(r);
    }

    default Vector3d getRigidBodyPos() {
        Transform transform = new Transform();
        this.getRigidBody().getWorldTransform(transform);
        return new Vector3d(transform.origin.x, transform.origin.y, transform.origin.z);
    }

    default boolean hasCollision() {
        return this.getRigidBody() != null && this.getCollision() != null;
    }
}
