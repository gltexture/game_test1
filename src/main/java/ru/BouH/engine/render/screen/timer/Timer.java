package ru.BouH.engine.render.screen.timer;

import ru.BouH.engine.game.Game;

import java.util.concurrent.atomic.AtomicBoolean;

public class Timer {
    private final int physicsTicks;
    private double lastTime;
    private double renderPartial;
    private final AtomicBoolean syncUpdate = new AtomicBoolean();

    public Timer(int physicsTicks) {
        this.physicsTicks = physicsTicks;
        this.lastTime = Game.systemTime();
    }

    public void updateTimer() {
        double currentTime = Game.systemTime();
        double elapsed = currentTime - this.lastTime;
        this.lastTime = currentTime;
        this.renderPartial = elapsed * this.physicsTicks;
    }

    public double getRenderPartial() {
        return this.renderPartial;
    }

    public boolean markSyncUpdate() {
        return this.getSyncUpdate().get();
    }

    public AtomicBoolean getSyncUpdate() {
        return this.syncUpdate;
    }

    public static void syncUp() {
        Game.getGame().getScreen().getTimer().getSyncUpdate().set(true);
    }

    public static void syncDown() {
        Game.getGame().getScreen().getTimer().getSyncUpdate().set(false);
    }
}
