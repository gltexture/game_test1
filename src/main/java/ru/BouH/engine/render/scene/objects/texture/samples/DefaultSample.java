package ru.BouH.engine.render.scene.objects.texture.samples;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;

public class DefaultSample implements Sample {
    private final Vector3d colors1;
    private final Vector3d colors2;
    private final int scaling;

    public DefaultSample(Vector3d colors1, Vector3d colors2, int scaling) {
        this.colors1 = colors1;
        this.colors2 = colors2;
        this.scaling = scaling;
    }

    public int getScaling() {
        return this.scaling;
    }

    public Vector3d getColors1() {
        return this.colors1;
    }

    public Vector3d getColors2() {
        return this.colors2;
    }

    @Override
    public WorldItemTexture.PassUniValue[] toPassShaderValues() {
        return new WorldItemTexture.PassUniValue[] {new WorldItemTexture.PassUniValue("quads_c1", this.getColors1()), new WorldItemTexture.PassUniValue("quads_c2", this.getColors2()), new WorldItemTexture.PassUniValue("quads_scaling", this.getScaling())};
    }

    @Override
    public int getRenderID() {
        return 2;
    }
}
