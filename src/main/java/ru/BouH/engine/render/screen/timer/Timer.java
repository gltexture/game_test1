package ru.BouH.engine.render.screen.timer;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.utils.Syncer;

public class Timer {
    private final Syncer SYNC_TIME = new Syncer();
    private double lastTime = -1L;

    public double getDeltaTime() {
        double currentTime = Game.glfwTime();
        double deltaTime = (currentTime - this.lastTime);
        this.lastTime = currentTime;
        if (this.lastTime < 0) {
            return 0.0d;
        }
        return deltaTime;
    }

    public Syncer getSyncUpdate() {
        return this.SYNC_TIME;
    }
}
