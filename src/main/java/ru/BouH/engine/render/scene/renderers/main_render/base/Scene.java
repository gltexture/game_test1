package ru.BouH.engine.render.scene.renderers.main_render.base;

import org.joml.Vector3d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.components.Camera;
import ru.BouH.engine.render.scene.renderers.main_render.WorldRender;
import ru.BouH.engine.render.scene.renderers.main_render.GuiRender;
import ru.BouH.engine.render.scene.renderers.main_render.SkyRender;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scene {
    private final List<SceneRenderBase> sceneRenderBases;
    private final SceneWorld sceneWorld;
    private final SkyRender skyRender;
    private final GuiRender guiRender;
    private final WorldRender worldRender;

    public Scene(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.sceneRenderBases = new ArrayList<>();
        this.skyRender = new SkyRender(sceneWorld);
        this.worldRender = new WorldRender(sceneWorld);
        this.guiRender = new GuiRender(sceneWorld);
    }

    public void init() {
        this.sceneRenderBases.add(this.getSkyRender());
        this.sceneRenderBases.add(this.getEntityRender());
        this.sceneRenderBases.add(this.getGuiRender());
        this.sceneRenderBases.sort(Comparator.comparing(SceneRenderBase::getRenderPriority));
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
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("... " + sceneRenderBase.getRenderGroup().name());
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Success " + sceneRenderBase.getRenderGroup().name());
        }
        Game.getGame().getLogManager().log("Scene rendering started");
    }

    public void renderScene(double partialTicks) {
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            sceneRenderBase.bindProgram();
            sceneRenderBase.onRender(partialTicks);
            sceneRenderBase.unBindProgram();
        }
        this.lerpCamera(partialTicks);
    }

    private void lerpCamera(double partialTicks) {
        EntityPlayerSP entityPlayerSP = this.sceneWorld.getWorld().getLocalPlayer();
        Vector3d camPos = new Vector3d(entityPlayerSP.getPosition().x, entityPlayerSP.getEyeHeight(), entityPlayerSP.getPosition().z);
        this.sceneWorld.getCamera().getCamPosition().lerp(camPos, partialTicks);
        this.sceneWorld.getCamera().setCamRotation(this.sceneWorld.getWorld().getLocalPlayer().getRotation().x, this.sceneWorld.getWorld().getLocalPlayer().getRotation().y, this.sceneWorld.getWorld().getLocalPlayer().getRotation().z);
    }

    public void postRender() {
        Game.getGame().getLogManager().log("Stopping scene rendering: ");
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("... " + sceneRenderBase.getRenderGroup().name());
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Success " + sceneRenderBase.getRenderGroup().name());
        }
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }
}
