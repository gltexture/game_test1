package ru.BouH.engine.render.scene.renderers;

import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class UniformBufferUtils {
    public static void updateLightBuffers(SceneRenderBase sceneRenderBase) {
        SceneWorld sceneWorld1 = sceneRenderBase.getSceneWorld();
        sceneRenderBase.performUniformBuffer("Lights", new float[] {(float) sceneWorld1.getEnvironment().getSky().getSunPosition(), 1});
    }
}
