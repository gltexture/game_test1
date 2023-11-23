package ru.BouH.engine.render.environment.light;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

public interface ILight {
    void doAttachTo(PhysicsObject physicsObject);

    PhysicsObject attachedTo();

    Vector3d getOffset();

    void setOffset(Vector3d vector3d);

    LightType lightType();

    void deactivate();

    boolean isActive();

    Vector3d getLightColor();

    Vector3d getLightPos();

    default boolean isAttached() {
        return this.attachedTo() != null;
    }
}
