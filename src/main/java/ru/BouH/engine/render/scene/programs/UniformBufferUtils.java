package ru.BouH.engine.render.scene.programs;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.IntPair;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.environment.light.LightType;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.nio.FloatBuffer;
import java.util.List;

public class UniformBufferUtils {
    public static final UBO_DATA UBO_SUN = new UBO_DATA("SunLight", new IntPair(0, 20));
    public static final UBO_DATA UBO_POINT_LIGHTS = new UBO_DATA(LightType.POINT_LIGHT.getBufferName(), new IntPair(1, LightManager.MAX_POINT_LIGHTS * 32));
    public static final UBO_DATA UBO_MISC = new UBO_DATA("Misc", new IntPair(3, 8));

    public static void updateLightBuffers(SceneRenderBase sceneRenderBase) {
        SceneWorld sceneWorld1 = sceneRenderBase.getSceneWorld();
        LightManager lightManager = sceneWorld1.getEnvironment().getLightManager();
        Matrix4d view = RenderManager.instance.getViewMatrix();
        Vector3f getAngle = lightManager.getNormalisedSunAngle(view);

        float sunLightX = getAngle.x;
        float sunLightY = getAngle.y;
        float sunLightZ = getAngle.z;

        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(5);
        value1Buffer.put(lightManager.calcAmbientLight());
        value1Buffer.put(lightManager.getSunBrightness());
        value1Buffer.put(sunLightX);
        value1Buffer.put(sunLightY);
        value1Buffer.put(sunLightZ);
        value1Buffer.flip();

        sceneRenderBase.performUniformBuffer(UniformBufferUtils.UBO_SUN, value1Buffer);
        sceneRenderBase.performUniformBuffer(UniformBufferUtils.UBO_MISC, new float[]{SceneWorld.elapsedRenderTicks});
        UniformBufferUtils.updatePointLightBuffer(sceneRenderBase, view, UniformBufferUtils.UBO_POINT_LIGHTS.getName());
        MemoryUtil.memFree(value1Buffer);
    }

    private static void updatePointLightBuffer(SceneRenderBase sceneRenderBase, Matrix4d view, String bufferName) {
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(7 * LightManager.MAX_POINT_LIGHTS);
        List<PointLight> pointLightList = sceneRenderBase.getSceneWorld().getEnvironment().getLightManager().getPointLightList();
        int activeLights = pointLightList.size();
        for (int i = 0; i < activeLights; i++) {
            PointLight pointLight = pointLightList.get(i);
            float[] f1 = LightManager.getNormalisedPointLightArray(view, pointLight);
            value1Buffer.put(f1[0]);
            value1Buffer.put(f1[1]);
            value1Buffer.put(f1[2]);
            value1Buffer.put(f1[3]);
            value1Buffer.put(f1[4]);
            value1Buffer.put(f1[5]);
            value1Buffer.put(f1[6]);
            value1Buffer.flip();
            sceneRenderBase.performUniformBuffer(bufferName, i * 32, value1Buffer);
        }
        MemoryUtil.memFree(value1Buffer);
    }

    public static class UBO_DATA {
        private final String name;
        private final IntPair intPair;

        public UBO_DATA(String s, IntPair i) {
            this.name = s;
            this.intPair = i;
        }

        public String getName() {
            return this.name;
        }

        public IntPair getIntPair() {
            return this.intPair;
        }
    }
}
