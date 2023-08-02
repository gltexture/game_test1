package ru.BouH.engine.render.environment.sky;

import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.render.environment.light.Sun;

public class Sky {
    private final SkyBox skyBox;
    private final Sun sun;

    public Sky(String texturePath) {
        this.skyBox = new SkyBox(texturePath);
        this.sun = new Sun(90);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    protected Sun getSun() {
        return this.sun;
    }

    public double getSunPosition() {
        return this.getSun().getSunPosition();
    }

    public void setSunAngle(double angle) {
        this.getSun().setSunPosition(MathHelper.sin(angle));
    }
}
