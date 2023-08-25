package ru.BouH.engine.render.environment.light;

import org.joml.Vector3d;
import ru.BouH.engine.physx.world.object.WorldItem;

public interface ILight {
    void doAttachTo(WorldItem worldItem);
    WorldItem attachedTo();
    Vector3d getOffset();
    void setOffset(Vector3d vector3d);
    LightType lightType();
    boolean isActive();
    Vector3d getLightColor();
    Vector3d getLightPos();
    default boolean isAttached() {
        return this.attachedTo() != null;
    }
}
