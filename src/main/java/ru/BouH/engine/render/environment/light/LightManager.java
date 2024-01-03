package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LightManager {
    public static final int MAX_POINT_LIGHTS = 1024;
    private final SceneWorld sceneWorld;
    private final Sun sun;
    private final List<PointLight> pointLightList;
    private List<PointLight> activePointLights;

    public LightManager(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.sun = new Sun();
        this.pointLightList = new ArrayList<>(LightManager.MAX_POINT_LIGHTS);
        this.activePointLights = new ArrayList<>(LightManager.MAX_POINT_LIGHTS);
        this.init();
    }

    public static float[] getPointLightArray(PointLight pointLight) {
        return new float[]{(float) pointLight.getLightPos().x, (float) pointLight.getLightPos().y, (float) pointLight.getLightPos().z, (float) pointLight.getLightColor().x, (float) pointLight.getLightColor().y, (float) pointLight.getLightColor().z, (float) pointLight.getBrightness()};
    }

    public static float[] getNormalisedPointLightArray(Matrix4d viewMatrix, PointLight pointLight) {
        Vector3d newPos = pointLight.getNormalizedPointLightPos(viewMatrix);
        return new float[]{(float) newPos.x, (float) newPos.y, (float) newPos.z, (float) pointLight.getLightColor().x, (float) pointLight.getLightColor().y, (float) pointLight.getLightColor().z, (float) pointLight.getBrightness()};
    }

    private void init() {
        this.setSunAngle(new Vector3f(1.0f, 1.0f, 0.0f));
        this.fillCollections();
    }

    private void fillCollections() {
        for (int i = 0; i < LightManager.MAX_POINT_LIGHTS; i++) {
            this.getPointLightList().add(new PointLight());
        }
    }

    public void updateLightManager() {
        List<PointLight> lights1 = new ArrayList<>(this.getPointLightList());
        this.activePointLights = lights1.stream().filter(PointLight::isActive).collect(Collectors.toList());
        lights1.forEach(e -> e.onUpdate(this.getSceneWorld()));
    }

    public void addLight(ILight iLight) {
        if (iLight.lightType() == LightType.POINT_LIGHT) {
            int i = this.getActivePointLights().size();
            if (i >= LightManager.MAX_POINT_LIGHTS) {
                Game.getGame().getLogManager().bigWarn("Reached point lights limit: " + LightManager.MAX_POINT_LIGHTS);
                return;
            }
            this.getPointLightList().set(i, (PointLight) iLight);
        }
    }

    public void removeLight(ILight iLight) {
        iLight.doAttachTo(null);
        iLight.deactivate();
    }

    public PointLight createPointLight(Vector3d lightColor, PhysicsObject physicsObject, double brightness) {
        PointLight pointLight = new PointLight(lightColor, physicsObject, brightness);
        int i = this.getActivePointLights().size();
        this.getPointLightList().set(i, pointLight);
        return pointLight;
    }

    public Vector3f getSunAngle() {
        return this.sun.getSunPosition();
    }

    public void setSunAngle(Vector3f vector3f) {
        this.sun.setSunPosition(vector3f);
    }

    public float getSunBrightness() {
        return this.sun.getBrightness();
    }

    public Vector3f getNormalisedSunAngle(Matrix4d viewMatrix) {
        Vector3f direction = this.getSunAngle();
        Vector4d newDir = new Vector4d(direction, 0.0f);
        newDir.mul(viewMatrix);
        return new Vector3f((float) newDir.x, (float) newDir.y, (float) newDir.z);
    }

    public float calcAmbientLight() {
        float ambient = 0.5f;
        return Math.max(ambient * this.getSunBrightness(), 5.0e-2f);
    }

    public List<PointLight> getActivePointLights() {
        return this.activePointLights;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }
}
