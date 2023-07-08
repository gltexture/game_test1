package ru.BouH.engine.proxy.lights;

public enum LightType {
    POINT_LIGHT("point_light"),
    SPOT_LIGHT("spot_light"),
    DIRECTIONAL_LIGHT("directional_light");

    private final String id;
    LightType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
