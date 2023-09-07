package ru.BouH.engine.render.screen.timer;

import ru.BouH.engine.game.Game;

public class Timer {
    private final int physicsTicks;
    private double lastTime;
    private double renderPartial;

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
}
