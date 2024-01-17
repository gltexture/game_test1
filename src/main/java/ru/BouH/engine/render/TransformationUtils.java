package ru.BouH.engine.render;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class TransformationUtils {
    private final Matrix4d viewMatrix;

    public TransformationUtils() {
        this.viewMatrix = new Matrix4d();
    }

    public final Matrix4d getModelMatrix(Model<Format3D> model) {
        Matrix4d m1 = new Matrix4d();
        Quaterniond quaterniond = new Quaterniond();
        quaterniond.rotateXYZ(Math.toRadians(model.getFormat().getRotation().x), Math.toRadians(model.getFormat().getRotation().y), Math.toRadians(model.getFormat().getRotation().z));
        return m1.identity().translationRotateScale(model.getFormat().getPosition(), quaterniond, model.getFormat().getScale());
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().perspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4d getOrthographicMatrix(float left, float right, float bottom, float top) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho2D(left, right, bottom, top);
    }

    public Matrix4d getOrthoModelMatrix(Model<Format2D> model, Matrix4d orthoMatrix) {
        Vector2d rotation = model.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(new Vector3d(model.getFormat().getPosition(), 0.0d)).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).scaleXY(model.getFormat().getScale().x, model.getFormat().getScale().y);
        Matrix4d viewCurr = new Matrix4d(orthoMatrix);
        return viewCurr.mul(m1);
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model, Matrix4d viewMatrix) {
        Vector3d rotation = model.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(model.getFormat().getPosition()).rotateXYZ(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(-rotation.z)).scale(model.getFormat().getScale());
        Matrix4d viewCurr = new Matrix4d(viewMatrix);
        return viewCurr.mul(m1);
    }

    public void updateViewMatrix(ICamera camera) {
        Vector3d cameraPos = camera.getCamPosition();
        Vector3d cameraRot = camera.getCamRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().rotateXYZ(Math.toRadians(cameraRot.x), Math.toRadians(cameraRot.y), Math.toRadians(cameraRot.z)).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        this.viewMatrix.set(m1);
    }

    public Matrix4d getViewMatrix() {
        return this.viewMatrix;
    }
}
