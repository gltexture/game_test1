package ru.BouH.engine.render.scene.components;

import org.joml.Vector3d;

public class Camera {
    private final Vector3d camPosition;
    private final Vector3d camRotation;

    public Camera() {
        this.camPosition = new Vector3d(0.0d, 0.0d, 0.0d);
        this.camRotation = new Vector3d(0.0d, 0.0d, 0.0d);
    }

    public Vector3d getCamPosition() {
        return this.camPosition;
    }

    public Vector3d getCamRotation() {
        return this.camRotation;
    }

    public void setCamPosition(double x, double y, double z) {
        this.camPosition.x = x;
        this.camPosition.y = y;
        this.camPosition.z = z;
    }

    public void moveCameraPos(double x, double y, double z) {
        if (z != 0) {
            this.camPosition.x += Math.sin(Math.toRadians(this.camRotation.y)) * -1.0f * z;
            this.camPosition.z += Math.cos(Math.toRadians(this.camRotation.y)) * z;
        }
        if (x != 0) {
            this.camPosition.x += Math.sin(Math.toRadians(this.camRotation.y - 90)) * -1.0f * x;
            this.camPosition.z += Math.cos(Math.toRadians(this.camRotation.y - 90)) * x;
        }
        if (y != 0) {
            this.camPosition.y += y;
        }
    }

    public void setCamRotation(double x, double y, double z) {
        this.camRotation.x = x;
        this.camRotation.y = y;
        this.camRotation.z = z;
    }

    public void moveCameraRot(double x, double y, double z) {
        this.camRotation.x += x;
        this.camRotation.y += y;
        this.camRotation.z += z;
    }
}
