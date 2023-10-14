package ru.BouH.engine.render.screen.timer;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.utils.Syncer;

public class Timer {
    private final Syncer SYNC_TIME = new Syncer();
    private double lastTime;
    private double deltaTime;

    public Timer() {
        this.lastTime = Game.glfwTime();
    }

    public void reset() {
        this.lastTime = Game.glfwTime();
    }

    public static void syncUp() {
        Game.getGame().getScreen().getTimer().getSyncUpdate().syncUp();
    }

    public static void syncDown() {
        Game.getGame().getScreen().getTimer().getSyncUpdate().syncDown();
    }

    public void updateTimer() {
        double currentTime = Game.glfwTime();
        this.deltaTime = currentTime - this.lastTime;
        this.lastTime = currentTime;
    }

    public double getDeltaTime() {
        return this.deltaTime;
    }

    public boolean markSyncUpdate() {
        return this.getSyncUpdate().shouldSync();
    }

    public Syncer getSyncUpdate() {
        return this.SYNC_TIME;
    }
}
