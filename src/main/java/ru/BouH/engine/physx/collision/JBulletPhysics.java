package ru.BouH.engine.physx.collision;

import com.bulletphysics.dynamics.RigidBody;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;

public interface JBulletPhysics {
    AbstractCollision getCollision();
    RigidBody getRigidBody();
    void onJBUpdate();
    default boolean hasCollision() {
        return this.getRigidBody() != null && this.getCollision() != null;
    }
}
