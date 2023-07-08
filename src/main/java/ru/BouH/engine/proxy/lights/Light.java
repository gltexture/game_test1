package ru.BouH.engine.proxy.lights;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import ru.BouH.engine.render.scene.world.RenderWorld;

public abstract class Light {
    private Vector3d colour;
    private Vector3d position;
    private double intensity;
    private boolean isEnabled;
    private final RenderWorld renderWorld;
    private final LightType lightType;

    public Light(RenderWorld renderWorld, LightType lightType) {
        this.renderWorld = renderWorld;
        this.lightType = lightType;
        this.isEnabled = false;
        this.position = new Vector3d(0.0f, 0.0f, 0.0f);
        this.setColour(new Vector3d(1.0f, 1.0f, 1.0f));
        this.setIntensity(1.0f);
    }

    public Vector3d getPosition() {
        Vector3d position = new Vector3d(this.position);
        Vector4d aux = new Vector4d(position, 1.0f);
        Matrix4d matrix4d = this.renderWorld.getRenderManager().getTransform().getViewMatrix(this.renderWorld.getCamera());
        aux.mul(matrix4d);
        return position.set(aux.x, aux.y, aux.z);
    }

    public void setPosition(double x, double y, double z) {
        this.position.set(x ,y, z);
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public RenderWorld getRenderWorld() {
        return this.renderWorld;
    }

    public LightType getLightType() {
        return this.lightType;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getIntensity() {
        return this.isEnabled() ? this.intensity : 0.0f;
    }

    public Vector3d getColour() {
        return this.colour;
    }

    public void setColour(Vector3d colour) {
        this.colour = colour;
    }

    public void doEnable() {
        isEnabled = true;
    }

    public void doDisable() {
        isEnabled = false;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }
}
