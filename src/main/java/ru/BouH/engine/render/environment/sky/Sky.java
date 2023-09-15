package ru.BouH.engine.render.environment.sky;

import ru.BouH.engine.render.scene.objects.texture.samples.CubeMapPNGTexture;

public class Sky {
    private final SkyBox skyBox;

    public Sky(CubeMapPNGTexture cubeMapPNGTexture) {
        this.skyBox = new SkyBox(cubeMapPNGTexture);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
