package ru.BouH.engine.render.scene.render.entities.init;

import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.proxy.lights.Light;
import ru.BouH.engine.render.scene.render.IRenderItem;
import ru.BouH.engine.render.scene.render.models.entity.EntityModel;
import ru.BouH.engine.render.scene.world.RenderWorld;

public class RenderEntity implements IRenderItem {
    private final RenderWorld renderWorld;
    private Light light;
    private EntityModel entityModel;
    private final PhysEntity physEntity;

    public RenderEntity(RenderWorld renderWorld, PhysEntity physEntity, EntityModel entityModel) {
        this.renderWorld = renderWorld;
        this.entityModel = entityModel;
        this.physEntity = physEntity;
    }

    public PhysEntity getEntity() {
        return this.physEntity;
    }

    public void onUpdate() {

    }

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
    }

    public EntityModel getEntityModel() {
        return this.entityModel;
    }

    public RenderWorld getRenderWorld() {
        return this.renderWorld;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public Light getLight() {
        if (this.light != null) {
            return this.light;
        }
        return this.physEntity.getLight();
    }
}
