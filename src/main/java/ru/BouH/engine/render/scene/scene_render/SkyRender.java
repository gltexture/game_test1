package ru.BouH.engine.render.scene.scene_render;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.sky.SkyBox;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;

public class SkyRender extends SceneRenderBase {
    private final ShaderManager skyShaders;

    public SkyRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(0, sceneRenderConveyor, new RenderGroup("SKYBOX", true));
        this.skyShaders = ResourceManager.shaderAssets.skybox;
    }

    public void onRender(double partialTicks) {
        SkyBox skyBox = this.getSceneWorld().getEnvironment().getSky().getSkyBox();
        if (skyBox != null) {
            this.skyShaders.bind();
            GL30.glDisable(GL30.GL_CULL_FACE);
            GL30.glDepthFunc(GL30.GL_LEQUAL);
            this.skyShaders.getUtils().performProjectionMatrix();
            Matrix4d matrix4d = RenderManager.instance.getModelViewMatrix(skyBox.getModel3DInfo());
            matrix4d.m30(0);
            matrix4d.m31(0);
            matrix4d.m32(0);
            this.skyShaders.getUtils().performModelViewMatrix3d(matrix4d);
            this.skyShaders.getUtils().setCubeMapTexture(skyBox.getCubeMap());
            Scene.renderModel(skyBox.getModel3DInfo());
            this.skyShaders.unBind();
            GL30.glDepthFunc(GL30.GL_LESS);
            GL30.glEnable(GL30.GL_CULL_FACE);
        }
    }

    @Override
    public void onStartRender() {
    }

    @Override
    public void onStopRender() {
    }
}
