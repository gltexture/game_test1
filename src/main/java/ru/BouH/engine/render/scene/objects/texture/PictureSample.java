package ru.BouH.engine.render.scene.objects.texture;

public interface PictureSample extends Sample {
    void performTexture(int code);
    boolean isValid();
}
