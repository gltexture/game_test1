package ru.BouH.engine.proxy.lights.env;

import org.joml.*;
import ru.BouH.engine.proxy.lights.Attenuation;
import ru.BouH.engine.proxy.lights.Light;
import ru.BouH.engine.proxy.lights.LightType;
import ru.BouH.engine.render.scene.world.RenderWorld;

import java.lang.Math;

public class SpotLight extends Light {
    private Vector3d coneDirection;
    private final Attenuation attenuation;
    private float cutOff;
    private float cutOffAngle;

    public SpotLight(RenderWorld renderWorld, Vector3d coneDirection, float cutOffAngle) {
        super(renderWorld, LightType.SPOT_LIGHT);
        this.coneDirection = coneDirection;
        this.cutOffAngle = cutOffAngle;
        this.attenuation = new Attenuation(0.0f, 0.0f, 1.0f);
        this.setCutOffAngle(this.cutOffAngle);
    }

    public Attenuation getAttenuation() {
        return this.attenuation;
    }

    public Vector3d getConeDirection() {
        Vector3d dir = new Vector3d(this.coneDirection);
        Vector4d aux = new Vector4d(dir, 0.0f);
        Matrix4d matrix4d = this.getRenderWorld().getRenderManager().getTransform().getViewMatrix(this.getRenderWorld().getCamera());
        aux.mul(matrix4d);
        return this.coneDirection.set(aux.x, aux.y, aux.z);
    }

    public float getCutOff() {
        return this.cutOff;
    }

    public float getCutOffAngle() {
        return this.cutOffAngle;
    }

    public void setConeDirection(double x, double y, double z) {
        this.coneDirection.set(x, y, z);
    }

    public void setConeDirection(Vector3d coneDirection) {
        this.coneDirection = coneDirection;
    }

    public void setCutOffAngle(float cutOffAngle) {
        this.cutOffAngle = cutOffAngle;
        this.cutOff = (float) Math.cos(Math.toRadians(cutOffAngle));
    }
}
