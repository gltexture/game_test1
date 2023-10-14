package ru.BouH.engine.render.scene.world.camera;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.controller.IController;

public class Camera implements ICamera {
    private final Vector3d camPosition;
    private final Vector3d camRotation;
    private IController controller;

    public Camera(IController controller, Vector3d pos, Vector3d rot) {
        this.camPosition = new Vector3d(pos);
        this.camRotation = new Vector3d(rot);
        this.controller = controller;
    }

    public Camera(IController controller) {
        this(controller, new Vector3d(0.0d), new Vector3d(0.0d));
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    public IController getController() {
        return this.controller;
    }

    protected void setCameraPos(Vector3d vector3d) {
        this.camPosition.set(vector3d);
    }

    protected void setCameraRot(Vector3d vector3d) {
        this.camRotation.set(vector3d);
    }

    protected void move2dCameraRot(Vector2d xy) {
        this.addCameraRot(new Vector3d(xy, 0));
    }

    public void addCameraPos(Vector3d vector3d) {
        this.setCameraPos(this.getCamPosition().add(vector3d));
    }

    public void addCameraRot(Vector3d vector3d) {
        this.setCameraRot(this.getCamRotation().add(vector3d));
    }

    public Vector3d getCamPosition() {
        return new Vector3d(this.camPosition);
    }

    public Vector3d getCamRotation() {
        return new Vector3d(this.camRotation);
    }

    @Override
    public void updateCameraPosition(double partialTicks) {

    }

    @Override
    public void updateCameraRotation(double partialTicks) {

    }
}
