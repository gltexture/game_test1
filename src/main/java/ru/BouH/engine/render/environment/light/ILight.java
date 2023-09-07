package ru.BouH.engine.render.environment.light;

import org.joml.Vector3d;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;

public interface ILight {
    void doAttachTo(PhysXObject physXObject);

    PhysXObject attachedTo();

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
