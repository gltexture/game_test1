package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class WorldObject extends PhysXObject {

    public WorldObject(SceneWorld sceneWorld, WorldItem worldItem, RenderData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    protected Model3D buildObjectModel() {
        if (this.getRenderData() instanceof RenderModeledData) {
            RenderModeledData renderModeledData = (RenderModeledData) this.getRenderData();
            MeshModel meshModel = renderModeledData.getMeshModel();
            return new Model3D(meshModel);
        }
        return null;
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
