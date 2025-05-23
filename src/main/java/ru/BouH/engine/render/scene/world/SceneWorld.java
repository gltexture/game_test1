package ru.BouH.engine.render.scene.world;

import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.scene.components.Camera;
import ru.BouH.engine.render.scene.renderers.items.entity.EntityItem;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;
import ru.BouH.engine.render.scene.renderers.items.terrain.TerrainItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SceneWorld {
    private final Camera camera;
    private final List<EntityItem> entityList = new ArrayList<>();
    private final Environment environment;
    private final World world;
    private final TerrainItem terrainItem;
    public float tick;

    public SceneWorld(World world) {
        this.world = world;
        this.terrainItem = new TerrainItem(this.getWorld().getTerrain());
        this.environment = new Environment();
        this.camera = new Camera();
    }

    public List<EntityItem> getEntityList() {
        return this.entityList;
    }

    public void addEntity(PhysEntity physEntity, EntityModel entityModel) {
        EntityItem entityItem = new EntityItem(this, physEntity, entityModel);
        if (entityItem.isHasRender()) {
            entityModel.getPropForm().getiRenderFabric().onStartRender(entityItem);
        }
        this.addEntity(entityItem);
    }

    public void addEntity(EntityItem entityItem) {
        this.entityList.add(entityItem);
    }

    public void removeEntity(EntityItem entityItem) {
        entityItem.renderFabric().onStopRender(entityItem);
        this.entityList.remove(entityItem);
    }

    public void tickWorld() {
        this.tick += 0.01f;
        this.getEnvironment().getSky().setSunAngle(this.tick * 10.0f);
    }

    public void onWorldRenderUpdate() {
        Iterator<EntityItem> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            EntityItem entityItem = iterator.next();
            PhysEntity physEntity = entityItem.getEntity();
            if (physEntity.isDead()) {
                iterator.remove();
                continue;
            }
            entityItem.onUpdate();
        }
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public TerrainItem getTerrainItem() {
        return this.terrainItem;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public World getWorld() {
        return this.world;
    }
}
