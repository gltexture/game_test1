package ru.BouH.engine.render.environment.sky;

public class Sky {
    private final SkyBox skyBox;

    public Sky(String texturePath) {
        this.skyBox = new SkyBox(texturePath);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
