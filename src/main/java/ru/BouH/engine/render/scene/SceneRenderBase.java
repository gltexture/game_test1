package ru.BouH.engine.render.scene;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public abstract class SceneRenderBase {
    private final int renderPriority;
    private final RenderGroup renderGroup;
    private final Scene.SceneRenderConveyor sceneRenderConveyor;

    protected SceneRenderBase(int renderPriority, Scene.SceneRenderConveyor sceneRenderConveyor, RenderGroup renderGroup) {
        this.renderPriority = renderPriority;
        this.renderGroup = renderGroup;
        this.sceneRenderConveyor = sceneRenderConveyor;
        Game.getGame().getLogManager().log("Scene \"" + renderGroup.getPath() + "\" init");
    }

    public ICamera getCamera() {
        return Game.getGame().getScreen().getCamera();
    }

    public abstract void onRender(double partialTicks);

    public abstract void onStartRender();

    public abstract void onStopRender();

    public int getRenderPriority() {
        return this.renderPriority;
    }

    public RenderGroup getRenderGroup() {
        return this.renderGroup;
    }

    public Scene.SceneRenderConveyor getSceneRenderConveyor() {
        return this.sceneRenderConveyor;
    }

    public SceneWorld getSceneWorld() {
        return this.getSceneRenderConveyor().getRenderWorld();
    }
}
