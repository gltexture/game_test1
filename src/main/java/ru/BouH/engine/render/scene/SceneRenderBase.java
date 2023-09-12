package ru.BouH.engine.render.scene;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.IntPair;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.shadows.CascadeShadowBuilder;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.render.scene.programs.CubeMapSample;
import ru.BouH.engine.render.scene.programs.ShaderManager;
import ru.BouH.engine.render.scene.programs.UniformBufferProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.ICamera;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

public abstract class SceneRenderBase {
    private final int renderPriority;
    private final RenderGroup renderGroup;
    private final ShaderManager shaderManager;
    private final Scene.SceneRenderConveyor sceneRenderConveyor;
    private final SceneRenderUtils sceneRenderUtils;

    protected SceneRenderBase(int renderPriority, Scene.SceneRenderConveyor sceneRenderConveyor, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        Game.getGame().getLogManager().log("Scene \"" + renderGroup.getPath() + "\" init");
        this.renderGroup = renderGroup;
        this.shaderManager = new ShaderManager(renderGroup);
        this.sceneRenderConveyor = sceneRenderConveyor;
        this.sceneRenderUtils = new SceneRenderUtils();
    }

    public ICamera getCamera() {
        return Game.getGame().getScreen().getCamera();
    }

    public void bindProgram() {
        this.getShaderManager().bind();
    }

    public void unBindProgram() {
        this.getShaderManager().unBind();
    }

    public abstract void onRender(double partialTicks);

    public void onStartRender() {
        this.getShaderManager().startProgram();
    }

    public void onStopRender() {
        this.getShaderManager().destroyProgram();
    }

    public void performUniform(String uniform, Object o) {
        if (!this.getShaderManager().checkUniform(uniform)) {
            Game.getGame().getLogManager().bigWarn("Uniform \"" + uniform + "\" " + "is not registered in scene \"" + this.renderGroup.name() + "\"!");
            return;
        }
        this.getShaderManager().performUniform(uniform, o);
    }

    public void performUniformBuffer(UniformBufferUtils.UBO_DATA uniform, ByteBuffer buffer) {
        this.performUniformBuffer(uniform.getName(), 0, buffer);
    }

    public void performUniformBuffer(UniformBufferUtils.UBO_DATA uniform, FloatBuffer buffer) {
        this.performUniformBuffer(uniform.getName(), 0, buffer);
    }

    public void performUniformBuffer(UniformBufferUtils.UBO_DATA uniform, float[] data) {
        this.performUniformBuffer(uniform.getName(), 0, data);
    }

    public void performUniformBuffer(String uniform, ByteBuffer buffer) {
        this.performUniformBuffer(uniform, 0, buffer);
    }

    public void performUniformBuffer(String uniform, FloatBuffer buffer) {
        this.performUniformBuffer(uniform, 0, buffer);
    }

