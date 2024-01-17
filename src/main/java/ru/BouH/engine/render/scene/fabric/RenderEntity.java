package ru.BouH.engine.render.scene.fabric;

import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.items.EntityObject;

public class RenderEntity extends RenderWorldItem {
    public RenderEntity() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        EntityObject entityObject = (EntityObject) renderItem;
        if (entityObject.isHasModel()) {
            Scene.renderEntity(entityObject);
        }
    }
}
