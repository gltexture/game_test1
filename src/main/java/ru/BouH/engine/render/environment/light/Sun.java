package ru.BouH.engine.render.environment.light;

import org.joml.Vector3f;
import ru.BouH.engine.math.MathHelper;

public class Sun {
    private Vector3f sunPosition;

    public Sun() {
        this.sunPosition = new Vector3f(0, 1, 0);
    }

    public void setSunPosition(Vector3f sunPosition) {
        this.sunPosition = sunPosition;
        this.sunPosition.normalize();
    }

    public float getBrightness() {
        Vector3f nv = this.getSunPosition().mul(1, 0, 1);
        float angle1 = nv.angle(this.getSunPosition());
        float factor = MathHelper.sin(Math.toDegrees(angle1));
        factor += (factor * 0.2f);
        return MathHelper.clamp(factor, 0.0f, 1.0f);
    }

    public Vector3f getSunPosition() {
        return new Vector3f(this.sunPosition);
    }
}
