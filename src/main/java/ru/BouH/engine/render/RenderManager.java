package ru.BouH.engine.render;

import org.joml.Matrix4d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format2D;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class RenderManager {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 1000.0f;
    public static RenderManager instance = new RenderManager();
    private final TransformationUtils transformationUtils;

    public RenderManager() {
        this.transformationUtils = new TransformationUtils();
    }

    public TransformationUtils getTransform() {
        return this.transformationUtils;
    }

    public Matrix4d getProjectionMatrix() {
        return RenderManager.instance.getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR);
    }

    public Matrix4d getViewMatrix() {
        return RenderManager.instance.getTransform().getViewMatrix();
    }

    public Matrix4d getModelViewMatrix(Matrix4d matrix4d, Mesh<Format3D> mesh) {
        return RenderManager.instance.getTransform().getModelViewMatrix(mesh, matrix4d);
    }

    public Matrix4d getModelViewMatrix(Mesh<Format3D> mesh) {
        return RenderManager.instance.getTransform().getModelViewMatrix(mesh, RenderManager.instance.getTransform().getViewMatrix());
    }

    public Matrix4d getModelMatrix(Mesh<Format3D> mesh) {
        return RenderManager.instance.getTransform().getModelMatrix(mesh);
    }

    public void updateViewMatrix(ICamera camera) {
        this.transformationUtils.updateViewMatrix(camera);
    }

    public Matrix4d getScreenMatrix2D() {
        return RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
    }

    public Matrix4d getOrthographicModelMatrix(Mesh<Format2D> mesh) {
        Matrix4d orthographicMatrix = RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
        return RenderManager.instance.getTransform().getOrthoModelMatrix(mesh, orthographicMatrix);
    }
}
