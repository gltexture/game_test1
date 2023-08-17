package ru.BouH.engine.render.environment;

import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.environment.sky.Sky;
import ru.BouH.engine.render.scene.primitive_forms.VectorForm;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class Environment {
    private Sky sky;
    private final SceneWorld sceneWorld;
    private final LightManager lightManager;
    public VectorForm sunDebugVector;

    public Environment(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.lightManager = new LightManager(sceneWorld);
        this.sky = new Sky(sceneWorld, "environment/skybox/skybox1.png");
        Vector3f vector3f = this.getSunPosition();
        this.sunDebugVector = new VectorForm(new Vector3d(0.0f, 0.0f, 0.0f), new Vector3d(vector3f).mul(100));
    }

    public Vector3f getSunPosition() {
        return this.getLightManager().getSunAngle();
    }

    public void setSunAngle(Vector3f angle) {
        this.getLightManager().setSunAngle(angle);
    }

    public LightManager getLightManager() {
        return this.lightManager;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public Sky getSky() {
        return this.sky;
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }
}
