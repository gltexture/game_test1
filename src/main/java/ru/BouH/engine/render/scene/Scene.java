package ru.BouH.engine.render.scene;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.scene.scene_render.GuiRender;
import ru.BouH.engine.render.scene.scene_render.SkyRender;
import ru.BouH.engine.render.scene.scene_render.WorldRender;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scene {
    private final List<SceneRenderBase> sceneRenderBases;
    private final SceneWorld sceneWorld;
    private final SkyRender skyRender;
    private final GuiRender guiRender;
    private final WorldRender worldRender;
    private ICamera currentCamera;

    public Scene(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.sceneRenderBases = new ArrayList<>();
        this.skyRender = new SkyRender(sceneWorld);
        this.worldRender = new WorldRender(sceneWorld);
        this.guiRender = new GuiRender(sceneWorld);
        this.currentCamera = null;
    }

    public void init() {
        this.sceneRenderBases.add(this.getSkyRender());
        this.sceneRenderBases.add(this.getEntityRender());
        this.sceneRenderBases.add(this.getGuiRender());
        this.sceneRenderBases.sort(Comparator.comparing(SceneRenderBase::getRenderPriority));
    }

    public static void setCamera(ICamera camera) {
        Game.getGame().getScreen().getScene().setRenderCamera(camera);
    }

    public void setRenderCamera(ICamera camera) {
        this.currentCamera = camera;
    }

    public ICamera getCurrentCamera() {
        return this.currentCamera;
    }

    public WorldRender getEntityRender() {
        return this.worldRender;
    }

    public GuiRender getGuiRender() {
        return this.guiRender;
    }

    public SkyRender getSkyRender() {
        return this.skyRender;
    }

    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }

    public void preRender() {
        this.attachCameraToLocalPlayer(Game.getGame().getProxy().getLocalPlayer());
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully started!");
        }
        Game.getGame().getLogManager().log("Scene rendering started");
    }

    protected void attachCameraToLocalPlayer(LocalPlayer localPlayer) {
        if (localPlayer != null && localPlayer.getEntityPlayerSP() != null) {
            this.setRenderCamera(new AttachedCamera(localPlayer.getEntityPlayerSP()));
        }
    }

    public boolean isCameraAttachedToItem(WorldItem worldItem) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getWorldItem() == worldItem;
    }

    public void renderScene(double partialTicks) {
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            sceneRenderBase.bindProgram();
            sceneRenderBase.onRender(partialTicks);
            sceneRenderBase.unBindProgram();
        }
        if (this.getCurrentCamera() != null) {
            this.getCurrentCamera().updateCamera(partialTicks);
        }
    }

    public void postRender() {
        Game.getGame().getLogManager().log("Stopping scene rendering: ");
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Stopping " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully stopped!");
        }
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }
}
