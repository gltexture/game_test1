package ru.BouH.engine.render.scene.components;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class TransformationUtils {
    public final Matrix4d getModelMatrix(Model3D model3D) {
        Matrix4d m1 = new Matrix4d();
        Quaterniond quaterniond = new Quaterniond();
        quaterniond.rotateXYZ(Math.toRadians(model3D.getRotation().x), Math.toRadians(model3D.getRotation().y), Math.toRadians(model3D.getRotation().z));
        return m1.identity().translationRotateScale(model3D.getPosition(), quaterniond, model3D.getScale());
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().perspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4d getOrthographicMatrix(float left, float right, float bottom, float top) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho2D(left, right, bottom, top);
    }

    public Matrix4d getOrthoModelMatrix(Model2D model2D, Matrix4d orthoMatrix) {
        Vector2d rotation = model2D.getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(new Vector3d(model2D.getPosition(), 0.0d)).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).scale(model2D.getScale());
        Matrix4d viewCurr = new Matrix4d(orthoMatrix);
        return viewCurr.mul(m1);
    }

    public Matrix4d getModelViewMatrix(Model3D model3D, Matrix4d viewMatrix) {
        Vector3d rotation = model3D.getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(model3D.getPosition()).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).rotateZ(Math.toRadians(-rotation.z)).scale(model3D.getScale());
        Matrix4d viewCurr = new Matrix4d(viewMatrix);
        return viewCurr.mul(m1);
    }

    public Matrix4d getViewMatrix(ICamera camera) {
        Vector3d cameraPos = camera.getCamPosition();
        Vector3d cameraRot = camera.getCamRotation();
        Matrix4d m1 = new Matrix4d();
        return m1.identity().rotate(Math.toRadians(cameraRot.x), new Vector3d(1.0d, 0.0d, 0.0d)).rotate(Math.toRadians(cameraRot.y), new Vector3d(0.0d, 1.0d, 0.0d)).rotate(Math.toRadians(cameraRot.z), new Vector3d(0.0d, 0.0d, 1.0d)).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }
}
