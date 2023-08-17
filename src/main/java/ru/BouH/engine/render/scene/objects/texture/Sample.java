package ru.BouH.engine.render.scene.objects.texture;

public interface Sample {
    int getRenderID();
    default ItemTexture.PassUniValue[] toPassShaderValues() {
        return null;
    }
}
