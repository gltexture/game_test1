package ru.BouH.engine.render.scene.components;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Model2DInfo {
    private final Model2D model2D;
    private final Vector2d position;
    private final Vector2d rotation;
    private double scale;

    public Model2DInfo(Model2D model2D) {
        this.model2D = model2D;
        this.position = new Vector2d(0.0d, 0.0d);
        this.rotation = new Vector2d(0.0d, 0.0d);
        this.scale = 1.0f;
    }

    public void setPosition(double x, double y) {
        this.getPosition().x = x;
        this.getPosition().y = y;
    }

    public void setRotation(double x, double y) {
        this.getRotation().x = x;
        this.getRotation().y = y;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Vector2d getRotation() {
        return this.rotation;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public double getScale() {
        return this.scale;
    }

    public Model2D getMesh() {
        return this.model2D;
    }
}