    public void performUniformBuffer(String uniform, float[] data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(String uniform, int offset, ByteBuffer buffer) {
        if (!this.getShaderManager().checkUniformBuffer(uniform)) {
            Game.getGame().getLogManager().bigWarn("Uniform-Buffer \"" + uniform + "\" " + "is not registered in scene \"" + this.renderGroup.name() + "\"!");
            return;
        }
        this.getShaderManager().performUniformBuffer(uniform, offset, buffer);
    }

    public void performUniformBuffer(String uniform, int offset, FloatBuffer buffer) {
        if (!this.getShaderManager().checkUniformBuffer(uniform)) {
            Game.getGame().getLogManager().bigWarn("Uniform-Buffer \"" + uniform + "\" " + "is not registered in scene \"" + this.renderGroup.name() + "\"!");
            return;
        }
        this.getShaderManager().performUniformBuffer(uniform, offset, buffer);
    }

    public void performUniformBuffer(String uniform, int offset, float[] data) {
        if (!this.getShaderManager().checkUniformBuffer(uniform)) {
            Game.getGame().getLogManager().bigWarn("Uniform-Buffer \"" + uniform + "\" " + "is not registered in scene \"" + this.renderGroup.name() + "\"!");
            return;
        }
        this.getShaderManager().performUniformBuffer(uniform, offset, data);
    }

    protected void addUniform(String u) {
        this.getShaderManager().addUniform(u);
    }

    protected void addUniformBuffer(UniformBufferUtils.UBO_DATA u) {
        this.getShaderManager().addUniformBuffer(u);
    }

    protected void addUniformBuffer(String u, IntPair intPair) {
        this.getShaderManager().addUniformBuffer(u, intPair);
    }

    public UniformBufferProgram getUniformBufferProgram(String name) {
        return this.getShaderManager().getUniformBufferProgram(name);
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public int getRenderPriority() {
        return this.renderPriority;
    }

    public RenderGroup getRenderGroup() {
        return this.renderGroup;
    }

    public SceneRenderUtils getUtils() {
        return this.sceneRenderUtils;
    }

    public Scene.SceneRenderConveyor getSceneRenderConveyor() {
        return this.sceneRenderConveyor;
    }

    public Scene.ShadowDispatcher getShadowDispatcher() {
        return this.getSceneRenderConveyor().getShadowDispatcher();
    }

    public SceneWorld getSceneWorld() {
        return this.getSceneRenderConveyor().getRenderWorld();
    }

    public class SceneRenderUtils {
        public SceneRenderUtils() {
        }

        public void disableMsaa() {
            GL30.glDisable(GL30.GL_MULTISAMPLE);
        }

        public void enableMsaa() {
            GL30.glEnable(GL30.GL_MULTISAMPLE);
        }

        public void disableLight() {
            SceneRenderBase.this.performUniform(UniformConstants.enable_light, 0);
        }

        public void enableLight() {
            SceneRenderBase.this.performUniform(UniformConstants.enable_light, 1);
        }

        public void performProjectionMatrix() {
            SceneRenderBase.this.performUniform(UniformConstants.projection_matrix, RenderManager.instance.getProjectionMatrix());
        }

        public void performModelViewMatrix3d(Model3D model3D) {
            SceneRenderBase.this.performUniform(UniformConstants.model_view_matrix, RenderManager.instance.getModelViewMatrix(model3D));
        }

        public void performModelViewMatrix3d(Matrix4d matrix4d) {
            SceneRenderBase.this.performUniform(UniformConstants.model_view_matrix, matrix4d);
        }

        public void performViewMatrix3d(Matrix4d matrix4d) {
            SceneRenderBase.this.performUniform(UniformConstants.view_matrix, matrix4d);
        }

        public void performModelMatrix3d(Model3D model3D) {
            SceneRenderBase.this.performUniform(UniformConstants.model_matrix, RenderManager.instance.getModelMatrix(model3D));
        }

        public void performLightModelProjection(int start, Model3D model3D) {
            this.performModelMatrix3d(model3D);
            Scene.ShadowDispatcher shadowDispatcher = SceneRenderBase.this.getShadowDispatcher();
            List<CascadeShadowBuilder> cascadeShadowBuilders = shadowDispatcher.getCascadeShadowBuilders();
            for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
                CascadeShadowBuilder cascadeShadowBuilder = cascadeShadowBuilders.get(i);
                SceneRenderBase.this.performUniform("shadowMap_" + i, start + i);
                SceneRenderBase.this.performUniform("CShadows[" + i + "]" + ".projection_view_matrix", cascadeShadowBuilder.getProjectionViewMatrix());
                SceneRenderBase.this.performUniform("CShadows[" + i + "]" + ".split_distance", (float) cascadeShadowBuilder.getSplitDistance());
            }
            shadowDispatcher.getDepthMap().bindTextures(GL30.GL_TEXTURE1);
        }

        public void performProperties(RenderData.RenderProperties renderProperties) {
            SceneRenderBase.this.performUniform(UniformConstants.texture_scaling, renderProperties.getTextureScaling());
            if (renderProperties.isLightExposed()) {
                this.enableLight();
            } else {
                this.disableLight();
            }
        }

        public void setNormalMap(WorldItemTexture worldItemTexture) {
            this.setNormalMap(1, worldItemTexture);
        }

        public void setNormalMap(int code, WorldItemTexture worldItemTexture) {
            if (worldItemTexture != null && worldItemTexture.hasNormalMap()) {
                PNGTexture pngTexture = worldItemTexture.getNormalMap();
                SceneRenderBase.this.performUniform(UniformConstants.normal_map, code);
                SceneRenderBase.this.performUniform(UniformConstants.use_normal_map, 1);
                pngTexture.performTexture(GL30.GL_TEXTURE0 + code);
            } else {
                SceneRenderBase.this.performUniform(UniformConstants.use_normal_map, 0);
            }
        }

        public void setCubeMapTexture(CubeMapSample cubeMapTexture) {
            this.setCubeMapTexture(GL30.GL_TEXTURE0, cubeMapTexture);
        }

        public void setCubeMapTexture(int code, CubeMapSample cubeMapTexture) {
            if (cubeMapTexture == null) {
                Game.getGame().getLogManager().warn("CubeMap is NULL!");
                return;
            }
            SceneRenderBase.this.performUniform(UniformConstants.cube_map_sampler, code);
            GL30.glActiveTexture(GL30.GL_TEXTURE0 + code);
            GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubeMapTexture.getTextureId());
        }

        public void setTexture(WorldItemTexture worldItemTexture) {
            this.setNormalMap(1, worldItemTexture);
            if (worldItemTexture.getSample() instanceof PictureSample) {
                this.setPngTexture(worldItemTexture, (PictureSample) worldItemTexture.getSample());
                return;
            }
            SceneRenderBase.this.performUniform(UniformConstants.use_texture, worldItemTexture.getRenderID());
            if (worldItemTexture.hasValueToPass()) {
                for (WorldItemTexture.PassUniValue passUniValue : worldItemTexture.getValues()) {
                    SceneRenderBase.this.performUniform(passUniValue.getName(), passUniValue.getO());
                }
            }
        }

        public void setPngTexture(WorldItemTexture worldItemTexture, PictureSample sample) {
            if (sample != null && sample.isValid()) {
                SceneRenderBase.this.performUniform(UniformConstants.use_texture, worldItemTexture.getRenderID());
                sample.performTexture(GL30.GL_TEXTURE0);
            } else {
                this.setTexture(WorldItemTexture.standardError);
            }
        }
    }
}
