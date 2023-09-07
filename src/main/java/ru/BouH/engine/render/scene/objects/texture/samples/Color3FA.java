package ru.BouH.engine.render.scene.objects.texture.samples;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;

public final class Color3FA implements Sample {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public Color3FA(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color3FA(int hex) {
        float[] c = Color3FA.HEX2RGB(hex);
        this.red = c[0];
        this.green = c[1];
        this.blue = c[2];
        this.alpha = 1.0f;
    }

    public Color3FA(Vector4f vector4f) {
        this(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public Color3FA(Vector3f vector3f) {
        this(new Vector4f(vector3f, 1.0f));
    }

    public Color3FA(Vector4d vector4d) {
        this((float) vector4d.x, (float) vector4d.y, (float) vector4d.z, (float) vector4d.w);
    }

    public Color3FA(Vector3d vector3d) {
        this(new Vector4d(vector3d, 1.0d));
    }

    public static float[] HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
    }

    public Vector4f getVectorColors4f() {
        return new Vector4f(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
    }

    public Vector3f getVectorColors3f() {
        return new Vector3f(this.getRed(), this.getGreen(), this.getBlue());
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getAlpha() {
        return this.alpha;
    }

    @Override
    public int getRenderID() {
        return 1;
    }

    @Override
    public WorldItemTexture.PassUniValue[] toPassShaderValues() {
        return new WorldItemTexture.PassUniValue[]{new WorldItemTexture.PassUniValue("object_rgb", this.getVectorColors4f())};
    }
}
