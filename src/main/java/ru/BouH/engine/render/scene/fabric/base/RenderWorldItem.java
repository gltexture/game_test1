package ru.BouH.engine.render.scene.fabric.base;

import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class RenderWorldItem implements RenderFabric {
    @Override
    public void onStartRender(IRenderObject renderItem) {
        PhysicsObject entityItem = (PhysicsObject) renderItem;
        if (entityItem.isHasModel()) {
            entityItem.getModel3D().getFormat().getPosition().set(entityItem.getWorldItem().getPosition());
        }
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
        PhysicsObject entityItem = (PhysicsObject) renderItem;
        if (entityItem.isHasModel()) {
            SceneWorld.toCleanSet.add(entityItem.getModel3D());
        }
    }
}
