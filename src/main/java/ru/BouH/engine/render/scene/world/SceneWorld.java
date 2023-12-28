package ru.BouH.engine.render.scene.world;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.components.IMesh;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.utils.synchronizing.Syncer;

import java.util.*;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    public static Set<IMesh> toCleanSet = new HashSet<>();
    public static float elapsedRenderTicks;
    private final List<PhysicsObject> entityList = new ArrayList<>();
    private final Environment environment;
    private final World world;
    private FrustumCulling frustumCulling;
    public Syncer refreshSyncer = new Syncer();

    public SceneWorld(World world) {
        this.world = world;
        this.environment = new Environment(this);
        this.frustumCulling = null;
    }

    public List<PhysicsObject> getFilteredEntityList() {
        List<PhysicsObject> physicsObjects = new ArrayList<>(this.getEntityList());
        if (this.getFrustumCulling() == null) {
            return physicsObjects;
        }
        return physicsObjects.stream().filter(PhysicsObject::isVisible).collect(Collectors.toList());
    }

    public List<PhysicsObject> getEntityList() {
        return this.entityList;
    }

    public void addItem(WorldItem worldItem, RenderData renderData) throws GameException {
        if (renderData == null) {
            throw new GameException("Wrong render parameters: " + worldItem.toString());
        }
        this.addPhysEntity(renderData.getPhysRender(this, worldItem));
    }
    public static PhysicsObject PL = null;

    public void addPhysEntity(PhysicsObject physicsObject) {
        physicsObject.onSpawn(this);
        this.getEntityList().add(physicsObject);
        if (physicsObject.getWorldItem() instanceof EntityPlayerSP) {
            PL = physicsObject;
        }
    }

    public void removeEntity(PhysicsObject physicsObject) {
        physicsObject.onDestroy(this);
        this.getEntityList().remove(physicsObject);
    }

    public void removeAllEntities() {
        Iterator<PhysicsObject> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            PhysicsObject physicsObject = iterator.next();
            physicsObject.onDestroy(this);
            iterator.remove();
        }
    }

    public void addAttachedLight(PhysicsObject physicsObject, ILight light) {
        light.doAttachTo(physicsObject);
        this.addLight(light);
    }

    public void doDetachLight(PhysicsObject physicsObject) {
        physicsObject.getLight().doAttachTo(null);
    }

    public void deactivateLight(ILight light) {
        light.deactivate();
    }

    public void removeLight(PhysicsObject physicsObject) {
        this.removeLight(physicsObject.getLight());
    }

    public void removeLight(ILight iLight) {
        this.getEnvironment().getLightManager().removeLight(iLight);
    }

    public void deactivateLight(PhysicsObject physicsObject) {
        this.deactivateLight(physicsObject.getLight());
    }

    public void addLight(ILight light) {
        this.getEnvironment().getLightManager().addLight(light);
    }

    @Override
    public void onWorldStart() {
        Game.getGame().getProfiler().startSection(SectionManager.renderWorld);
    }

    public void onWorldUpdate() {
        this.getEnvironment().updateEnvironment();
        SceneWorld.elapsedRenderTicks += 0.01f;
    }

    @Override
    public void onWorldEnd() {
        Game.getGame().getProfiler().endSection(SectionManager.renderWorld);
        Game.getGame().getLogManager().log("Cleaning meshes!");
        this.removeAllEntities();
        SceneWorld.toCleanSet.forEach(IMesh::cleanMesh);
        Game.getGame().getLogManager().log("Successfully cleaned meshes!");
    }

    public void onWorldEntityUpdate(boolean refresh, double partialTicks) {
        Iterator<PhysicsObject> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            PhysicsObject physicsObject = iterator.next();
            if (refresh) {
                physicsObject.refreshInterpolatingState();
                physicsObject.setPrevPos(physicsObject.getWorldItem().getPosition());
                physicsObject.setPrevRot(physicsObject.getWorldItem().getRotation());
            }
            physicsObject.onUpdate(this);
            physicsObject.updateRenderPos(partialTicks);
            physicsObject.checkCulling(this.getFrustumCulling());
            if (physicsObject.isDead()) {
                physicsObject.onDestroy(this);
                iterator.remove();
            }
        }
        this.onWorldUpdate();
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public void setFrustumCulling(FrustumCulling frustumCulling) {
        this.frustumCulling = frustumCulling;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public World getWorld() {
        return this.world;
    }
}
