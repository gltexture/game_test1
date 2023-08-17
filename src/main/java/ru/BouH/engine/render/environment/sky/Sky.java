package ru.BouH.engine.render.environment.sky;

import ru.BouH.engine.render.scene.world.SceneWorld;

public class Sky {
    private final SkyBox skyBox;

    public Sky(SceneWorld sceneWorld, String texturePath) {
        this.skyBox = new SkyBox(texturePath);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
