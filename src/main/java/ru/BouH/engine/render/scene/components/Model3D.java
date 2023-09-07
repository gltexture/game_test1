package ru.BouH.engine.render.scene.components;

import org.joml.Vector3d;

public class Model3D extends ObjectModel {
    private final Vector3d position;
    private final Vector3d rotation;
    private double scale;

    public Model3D(MeshModel meshModel) {
        super(meshModel);
        this.position = new Vector3d(0.0d);
        this.rotation = new Vector3d(0.0d);
        this.scale = 1.0f;
    }

    public void setPosition(double x, double y, double z) {
        this.getPosition().x = x;
        this.getPosition().y = y;
        this.getPosition().z = z;
    }

    public void setRotation(double x, double y, double z) {
        this.getRotation().x = x;
        this.getRotation().y = y;
        this.getRotation().z = z;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3d v) {
        this.getRotation().x = v.x;
        this.getRotation().y = v.y;
        this.getRotation().z = v.z;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(Vector3d vector3d) {
        this.getPosition().set(vector3d);
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
