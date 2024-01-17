package ru.BouH.engine.render.environment.sky;


import ru.BouH.engine.render.scene.programs.CubeMapProgram;

public class Sky {
    private final SkyBox skyBox;

    public Sky(CubeMapProgram.CubeMapTextureArray cubeMapPNGTexture) {
        this.skyBox = new SkyBox(cubeMapPNGTexture);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
