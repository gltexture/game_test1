package ru.BouH.engine.physics.jb_objects;

import ru.BouH.engine.physics.world.object.IWorldDynamic;

public interface JBulletEntity extends IWorldDynamic {
    RigidBodyObject getRigidBodyObject();

    default boolean isValid() {
        return this.getRigidBodyObject() != null && !this.getRigidBodyObject().isNull() && this.getRigidBodyObject().isInWorld();
    }
}
