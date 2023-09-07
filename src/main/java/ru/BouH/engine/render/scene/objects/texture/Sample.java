package ru.BouH.engine.render.scene.objects.texture;

public interface Sample {
    int getRenderID();

    default WorldItemTexture.PassUniValue[] toPassShaderValues() {
        return null;
    }
}
