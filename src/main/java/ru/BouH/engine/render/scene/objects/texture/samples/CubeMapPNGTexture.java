package ru.BouH.engine.render.scene.objects.texture.samples;

public class CubeMapPNGTexture {
    private final PNGTexture[] pngTextures;

    public CubeMapPNGTexture(String textureNamePng) {
        this.pngTextures = new PNGTexture[6];
        for (int i = 0; i < 6; i++) {
            pngTextures[i] = PNGTexture.createTexture("cubemaps/" + textureNamePng + "_" + (i + 1) + ".png");
        }
    }

    public PNGTexture[] getPngTextures() {
        return this.pngTextures;
    }
}
