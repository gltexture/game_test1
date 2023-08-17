package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class LightManager {
    private final Sun sun;
    private final double minAmbientLight;
    private final Vector3f sunNormal;

    public LightManager(SceneWorld sceneWorld) {
        this.sun = new Sun(new Vector3f(0.25f, 1.0f, 0.25f));
        this.minAmbientLight = 0.1f;
        this.sunNormal = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    public void setSunAngle(Vector3f vector3f) {
        this.sun.setSunPosition(vector3f);
    }

    public Vector3f getSunAngle() {
        return this.sun.getSunPosition();
    }

    public Vector3f getNormalisedSunAngle(Matrix4d viewMatrix) {
        Vector3f direction = this.getSunAngle();
        Vector4d newDir = new Vector4d(direction, 0.0f);
        newDir.mul(viewMatrix);
        return new Vector3f((float) newDir.x, (float) newDir.y, (float) newDir.z);
    }

    public float calcAmbientLight() {
        float angle = this.getSunAngle().angleCos(this.sunNormal);
        return (float) MathHelper.max(angle, this.minAmbientLight);
    }
}
