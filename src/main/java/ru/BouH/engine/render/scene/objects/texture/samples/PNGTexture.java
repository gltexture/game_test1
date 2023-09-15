package ru.BouH.engine.render.scene.objects.texture.samples;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PNGTexture implements PictureSample {
    private int textureId;
    private int width;
    private int height;
    private ByteBuffer imageBuffer;

    private PNGTexture(InputStream inputStream) {
        try {
            PNGDecoder pngDecoder = new PNGDecoder(inputStream);
            this.width = pngDecoder.getWidth();
            this.height = pngDecoder.getHeight();
            ByteBuffer byteBuffer = null;
            try {
                byteBuffer = ByteBuffer.allocateDirect(4 * pngDecoder.getWidth() * pngDecoder.getHeight());
                pngDecoder.decode(byteBuffer, pngDecoder.getWidth() * 4, PNGDecoder.Format.RGBA);
                byteBuffer.flip();
            } catch (IOException e) {
                Game.getGame().getLogManager().warn("Byte buffer decoding error" + e.getCause());
            }
            this.imageBuffer = byteBuffer;
            this.textureId = GL20.glGenTextures();
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
            GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
            GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.getWidth(), this.getHeight(), 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, this.getPNGInBuffer());
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
            GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        } catch (IOException e) {
            Game.getGame().getLogManager().bigWarn(e.getMessage());
        }
    }

    public static PNGTexture createTexture(String textureName) {
        PNGTexture PNGTexture = null;
        try {
            Game.getGame().getLogManager().log("Loading Texture " + textureName);
            InputStream inputStream = Utils.loadTexture(textureName);
            if (inputStream == null) {
                throw new IOException("Wrong texture: " + textureName);
            } else {
                PNGTexture = new PNGTexture(inputStream);
            }
        } catch (IOException e) {
            Game.getGame().getLogManager().bigWarn(e.toString());
        }
        if (PNGTexture != null && PNGTexture.isValid()) {
            Game.getGame().getLogManager().log("Texture " + textureName + " loaded");
        } else {
            Game.getGame().getLogManager().warn("Texture " + textureName + "not loaded");
        }
        return PNGTexture;
    }

    public static PNGTexture createTexture(InputStream inputStream) {
        return new PNGTexture(inputStream);
    }

    public boolean isValid() {
        return this.getPNGInBuffer() != null;
    }

    public ByteBuffer getPNGInBuffer() {
        return this.imageBuffer;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void clear() {
        MemoryUtil.memFree(this.getPNGInBuffer());
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glDeleteTextures(this.textureId);
    }

    public void performTexture(int code) {
        GL20.glActiveTexture(code);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
    }

    @Override
    public int getRenderID() {
        return 0;
    }

    @Override
    public WorldItemTexture.PassUniValue[] toPassShaderValues() {
        return new WorldItemTexture.PassUniValue[]{new WorldItemTexture.PassUniValue("texture_sampler", 0)};
    }
}