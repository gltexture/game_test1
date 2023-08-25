package ru.BouH.engine.render.environment.light;

public enum LightType {
    POINT_LIGHT("PointLights");

    private final String bufferName;
    LightType(String bufferName) {
        this.bufferName = bufferName;
    }

    public String getBufferName() {
        return this.bufferName;
    }
}
