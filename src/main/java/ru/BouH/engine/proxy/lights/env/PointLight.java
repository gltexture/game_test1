package ru.BouH.engine.proxy.lights.env;

import ru.BouH.engine.proxy.lights.Attenuation;
import ru.BouH.engine.proxy.lights.Light;
import ru.BouH.engine.proxy.lights.LightType;
import ru.BouH.engine.render.scene.world.RenderWorld;

public class PointLight extends Light {
    private final Attenuation attenuation;

    public PointLight(RenderWorld renderWorld) {
        super(renderWorld, LightType.POINT_LIGHT);
        this.attenuation = new Attenuation(0.0f, 0.0f, 1.0f);
    }

    public Attenuation getAttenuation() {
        return this.attenuation;
    }
}
