package ru.BouH.engine.render.environment.light;

import org.joml.Vector3f;

public class Sun {
    private Vector3f sunPosition;

    public Sun(Vector3f sunPosition) {
        this.sunPosition = sunPosition;
    }

    public void setSunPosition(Vector3f sunPosition) {
        this.sunPosition = sunPosition;
    }

    public Vector3f getSunPosition() {
        return this.sunPosition;
    }
}
