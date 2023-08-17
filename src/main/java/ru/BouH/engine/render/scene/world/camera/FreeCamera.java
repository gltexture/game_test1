package ru.BouH.engine.render.scene.world.camera;

import org.joml.Vector3d;

public class FreeCamera extends Camera {
    public FreeCamera(Vector3d pos, Vector3d rot) {
        super(pos, rot);
    }

    public void setCameraPos(Vector3d vector3d) {
        super.setCameraPos(vector3d);
    }

    public void setCameraRot(Vector3d vector3d) {
        super.setCameraRot(vector3d);
    }

    public void addCameraPos(Vector3d vector3d) {
        super.setCameraPos(this.getCamPosition().add(vector3d));
    }

    public void addCameraRot(Vector3d vector3d) {
        super.setCameraRot(this.getCamRotation().add(vector3d));
    }
}
