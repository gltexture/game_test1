package ru.BouH.engine.render.environment.light;

public class Sun {
    private double sunPosition;

    public Sun(double angle) {
        this.sunPosition = angle;
    }

    public void setSunPosition(double sunPosition) {
        this.sunPosition = sunPosition;
    }

    public double getSunPosition() {
        return this.sunPosition;
    }
}
