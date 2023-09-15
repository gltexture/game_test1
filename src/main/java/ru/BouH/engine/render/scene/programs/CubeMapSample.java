package ru.BouH.engine.render.scene.programs;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.objects.texture.samples.CubeMapPNGTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;

public class CubeMapSample {
    private int textureId;

    public CubeMapSample(String textureNamePng) {
        this.generateTexture(textureNamePng);
    }

    public CubeMapSample(CubeMapPNGTexture cubeMapPNGTexture) {
        this.generateTexture(cubeMapPNGTexture);
    }

    private void generateTexture(CubeMapPNGTexture cubeMapPNGTexture) {
        this.textureId = GL30.glGenTextures();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < 6; i++) {
            PNGTexture pngTexture = cubeMapPNGTexture.getPngTextures()[i];
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGB16, pngTexture.getWidth(), pngTexture.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pngTexture.getPNGInBuffer());
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    private void generateTexture(String textureNamePng) {
        this.textureId = GL30.glGenTextures();

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, this.textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE);

        for (int i = 0; i < 6; i++) {
            PNGTexture pngTexture = PNGTexture.createTexture("cubemaps/" + textureNamePng + "_" + (i + 1) + ".png");
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGB16, pngTexture.getWidth(), pngTexture.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pngTexture.getPNGInBuffer());
        }

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void cleanCubeMap() {
        GL30.glDeleteTextures(this.getTextureId());
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, 0);
    }
}
