package ru.BouH.engine.render.scene.components;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Texture {
    private PNGDecoder pngDecoder;
    private int textureId;
    private Texture(InputStream inputStream) {
        try {
            this.pngDecoder = new PNGDecoder(inputStream);
            this.textureId = GL20.glGenTextures();
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
            GL20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
            GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, this.getWidth(), this.getHeight(), 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, this.getPNGInBuffer());
            GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D);
        } catch (IOException e) {
            Game.getGame().getLogManager().bigWarn(e.getMessage());
        }
    }

    public static Texture createTexture(String textureName) {
        Texture texture = null;
        try {
            Game.getGame().getLogManager().log("Loading texture " + textureName);
            InputStream inputStream = Utils.loadTexture(textureName);
            if (inputStream == null) {
                throw new IOException("Error loading texture " + textureName);
            } else {
                texture = new Texture(inputStream);
                Game.getGame().getLogManager().log("Texture " + textureName + " loaded");
            }
        } catch (IOException e) {
            Game.getGame().getLogManager().bigWarn(e.toString());
        }
        return texture;
    }

    public static Texture createTexture(InputStream inputStream) {
        return new Texture(inputStream);
    }

    public ByteBuffer getPNGInBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * this.pngDecoder.getWidth() * this.pngDecoder.getHeight());
        try {
            this.pngDecoder.decode(byteBuffer, this.pngDecoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            return (ByteBuffer) byteBuffer.flip();
        } catch (IOException e) {
            Game.getGame().getLogManager().error("Byte buffer decoding error\n" + e.getCause());
        }
        return null;
    }

    public int getWidth() {
        return this.pngDecoder.getWidth();
    }

    public int getHeight() {
        return this.pngDecoder.getHeight();
    }

    public void performTexture() {
        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, this.textureId);
    }
}
