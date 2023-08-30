package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.screen.Screen;

import java.nio.ByteBuffer;

public class FrameBufferObjectProgram {
    private boolean msaa;
    private int frameBufferId;
    private int renderBufferId;
    private int[] textureBufferId;

    public FrameBufferObjectProgram() {
    }

    public void createFBO(Vector2i xy, int target, boolean msaa) {
        this.createFBO_MRT(xy, new int[] {GL30.GL_COLOR_ATTACHMENT0}, target, msaa);
    }

    public void createFBO_MRT(Vector2i xy, int[] attachments, int target, boolean msaa) {
        this.frameBufferId = GL30.glGenFramebuffers();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        int atL = attachments.length;
        this.textureBufferId = new int[atL];
        GL30.glGenTextures(this.textureBufferId);

        for (int i = 0; i < atL; i++) {
            this.bindTextureFBO(i);
            GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, target, xy.x, xy.y, 0, GL30.GL_RGBA, GL30.GL_FLOAT, (ByteBuffer) null);
            GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachments[i], GL30.GL_TEXTURE_2D, this.getTextureBufferId(i), 0);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        }
        GL30.glDrawBuffers(attachments);

        this.bindRenderDFBO();
        if (msaa) {
            GL43.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, this.msaaSamples(), GL30.GL_DEPTH24_STENCIL8, xy.x, xy.y);
        } else {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, xy.x, xy.y);
        }
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            Game.getGame().getLogManager().error("Failed to create framebuffer!");
        }

        this.unBindFBO();
        this.unBindRenderDFBO();
        this.unBindTextureFBO();
    }

    public int msaaSamples() {
        return Screen.MSAA_SAMPLES;
    }

    public int getTextureBufferId() {
        return this.getTextureBufferId(0);
    }

    public int getTextureBufferId(int i) {
        return this.textureBufferId[i];
    }

    public int getRenderBufferId() {
        return this.renderBufferId;
    }

    public int getFrameBufferId() {
        return this.frameBufferId;
    }

    public void bindTextureFBO(int i) {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.textureBufferId[i]);
    }

    public void bindTextureFBO() {
        this.bindTextureFBO(0);
    }

    public void unBindTextureFBO() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    public void bindRenderDFBO() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.renderBufferId);
    }

    public void unBindRenderDFBO() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
    }

    public void bindFBO() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);
    }

    public void unBindFBO() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void clearFBO() {
        this.unBindFBO();
        this.unBindTextureFBO();
        this.unBindRenderDFBO();
        if (this.textureBufferId != null) {
            GL30.glDeleteTextures(this.textureBufferId);
        }
        GL30.glDeleteRenderbuffers(this.renderBufferId);
        GL30.glDeleteFramebuffers(this.frameBufferId);
    }
}
