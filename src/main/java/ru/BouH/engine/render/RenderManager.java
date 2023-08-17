package ru.BouH.engine.render;

import org.joml.Matrix4d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.TransformMath;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class RenderManager {
    public static RenderManager instance = new RenderManager();
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.0f;

    private final TransformMath transformMath;

    public RenderManager() {
        this.transformMath = new TransformMath();
    }

    public TransformMath getTransform() {
        return this.transformMath;
    }

    public Matrix4d getProjectionMatrix() {
        return RenderManager.instance.getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR);
    }

    public Matrix4d getViewMatrix(ICamera iCamera) {
        return RenderManager.instance.getTransform().getViewMatrix(iCamera);
    }

    public Matrix4d getModelViewMatrix(ICamera iCamera, Model3D model3D) {
        return RenderManager.instance.getTransform().getModelViewMatrix(model3D, RenderManager.instance.getTransform().getViewMatrix(iCamera));
    }

    public Matrix4d getOrthoModelMatrix(Model2D model2D) {
        Matrix4d orthographicMatrix = RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
        return RenderManager.instance.getTransform().getOrthoModelMatrix(model2D, orthographicMatrix);
    }
}
