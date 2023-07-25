package ru.BouH.engine.render.scene.renderers.main_render.base;

public enum RenderGroup {
    GUI("gui"),
    WORLD("world"),
    SKYBOX("skybox");

    private final String path;

    RenderGroup(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
