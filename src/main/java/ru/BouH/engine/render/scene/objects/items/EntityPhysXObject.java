package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.primitive_forms.CollisionOBBoxForm;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class EntityPhysXObject extends PhysXObject {
    private final PhysEntity physEntity;

    public EntityPhysXObject(SceneWorld sceneWorld, WorldItem worldItem, RenderData renderData) {
        this(sceneWorld, (PhysEntity) worldItem, renderData);
    }

    private EntityPhysXObject(SceneWorld sceneWorld, PhysEntity physEntity, RenderData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    protected void setModel() {
        if (this.getRenderData() instanceof RenderModeledData) {
            RenderModeledData renderModeledData = (RenderModeledData) this.getRenderData();
            MeshModel meshModel = renderModeledData.getMeshModel();
            this.model3D = new Model3D(meshModel);
        }
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.physEntity.isDead()) {
            this.setDead();
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
    }

    public PhysEntity getEntity() {
        return this.physEntity;
    }
}
