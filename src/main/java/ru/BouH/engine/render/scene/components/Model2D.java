package ru.BouH.engine.render.scene.components;

import org.joml.Vector2d;

public class Model2D extends ObjectModel {
    private final Vector2d position;
    private final Vector2d rotation;
    private double scale;

    public Model2D(MeshModel meshModel) {
        super(meshModel);
        this.position = new Vector2d(0.0d);
        this.rotation = new Vector2d(0.0d);
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

    public Vector2d getRotation() {
        return this.rotation;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
