package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.game.resource.assets.models.basic.MeshHelper;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.brush.WorldBrush;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class BrushPlanePhysXObject extends PhysicsObject {
    private final WorldBrush worldBrush;

    public BrushPlanePhysXObject(SceneWorld sceneWorld, WorldItem worldItem, RenderData renderData) {
        this(sceneWorld, (WorldBrush) worldItem, renderData);
    }

    private BrushPlanePhysXObject(SceneWorld sceneWorld, WorldBrush worldBrush, RenderData renderData) {
        super(sceneWorld, worldBrush, renderData);
        this.worldBrush = worldBrush;
    }

    protected void setModel() {
        Plane4dBrush plane4dBrush = (Plane4dBrush) this.getWorldBrush();
        this.mesh = MeshHelper.generatePlane3DMesh(plane4dBrush.getVertices()[0], plane4dBrush.getVertices()[1], plane4dBrush.getVertices()[2], plane4dBrush.getVertices()[3]);
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
    }

    public WorldBrush getWorldBrush() {
        return this.worldBrush;
    }
}
