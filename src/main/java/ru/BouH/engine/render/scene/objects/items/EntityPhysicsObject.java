package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class EntityPhysicsObject extends WorldObject {
    private final PhysEntity physEntity;

    public EntityPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem, RenderData renderData) {
        this(sceneWorld, (PhysEntity) worldItem, renderData);
    }

    private EntityPhysicsObject(SceneWorld sceneWorld, PhysEntity physEntity, RenderData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public PhysEntity getEntity() {
        return this.physEntity;
    }
}
