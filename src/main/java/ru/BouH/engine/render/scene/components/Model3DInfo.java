package ru.BouH.engine.render.scene.components;

import org.joml.Vector3d;

public class Model3DInfo {
    private final Model3D model3D;
    private final Vector3d position;
    private final Vector3d rotation;
    private double scale;

    public Model3DInfo(Model3D model3D) {
        this.model3D = model3D;
        this.position = new Vector3d(0.0d, 0.0d, 0.0d);
        this.rotation = new Vector3d(0.0d, 0.0d, 0.0d);
        this.scale = 1.0f;
    }

    public void setPosition(Vector3d vector3d) {
        this.getPosition().set(vector3d);
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

    public Vector3d getPosition() {
        return this.position;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Model3D getModel3D() {
        return this.model3D;
    }
}
