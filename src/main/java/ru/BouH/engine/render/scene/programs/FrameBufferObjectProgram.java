package ru.BouH.engine.render.scene.programs;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.screen.window.Window;

import java.nio.ByteBuffer;

public class FrameBufferObjectProgram {
    private int frameBufferId;
    private int renderBufferId;
    private int textureBufferId;

    public FrameBufferObjectProgram(Window window) {
        this.createRenderBuffer(window);
    }

    public void createRenderBuffer(Window window) {
        this.createRenderBuffer(new Vector2i(window.getWidth(), window.getHeight()));
    }

    public void createRenderBuffer(Vector2d xy) {
        this.createRenderBuffer(new Vector2i((int) xy.x, (int) xy.y));
    }

    public void createRenderBuffer(Vector2i xy) {
        this.frameBufferId = GL30.glGenFramebuffers();
        this.textureBufferId = GL30.glGenTextures();
        this.renderBufferId = GL30.glGenRenderbuffers();
        this.bindFBO();

        this.bindTextureFBO();
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, xy.x, xy.y, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, this.textureBufferId, 0);

        this.bindRenderDFBO();
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT, xy.x, xy.y);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, this.renderBufferId);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            Game.getGame().getLogManager().error("Failed to create framebuffer!");
        }

        this.unBindFBO();
        this.unBindRenderDFBO();
        this.unBindTextureFBO();
    }

    public void bindTextureFBO() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.textureBufferId);
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
        GL30.glDeleteTextures(this.textureBufferId);
        GL30.glDeleteRenderbuffers(this.renderBufferId);
        GL30.glDeleteFramebuffers(this.frameBufferId);
    }
}
