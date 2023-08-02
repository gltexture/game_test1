package ru.BouH.engine.render.screen;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class ScreenTimer {
    private final SceneWorld sceneWorld;
    public Thread sceneThread;

    public ScreenTimer(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.init();
    }


    @SuppressWarnings("all")
    public void init() {
        this.sceneThread = new Thread(() -> {
            Game.getGame().getLogManager().debug("Starting scene timer!");
            while (!Game.getGame().shouldBeClosed) {
                this.getSceneWorld().onWorldRenderUpdate();
            }
        });
    }

    protected void startThread() {
        this.sceneThread.setName("scene");
        this.sceneThread.start();
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }
}
