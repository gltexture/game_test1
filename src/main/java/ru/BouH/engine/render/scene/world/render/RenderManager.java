package ru.BouH.engine.render.scene.world.render;

import ru.BouH.engine.render.scene.components.Transform;

public class RenderManager {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.0f;
    public static final float CAM_SPEED = 0.1f;
    public static final float CAM_SENS = 0.1f;
    public static final float MAX_POINT_LIGHTS = 64;
    public static final float MAX_SPOT_LIGHTS = 64;
    private final Transform transform;

    public RenderManager() {
        this.transform = new Transform();
    }

    public Transform getTransform() {
        return this.transform;
    }
}
