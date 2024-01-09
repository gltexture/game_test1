package ru.BouH.engine.render;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format2D;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class TransformationUtils {
    private final Matrix4d viewMatrix;

    public TransformationUtils() {
        this.viewMatrix = new Matrix4d();
    }

    public final Matrix4d getModelMatrix(Mesh<Format3D> mesh) {
        Matrix4d m1 = new Matrix4d();
        Quaterniond quaterniond = new Quaterniond();
        quaterniond.rotateXYZ(Math.toRadians(mesh.getFormat().getRotation().x), Math.toRadians(mesh.getFormat().getRotation().y), Math.toRadians(mesh.getFormat().getRotation().z));
        return m1.identity().translationRotateScale(mesh.getFormat().getPosition(), quaterniond, mesh.getFormat().getScale());
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().perspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4d getOrthographicMatrix(float left, float right, float bottom, float top) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho2D(left, right, bottom, top);
    }

    public Matrix4d getOrthoModelMatrix(Mesh<Format2D> mesh, Matrix4d orthoMatrix) {
        Vector2d rotation = mesh.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(new Vector3d(mesh.getFormat().getPosition(), 0.0d)).rotateX(Math.toRadians(-rotation.x)).rotateY(Math.toRadians(-rotation.y)).scaleXY(mesh.getFormat().getScale().x, mesh.getFormat().getScale().y);
        Matrix4d viewCurr = new Matrix4d(orthoMatrix);
        return viewCurr.mul(m1);
    }

    public Matrix4d getModelViewMatrix(Mesh<Format3D> mesh, Matrix4d viewMatrix) {
        Vector3d rotation = mesh.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(mesh.getFormat().getPosition()).rotateXYZ(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(-rotation.z)).scale(mesh.getFormat().getScale());
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
