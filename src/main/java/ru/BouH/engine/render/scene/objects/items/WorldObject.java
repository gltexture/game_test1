package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class WorldObject extends PhysicsObject {

    public WorldObject(SceneWorld sceneWorld, WorldItem worldItem, RenderData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    protected void setModel() {
        if (this.getRenderData() instanceof RenderModeledData) {
            RenderModeledData renderModeledData = (RenderModeledData) this.getRenderData();
            this.mesh = renderModeledData.getMeshModel();
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }
}
