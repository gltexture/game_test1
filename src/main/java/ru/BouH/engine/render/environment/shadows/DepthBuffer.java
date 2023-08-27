package ru.BouH.engine.render.environment.shadows;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;

public class DepthBuffer {
    private final DepthTexture depthTexture;
    private final int depthFbo;

    public DepthBuffer() {
        this.depthFbo = GL30.glGenFramebuffers();
        this.depthTexture = new DepthTexture(CascadeShadowBuilder.SHADOW_CASCADE_MAX);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.depthFbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, this.getDepthTexture().getId()[0], 0);
        GL30.glDrawBuffer(GL30.GL_NONE);
        GL30.glReadBuffer(GL30.GL_NONE);
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            Game.getGame().getLogManager().error("Failed to create framebuffer!");
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void bindTextures(int start) {
        for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
            GL30.glActiveTexture(start + i);
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.getDepthTexture().getId()[i]);
        }
    }

    public void cleanBuffer() {
        GL30.glDeleteFramebuffers(this.depthFbo);
        this.depthTexture.cleanTextures();
    }

    public DepthTexture getDepthTexture() {
        return this.depthTexture;
    }

    public int getDepthFbo() {
        return this.depthFbo;
    }
}
