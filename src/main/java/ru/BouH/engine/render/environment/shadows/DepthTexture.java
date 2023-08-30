package ru.BouH.engine.render.environment.shadows;

import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class DepthTexture {
    public static int MAP_DIMENSIONS = 4096;
    private final int[] id;

    public DepthTexture(int numCascades) {
        this.id = new int[numCascades];
        GL30.glGenTextures(this.id);

        for (int i = 0; i < numCascades; i++) {
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getId()[i]);
            GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32, DepthTexture.MAP_DIMENSIONS, DepthTexture.MAP_DIMENSIONS, 0, GL30.GL_DEPTH_COMPONENT, GL30.GL_FLOAT, (ByteBuffer) null);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_BORDER);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_BORDER);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_MODE, GL30.GL_COMPARE_R_TO_TEXTURE);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_FUNC, GL30.GL_LEQUAL);
            GL30.glTexParameterfv(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BORDER_COLOR, new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
        }
    }

    public void cleanTextures() {
        for (int i = 0; i < this.getId().length; i++) {
            GL30.glDeleteTextures(this.getId()[i]);
        }
    }

    public int[] getId() {
        return this.id;
    }
}
