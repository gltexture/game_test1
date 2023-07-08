package ru.BouH.engine.render.scene.world;

import org.joml.Vector3d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.proxy.lights.Light;
import ru.BouH.engine.proxy.lights.LightType;
import ru.BouH.engine.proxy.lights.env.DirectionalLight;
import ru.BouH.engine.proxy.lights.env.PointLight;
import ru.BouH.engine.proxy.lights.env.SpotLight;
import ru.BouH.engine.render.scene.components.Camera;
import ru.BouH.engine.render.scene.render.entities.init.RenderEntity;
import ru.BouH.engine.render.scene.render.scene.GuiRender;
import ru.BouH.engine.render.scene.render.scene.SceneRender;
import ru.BouH.engine.render.scene.render.scene.SkyRender;
import ru.BouH.engine.render.scene.world.render.RenderManager;

import java.util.*;

public class RenderWorld {
    private final RenderManager renderManager;
    private final SceneRender sceneRender;
    private final Camera camera;
    private final GuiRender guiRender;
    private final SkyRender skyRender;
    private final List<PointLight> pointLightList = new ArrayList<>();
    private final List<SpotLight> spotLightList = new ArrayList<>();
    private final Set<RenderEntity> renderEntitySet = new HashSet<>();
    private final DirectionalLight directionalLight;
    private final World world;

    public RenderWorld(World world) {
        this.world = world;
        this.renderManager = new RenderManager();
        this.sceneRender = new SceneRender(this);
        this.directionalLight = new DirectionalLight(this);
        this.directionalLight.setDirection(new Vector3d((float) Math.cos(Math.toRadians(230)), (float) Math.sin(Math.toRadians(90)), (float) Math.cos(Math.toRadians(60))));
        this.directionalLight.setIntensity(0.75f);
        this.directionalLight.doEnable();
        this.camera = new Camera();
        this.guiRender = new GuiRender(this);
        this.skyRender = new SkyRender(this);
    }

    public Set<RenderEntity> getRenderEntitySet() {
        return this.renderEntitySet;
    }

    public void addEntity(RenderEntity renderEntity) {
        renderEntity.getEntityModel().getRender().onStartRender(renderEntity);
        this.renderEntitySet.add(renderEntity);
    }

    public void removeEntity(RenderEntity renderEntity) {
        if (renderEntity.getLight() != null) {
            this.removeLight(renderEntity.getLight());
        }
        renderEntity.getEntityModel().getRender().onStopRender(renderEntity);
        this.renderEntitySet.remove(renderEntity);
    }

    public void onWorldUpdate() {
        Iterator<RenderEntity> iterator = this.getRenderEntitySet().iterator();
        while (iterator.hasNext()) {
            RenderEntity renderEntity = iterator.next();
            PhysEntity physEntity = renderEntity.getEntity();
            if (physEntity.isDead()) {
                if (physEntity.getLight() != null) {
                    this.removeLight(physEntity.getLight());
                }
                iterator.remove();
            } else {
                renderEntity.onUpdate();
            }
        }
    }

    public SceneRender getSceneRender() {
        return this.sceneRender;
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public GuiRender getGuiRender() {
        return this.guiRender;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }

    public List<SpotLight> getSpotLightList() {
        return this.spotLightList;
    }

    public DirectionalLight getDirectionalLight() {
        return this.directionalLight;
    }

    public SkyRender getSkyRender() {
        return this.skyRender;
    }

    public World getWorld() {
        return this.world;
    }

    public Light createLight(LightType lightType) {
        Light light = null;
        switch (lightType) {
            case POINT_LIGHT: {
                PointLight pointLight = new PointLight(this);
                if (this.getPointLightList().size() >= RenderManager.MAX_POINT_LIGHTS) {
                    Game.getGame().getLogManager().bigWarn("Warning! Point lights reached limit " + RenderManager.MAX_POINT_LIGHTS);
                } else {
                    light = pointLight;
                    Game.getGame().getLogManager().log("Added new point light in world");
                    this.getPointLightList().add(pointLight);
                }
                break;
            }
            case SPOT_LIGHT: {
                SpotLight spotLight = new SpotLight(this, new Vector3d(0.0f, 0.0f, -1.0f), 30.0f);
                if (this.getPointLightList().size() >= RenderManager.MAX_SPOT_LIGHTS) {
                    Game.getGame().getLogManager().bigWarn("Warning! Spot lights reached limit " + RenderManager.MAX_POINT_LIGHTS);
                } else {
                    light = spotLight;
                    Game.getGame().getLogManager().log("Added new spot light in world");
                    this.getSpotLightList().add(spotLight);
                }
                break;
            }
        }
        return light;
    }

    public void removeLight(Light light) {
        switch (light.getLightType()) {
            case POINT_LIGHT: {
                PointLight pointLight = (PointLight) light;
                Game.getGame().getLogManager().log("Removing " + light.getLightType().getId() + " - [" + pointLight.getPosition() + "]");
                this.getPointLightList().remove(pointLight);
                break;
            }
            case SPOT_LIGHT: {
                SpotLight spotLight = (SpotLight) light;
                Game.getGame().getLogManager().log("Removing " + light.getLightType().getId() + " - [" + spotLight.getPosition() + "]");
                this.getSpotLightList().remove(spotLight);
                break;
            }
            case DIRECTIONAL_LIGHT: {
                Game.getGame().getLogManager().log("Removing DirLight");
                this.getDirectionalLight().doDisable();
                break;
            }
        }
    }
}
