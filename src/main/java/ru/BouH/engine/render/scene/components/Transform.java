package ru.BouH.engine.render.scene.components;

import org.joml.*;

import java.lang.Math;

public class Transform {
    private final Matrix4d projectionMatrix;
    private final Matrix4d modelViewMatrix;
    private final Matrix4d viewMatrix;
    private final Matrix4d orthographicMatrix;
    private final Matrix4d orthoModelMatrix;

    public Transform() {
        this.projectionMatrix = new Matrix4d();
        this.modelViewMatrix = new Matrix4d();
        this.viewMatrix = new Matrix4d();
        this.orthographicMatrix = new Matrix4d();
        this.orthoModelMatrix = new Matrix4d();
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return this.projectionMatrix.identity().perspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4d getOrthographicMatrix(float left, float right, float bottom, float top) {
        return this.orthographicMatrix.identity().setOrtho2D(left, right, bottom, top);
    }

    public Matrix4d getOrthoModelMatrix(Model2DInfo model2DInfo, Matrix4d orthoMatrix) {
        Vector2d rotation = model2DInfo.getRotation();
        this.orthoModelMatrix.identity().translate(new Vector3d(model2DInfo.getPosition(), 0.0d)).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).scale(model2DInfo.getScale());
        Matrix4d viewCurr = new Matrix4d(orthoMatrix);
        return viewCurr.mul(this.orthoModelMatrix);
    }

    public Matrix4d getModelViewMatrix(Model3DInfo model3DInfo, Matrix4d viewMatrix) {
        Vector3d rotation = model3DInfo.getRotation();
        this.modelViewMatrix.identity().translate(model3DInfo.getPosition()).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).rotateZ(Math.toRadians(-rotation.z)).scale(model3DInfo.getScale());
        Matrix4d viewCurr = new Matrix4d(viewMatrix);
        return viewCurr.mul(this.modelViewMatrix);
    }

    public Matrix4d getViewMatrix(Camera camera) {
        Vector3d cameraPos = camera.getCamPosition();
        Vector3d cameraRot = camera.getCamRotation();
        return this.viewMatrix.identity().rotate(Math.toRadians(cameraRot.x), new Vector3d(1.0d, 0.0d, 0.0d)).rotate(Math.toRadians(cameraRot.y), new Vector3d(0.0d, 1.0d, 0.0d)).rotate(Math.toRadians(cameraRot.z), new Vector3d(0.0d, 0.0d, 1.0d)).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }
}
