package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.preforms.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class PhysicsPlaneObject extends PhysicsObject {
    private final Plane4dBrush physEntity;

    public PhysicsPlaneObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        this(sceneWorld, (Plane4dBrush) worldItem, renderData);
    }

    private PhysicsPlaneObject(SceneWorld sceneWorld, Plane4dBrush physEntity, RenderObjectData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    protected void initModel() {
        Plane4dBrush plane4dBrush = this.getEntity();
        this.setModel(MeshHelper.generatePlane3DModel(plane4dBrush.getVertices()[0], plane4dBrush.getVertices()[1], plane4dBrush.getVertices()[2], plane4dBrush.getVertices()[3]));
    }

    public Plane4dBrush getEntity() {
        return this.physEntity;
    }
}
