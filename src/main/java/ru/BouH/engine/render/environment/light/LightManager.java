package ru.BouH.engine.render.environment.light;

import ru.BouH.engine.math.MathHelper;

public class LightManager {
    private double ambientLight;
    private final Sun sun;
    private final double minAmbientLight;

    public LightManager() {
        this.sun = new Sun(90);
        this.minAmbientLight = 0.1f;
    }

    public void setSunAngle(double angle) {
        this.sun.setSunPosition(angle);
    }

    public double getSunAngle() {
        return this.sun.getSunPosition();
    }

    public double calcAmbientLight() {
        return MathHelper.max(MathHelper.sin(this.getSunAngle()), this.minAmbientLight);
    }
}
