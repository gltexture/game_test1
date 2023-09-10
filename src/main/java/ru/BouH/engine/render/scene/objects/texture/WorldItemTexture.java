package ru.BouH.engine.render.scene.objects.texture;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.objects.texture.samples.DefaultSample;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;

public class WorldItemTexture {
    public static final WorldItemTexture standardError = new WorldItemTexture(new DefaultSample(new Vector3d(0.0f, 0.0f, 0.0f), new Vector3d(1.0f, 0.0f, 1.0f)));
    private PNGTexture normalMap;

    private Sample sample;

    public WorldItemTexture(Sample sample) {
        this.sample = sample == null ? WorldItemTexture.standardError.getSample() : sample;
        this.setNormalMap((PNGTexture) null);
    }

    public WorldItemTexture(Sample sample, String normalMapPath) {
        this.sample = sample == null ? WorldItemTexture.standardError.getSample() : sample;
        this.setNormalMap(normalMapPath);
    }

    public static WorldItemTexture createItemTexture(Sample sample) {
        return new WorldItemTexture(sample);
    }

    public Sample getSample() {
        return this.sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public boolean hasNormalMap() {
        return this.getNormalMap() != null;
    }

    public PNGTexture getNormalMap() {
        return this.normalMap;
    }

    public void setNormalMap(String normalMapPath) {
        this.setNormalMap(PNGTexture.createTexture("normals/" + normalMapPath));
    }

    public void setNormalMap(PNGTexture normalMap) {
        this.normalMap = normalMap;
    }

    public int getRenderID() {
        return this.sample.getRenderID();
    }

    public boolean hasValueToPass() {
        return this.getValues() != null;
    }

    public PassUniValue[] getValues() {
        return this.getSample().toPassShaderValues();
    }

    public static class PassUniValue {
        private final String name;
        private final Object o;

        public PassUniValue(String name, Object o) {
            this.name = name;
            this.o = o;
        }

        public String getName() {
            return this.name;
        }

        public Object getO() {
            return this.o;
        }
    }
}
