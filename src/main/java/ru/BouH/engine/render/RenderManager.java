package ru.BouH.engine.render;

import org.joml.Matrix4d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.TransformationUtils;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class RenderManager {
    public static RenderManager instance = new RenderManager();
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.0f;
    public Matrix4d lightOrthoMatrix;

    private final TransformationUtils transformationUtils;

    public RenderManager() {
        this.transformationUtils = new TransformationUtils();
        this.lightOrthoMatrix = new Matrix4d().identity().ortho(-10.0d, 10.0d, -10.0d, 10.0d, -1.0f, 20.0f);
    }

    public TransformationUtils getTransform() {
        return this.transformationUtils;
    }

    public Matrix4d getProjectionMatrix() {
        return RenderManager.instance.getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR);
    }

    public Matrix4d getViewMatrix(ICamera iCamera) {
        return RenderManager.instance.getTransform().getViewMatrix(iCamera);
    }

    public Matrix4d getModelViewMatrix(Matrix4d matrix4d, Model3D model3D) {
        return RenderManager.instance.getTransform().getModelViewMatrix(model3D, matrix4d);
    }

    public Matrix4d getModelViewMatrix(ICamera iCamera, Model3D model3D) {
        return RenderManager.instance.getTransform().getModelViewMatrix(model3D, RenderManager.instance.getTransform().getViewMatrix(iCamera));
    }

    public Matrix4d getModelMatrix(Model3D model3D) {
        return RenderManager.instance.getTransform().getModelMatrix(model3D);
    }

    public Matrix4d getOrthographicModelMatrix(Model2D model2D) {
        Matrix4d orthographicMatrix = RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
        return RenderManager.instance.getTransform().getOrthoModelMatrix(model2D, orthographicMatrix);
    }
}
