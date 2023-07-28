package ru.BouH.engine.render.scene.renderers.items.entity;

import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;
import ru.BouH.engine.render.scene.renderers.items.models.box.CollisionBoxForm;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class EntityItem implements IRenderItem {
    private final SceneWorld sceneWorld;
    private final PhysEntity physEntity;
    private final EntityModel entityModel;
    private CollisionBoxForm collisionBoxForm;
    private boolean hasRender;

    public EntityItem(SceneWorld sceneWorld, PhysEntity physEntity, EntityModel entityModel) {
        this.sceneWorld = sceneWorld;
        this.entityModel = entityModel;
        this.physEntity = physEntity;
        this.collisionBoxForm = new CollisionBoxForm(physEntity);
        this.hasRender = entityModel != null;
    }

    public EntityItem(SceneWorld sceneWorld, PhysEntity physEntity) {
        this(sceneWorld, physEntity, null);
    }

    public boolean isHasRender() {
        return this.hasRender;
    }

    public PhysEntity getEntity() {
        return this.physEntity;
    }

    public void onUpdate() {
        if (this.entityModel != null && !this.hasRender) {
            this.hasRender = true;
        }
    }

    public CollisionBoxForm getCollisionBoxForm() {
        return this.collisionBoxForm;
    }

    public EntityModel getEntityModel() {
        return this.entityModel;
    }

    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }

    @Override
    public IRenderFabric renderFabric() {
        return this.getEntityModel().getPropForm().getiRenderFabric();
    }
}
