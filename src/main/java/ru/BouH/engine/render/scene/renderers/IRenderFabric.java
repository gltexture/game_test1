package ru.BouH.engine.render.scene.renderers;

import ru.BouH.engine.render.scene.renderers.items.IRenderItem;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;

public interface IRenderFabric {
    void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderItem iRenderItem);

    void onStartRender(IRenderItem iRenderItem);

    void onStopRender(IRenderItem iRenderItem);
}
