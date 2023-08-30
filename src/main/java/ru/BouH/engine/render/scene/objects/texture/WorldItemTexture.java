package ru.BouH.engine.render.scene.objects.texture;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.objects.texture.samples.DefaultSample;

public class WorldItemTexture {
    public static final WorldItemTexture standardError = new WorldItemTexture(new DefaultSample(new Vector3d(0.0f, 0.0f, 0.0f), new Vector3d(1.0f, 0.0f, 1.0f), 32));
    private Sample sample;

    public WorldItemTexture(Sample sample) {
        this.sample = sample == null ? WorldItemTexture.standardError.getSample() : sample;
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
