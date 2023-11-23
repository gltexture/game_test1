package ru.BouH.engine.physics.jb_objects;

public interface JBulletEntity {
    RigidBodyObject getRigidBodyObject();

    default boolean isValid() {
        return this.getRigidBodyObject() != null && !this.getRigidBodyObject().isNull() && this.getRigidBodyObject().isInWorld();
    }
}
