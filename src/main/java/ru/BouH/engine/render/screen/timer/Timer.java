package ru.BouH.engine.render.screen.timer;

import ru.BouH.engine.game.Game;

public class Timer {
    private final int physicsTicks;
    private long lastTime;
    private double renderPartial;

    public Timer(int physicsTicks) {
        this.physicsTicks = physicsTicks;
        this.lastTime = Game.systemTime();
    }

    public void update() {
        long currentTime = Game.systemTime();
        long elapsed = currentTime - this.lastTime;
        this.lastTime = currentTime;
        float deltaTime = elapsed / 1000000000f;
        this.renderPartial = deltaTime * this.physicsTicks;
    }

    public double getRenderPartial() {
        return this.renderPartial;
    }
}
