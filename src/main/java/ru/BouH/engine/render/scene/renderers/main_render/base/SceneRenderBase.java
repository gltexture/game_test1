package ru.BouH.engine.render.scene.renderers.main_render.base;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class SceneRenderBase {
    private final RenderGroup renderGroup;
    private final ShaderManager shaderManager;
    private final int renderPriority;
    private final SceneWorld sceneWorld;

    protected SceneRenderBase(int renderPriority, SceneWorld sceneWorld, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        Game.getGame().getLogManager().log("Scene \"" + renderGroup.getPath() + "\" init");
        this.renderGroup = renderGroup;
        this.shaderManager = new ShaderManager(renderGroup);
        this.sceneWorld = sceneWorld;
    }

    public void onStartRender() {
        this.getShaderManager().startProgram();
    }

    public void bindProgram() {
        this.getShaderManager().bind();
    }

    public void unBindProgram() {
        this.getShaderManager().unBind();
    }

    public abstract void onRender(double partialTicks);

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

    protected void addUniform(String u) {
        this.getShaderManager().addUniform(u);
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

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }
}
