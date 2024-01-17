package ru.BouH.engine.render.environment;

import org.joml.Vector3f;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.environment.sky.Sky;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class Environment {
    private final SceneWorld sceneWorld;
    private final LightManager lightManager;
    private Sky sky;

    public Environment(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.lightManager = new LightManager(sceneWorld);
        this.sky = new Sky(ResourceManager.renderAssets.skyboxCubeMap);
        Vector3f vector3f = this.getSunPosition();
    }

    public void updateEnvironment() {
        this.getLightManager().updateLightManager();
    }

    public PointLight getPointLightByID(int id) {
        return this.getLightManager().getPointLightList().get(id);
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
