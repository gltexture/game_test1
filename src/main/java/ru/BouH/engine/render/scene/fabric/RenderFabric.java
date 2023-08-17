package ru.BouH.engine.render.scene.fabric;

import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.IRenderObject;

public interface RenderFabric {
    void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem);

    void onStartRender(IRenderObject renderItem);

    void onStopRender(IRenderObject renderItem);
}
