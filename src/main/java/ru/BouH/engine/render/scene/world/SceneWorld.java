package ru.BouH.engine.render.scene.world;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.components.IMesh;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;

import java.util.*;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    public static Set<IMesh> toCleanSet = new HashSet<>();
    public static float elapsedRenderTicks;
    private final List<PhysXObject> entityList = new ArrayList<>();
    private final Environment environment;
    private final World world;
    private FrustumCulling frustumCulling;

    public SceneWorld(World world) {
        this.world = world;
        this.environment = new Environment(this);
        this.frustumCulling = null;
    }

    public List<PhysXObject> getFilteredEntityList() {
        List<PhysXObject> physXObjects = new ArrayList<>(this.getEntityList());
        if (this.getFrustumCulling() == null) {
            return physXObjects;
        }
        return physXObjects.stream().filter(PhysXObject::isVisible).collect(Collectors.toList());
    }

    public List<PhysXObject> getEntityList() {
        return this.entityList;
    }

    public void addItem(WorldItem worldItem, RenderData renderData) throws GameException {
        if (renderData == null) {
            throw new GameException("Wrong render parameters: " + worldItem.toString());
        }
        this.addPhysEntity(renderData.getPhysRender(this, worldItem));
    }

    public void addPhysEntity(PhysXObject physXObject) {
        physXObject.onSpawn(this);
        this.getEntityList().add(physXObject);
    }

    public void removeEntity(PhysXObject physXObject) {
        physXObject.onDestroy(this);
        this.getEntityList().remove(physXObject);
    }

    public void removeAllEntities() {
        Iterator<PhysXObject> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            PhysXObject physXObject = iterator.next();
            physXObject.onDestroy(this);
            iterator.remove();
        }
    }

    public void addAttachedLight(PhysXObject physXObject, ILight light) {
        light.doAttachTo(physXObject);
        this.addLight(light);
    }

    public void doDetachLight(PhysXObject physXObject) {
        physXObject.getLight().doAttachTo(null);
    }

    public void deactivateLight(ILight light) {
        light.deactivate();
    }

    public void removeLight(PhysXObject physXObject) {
        this.removeLight(physXObject.getLight());
    }

    public void removeLight(ILight iLight) {
        this.getEnvironment().getLightManager().removeLight(iLight);
    }

    public void deactivateLight(PhysXObject physXObject) {
        this.deactivateLight(physXObject.getLight());
    }

    public void addLight(ILight light) {
        this.getEnvironment().getLightManager().addLight(light);
    }

    @Override
    public void onWorldStart() {
        Game.getGame().getProfiler().startSection(SectionManager.renderWorld);
    }

    public void onWorldEntityUpdate(double partialTicks) {
        this.onWorldUpdate();
        Iterator<PhysXObject> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            PhysXObject physXObject = iterator.next();
            physXObject.onUpdate(this);
            physXObject.updateRenderPos(partialTicks);
            physXObject.checkCulling(this.getFrustumCulling());
            if (physXObject.isDead()) {
                physXObject.onDestroy(this);
                iterator.remove();
            }
        }
    }

    public void onWorldUpdate() {
        SceneWorld.elapsedRenderTicks += 0.01f;
        this.getEnvironment().updateEnvironment();
    }

    @Override
    public void onWorldEnd() {
        Game.getGame().getProfiler().endSection(SectionManager.renderWorld);
        Game.getGame().getLogManager().log("Cleaning meshes!");
        SceneWorld.toCleanSet.forEach(IMesh::cleanMesh);
        Game.getGame().getLogManager().log("Successfully cleaned meshes!");
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
