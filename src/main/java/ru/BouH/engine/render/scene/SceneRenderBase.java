package ru.BouH.engine.render.scene;

import org.joml.Matrix4d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.programs.ShaderManager;
import ru.BouH.engine.render.scene.programs.UniformBufferProgram;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public abstract class SceneRenderBase {
    private final RenderGroup renderGroup;
    private final ShaderManager shaderManager;
    private final int renderPriority;
    private final SceneWorld sceneWorld;
    private final SceneRenderUtils sceneRenderUtils;

    protected SceneRenderBase(int renderPriority, SceneWorld sceneWorld, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        Game.getGame().getLogManager().log("Scene \"" + renderGroup.getPath() + "\" init");
        this.renderGroup = renderGroup;
        this.shaderManager = new ShaderManager(renderGroup);
        this.sceneWorld = sceneWorld;
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

    public void performUniformBuffer(String uniform, float[] data) {
        if (!this.getShaderManager().checkUniformBuffer(uniform)) {
            Game.getGame().getLogManager().bigWarn("Uniform-Buffer \"" + uniform + "\" " + "is not registered in scene \"" + this.renderGroup.name() + "\"!");
            return;
        }
        this.getShaderManager().performUniformBuffer(uniform, data);
    }

    protected void addUniform(String u) {
        this.getShaderManager().addUniform(u);
    }

    protected void addUniformBuffer(String u, int size) {
        this.getShaderManager().addUniformBuffer(u, size);
    }

    public UniformBufferProgram getUniformBufferProgram() {
        return this.getShaderManager().getUniformBufferProgram();
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

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public class SceneRenderUtils {
        public SceneRenderUtils() {
        }

        public void disableLight() {
            SceneRenderBase.this.performUniform("disable_light", 1);
        }

        public void enableLight() {
            SceneRenderBase.this.performUniform("disable_light", 0);
        }

        public void performProjectionMatrix() {
            SceneRenderBase.this.performUniform("projection_matrix", RenderManager.instance.getProjectionMatrix());
        }

        public void performModelViewMatrix3d(SceneWorld sceneWorld, Model3D model3D) {
            SceneRenderBase.this.performUniform("model_view_matrix", RenderManager.instance.getModelViewMatrix(Game.getGame().getScreen().getCamera(), model3D));
        }

        public void performModelViewMatrix3d(Matrix4d matrix4d) {
            SceneRenderBase.this.performUniform("model_view_matrix", matrix4d);
        }

        public void setTexture(WorldItemTexture worldItemTexture) {
            if (worldItemTexture.getSample() instanceof PictureSample) {
                this.setPngTexture(worldItemTexture, (PictureSample) worldItemTexture.getSample());
                return;
            }
            SceneRenderBase.this.performUniform("use_texture", worldItemTexture.getRenderID());
            if (worldItemTexture.hasValueToPass()) {
                for (WorldItemTexture.PassUniValue passUniValue : worldItemTexture.getValues()) {
                    SceneRenderBase.this.performUniform(passUniValue.getName(), passUniValue.getO());
                }
            }
        }

        public void setPngTexture(WorldItemTexture worldItemTexture, PictureSample sample) {
            if (sample != null && sample.isValid()) {
                SceneRenderBase.this.performUniform("use_texture", worldItemTexture.getRenderID());
                sample.performTexture();
            } else {
                this.setTexture(WorldItemTexture.standardError);
            }
        }
    }
}
