package ru.BouH.engine.render.scene.components;

import org.joml.Vector4d;
import org.joml.Vector4f;

public class AmbientMaterial {
    private final Vector4d ambientColor;
    private final float reflectance;
    private final Vector4d specularColor;

    public AmbientMaterial(Vector4d ambientColor, Vector4d specularColor, float reflectance) {
        this.ambientColor = ambientColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
    }

    public Vector4d getAmbientColor() {
        return this.ambientColor;
    }

    public float getReflectance() {
        return this.reflectance;
    }

    public Vector4d getSpecularColor() {
        return this.specularColor;
    }
}
