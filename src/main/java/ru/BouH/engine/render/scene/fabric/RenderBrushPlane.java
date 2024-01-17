package ru.BouH.engine.render.scene.fabric;

import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.EntityObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsPlaneObject;

public class RenderBrushPlane extends RenderWorldItem {
    public RenderBrushPlane() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        PhysicsPlaneObject entityObject = (PhysicsPlaneObject) renderItem;
        if (entityObject.isHasModel()) {
            Scene.renderEntity(entityObject);
        }
    }
}
