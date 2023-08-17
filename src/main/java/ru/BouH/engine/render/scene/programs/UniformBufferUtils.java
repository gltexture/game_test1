package ru.BouH.engine.render.scene.programs;

import org.joml.Vector3f;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class UniformBufferUtils {
    public static void updateLightBuffers(SceneRenderBase sceneRenderBase) {
        SceneWorld sceneWorld1 = sceneRenderBase.getSceneWorld();
        LightManager lightManager = sceneWorld1.getEnvironment().getLightManager();
        Vector3f getAngle = lightManager.getNormalisedSunAngle(RenderManager.instance.getViewMatrix(Game.getGame().getScreen().getCamera()));
        float sunLightX = getAngle.x;
        float sunLightY = getAngle.y;
        float sunLightZ = getAngle.z;
        sceneRenderBase.performUniformBuffer("Lights", new float[] {lightManager.calcAmbientLight(), sunLightX, sunLightY, sunLightZ});
    }
}
