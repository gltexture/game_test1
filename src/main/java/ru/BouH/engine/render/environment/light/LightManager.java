package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LightManager {
    public static final int MAX_POINT_LIGHTS = 256;
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

    private void init() {
        this.setSunAngle(new Vector3f(1.0f, 1.0f, 0.0f));
        this.fillCollections();
    }

    private void fillCollections() {
        for (int i = 0; i < LightManager.MAX_POINT_LIGHTS; i++) {
            this.getPointLightList().add(new PointLight());
        }
    }

    public static float[] getPointLightArray(PointLight pointLight) {
        return new float[] {(float) pointLight.getLightPos().x, (float) pointLight.getLightPos().y, (float) pointLight.getLightPos().z, (float) pointLight.getLightColor().x, (float) pointLight.getLightColor().y, (float) pointLight.getLightColor().z, (float) pointLight.getBrightness()};
    }

    public static float[] getNormalisedPointLightArray(Matrix4d viewMatrix, PointLight pointLight) {
        Vector3d newPos = pointLight.getNormalizedPointLightPos(viewMatrix);
        return new float[] {(float)newPos.x, (float) newPos.y, (float) newPos.z, (float) pointLight.getLightColor().x, (float) pointLight.getLightColor().y, (float) pointLight.getLightColor().z, (float) pointLight.getBrightness()};
    }

    public void updateLightManager() {
        List<PointLight> lights1 = new ArrayList<>(this.getPointLightList());
        this.activePointLights = lights1.stream().filter(PointLight::isActive).collect(Collectors.toList());
        lights1.forEach(e -> e.onUpdate(this.getSceneWorld()));
    }

    public void addLight(ILight iLight) {
        int i = this.getActivePointLights().size();
        if (i >= LightManager.MAX_POINT_LIGHTS) {
            Game.getGame().getLogManager().bigWarn("Reached point lights limit: " + LightManager.MAX_POINT_LIGHTS);
            return;
        }
        if (iLight.lightType() == LightType.POINT_LIGHT) {
            this.getPointLightList().set(i, (PointLight) iLight);
        }
    }

    public void removeLight(ILight iLight) {
        if (iLight.lightType() == LightType.POINT_LIGHT) {
            PointLight pointLight = (PointLight) iLight;
            pointLight.doAttachTo(null);
            pointLight.deactivate();
        }
    }

    public PointLight createPointLight(Vector3d lightColor, WorldItem worldItem, double brightness) {
        PointLight pointLight = new PointLight(lightColor, worldItem, brightness);
        int i = this.getActivePointLights().size();
        this.getPointLightList().set(i, pointLight);
        return pointLight;
    }

    public void setSunAngle(Vector3f vector3f) {
        this.sun.setSunPosition(vector3f);
    }

    public Vector3f getSunAngle() {
        return this.sun.getSunPosition();
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
