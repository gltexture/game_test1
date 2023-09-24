package ru.BouH.engine.render.scene.objects.texture.samples;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;

public class DefaultSample implements Sample {
    private final Vector3d colors1;
    private final Vector3d colors2;

    public DefaultSample(Vector3d colors1, Vector3d colors2) {
        this.colors1 = colors1;
        this.colors2 = colors2;
    }

    public Vector3d getColors1() {
        return this.colors1;
    }

    public Vector3d getColors2() {
        return this.colors2;
    }

    @Override
    public int getRenderID() {
        return 2;
    }

    @Override
    public WorldItemTexture.PassUniValue[] toPassShaderValues() {
        return new WorldItemTexture.PassUniValue[]{new WorldItemTexture.PassUniValue("quads_c1", this.getColors1()), new WorldItemTexture.PassUniValue("quads_c2", this.getColors2())};
    }
}
